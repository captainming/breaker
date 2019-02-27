package com.neo.sk.breaker.front.breaker

import com.neo.sk.breaker.breaker.Protocol._
import com.neo.sk.breaker.breaker._
import com.neo.sk.breaker.front.pages.WelcomePage
import com.neo.sk.frontUtils.MiddleBufferInJs
import mhtml.Var
import org.scalajs.dom
import org.scalajs.dom.ext
import org.scalajs.dom.ext.{Color, KeyCode}
import org.scalajs.dom.html.{Document => _, _}
import org.scalajs.dom.raw._
import com.neo.sk.frontUtils.byteObject.ByteObject._
import com.neo.sk.frontUtils.byteObject.decoder

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.typedarray.ArrayBuffer

/**
  * User: Taoz
  * Date: 9/1/2016
  * Time: 12:45 PM
  */
/**
  * used by ltm on 2/16/2019
  */

class NetGameHolder(id: Long, name: String, roomId: Long) {
  private var logicFrameTime = System.currentTimeMillis()
  var nextFrame = 0

  val canvasUnit = 10
  val bounds = Point(Boundary.w, Boundary.h)
  val canvasBoundary = bounds * canvasUnit

  var currentRank = List.empty[Score]
  var historyRank = List.empty[Score]
  var myId = -1l
  var myName = "test"

  var syncFlag = false
  var justSynced = false
  var syncGridData: scala.Option[Protocol.GridDataSync] = None
  var dataDeposit = GridDataSync(0L, List.empty[Breaker], List.empty[Bk], List.empty[Sk], List.empty[Bl])

  var gameOver = false
  var gameBreak = false
  var winner = 0l
  var gameResultInfo = List.empty[GameResultInfo]
  var countDownFlag = false
  var countDownNum = ""

  var grid = new GridOnClient(bounds)
  var firstCome = true
  var wsSetup = false
  var actionList = Map[Long, (Int, Long)]()
  val watchKeys = Set(
    KeyCode.Space,
    KeyCode.Left,
    KeyCode.Up,
    KeyCode.Right,
    KeyCode.Down,
    KeyCode.F2,
    KeyCode.Escape,
    KeyCode.Enter
  )

  val aboutCanvas = dom.window.document.getElementById("aboutCanvas").asInstanceOf[HTMLDivElement]
  //左侧栏
  val idContainerDiv = dom.window.document.getElementById("idContainer").asInstanceOf[HTMLDivElement]
  val nameContainerDiv = dom.window.document.getElementById("nameContainer").asInstanceOf[HTMLDivElement]
  val roomIdDiv = dom.window.document.getElementById("roomId").asInstanceOf[HTMLDivElement]
  val landlordNumDiv = dom.window.document.getElementById("landlordNum").asInstanceOf[HTMLDivElement]
  //右侧栏
  val gameTimeDiv = dom.window.document.getElementById("gameTime").asInstanceOf[HTMLDivElement]
  //对战双方昵称
  val myname = dom.window.document.getElementById("myName").asInstanceOf[HTMLDivElement]
  val otherName = dom.window.document.getElementById("otherName").asInstanceOf[HTMLDivElement]
  //退出房间按钮
  val leaveRoom = dom.window.document.getElementById("leaveRoom").asInstanceOf[HTMLDivElement]

  val infoFlag = Var(0)
  var infoMsg = ""
  val canvas = dom.window.document.getElementById("GameView").asInstanceOf[HTMLCanvasElement]
  val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
  private[this] val drawGame = new Draw(ctx, canvas)

  val infoModal = infoFlag.map {
    case 0 => <div></div>
    case 1 =>
      <div style="width: 100%; height: 100%; position: fixed; top: 0px; background-color: rgba(0,0,0,0.3); z-index: 1;">
        <div style="width: 900px; height: 400px; margin: 80px auto 0px auto; border-radius: 8px; font-size: 18px; background-color: white; align-items: center; display: flex; flex-direction: column; justify-content: center; z-index: 2; ">
          <div style="color: black;">
            {infoMsg}
          </div>
          <div style="margin-top: 100px; display: flex;">
            <button type="button" style="width: 180px; height: 50px; border-radius: 4px; border: none; background-color: #225599; color: white; cursor: pointer; font-size: 16px;" onclick={() => {
              infoFlag := 0; canvas.focus()
            }}>留在房间</button>
            <button type="button" style="margin-left: 20px; width: 180px; height: 50px; border-radius: 4px; border: none; background-color: #225599; color: white; cursor: pointer; font-size: 16px;" onclick={() => {
              WelcomePage.showGame := 0
            }}>回到大厅</button>
          </div>
        </div>
      </div>
    case _ => <div></div>
  }
  def start(): xml.Node = {
    drawGame.drawGameOff(firstCome)
    canvas.width = canvasBoundary.x.toInt
    canvas.height = canvasBoundary.y.toInt
    aboutCanvas.style.display = "block"

    myName = name
    joinGame()

    dom.window.setInterval(() => gameLoop(), Protocol.frameRate)
    nextFrame = dom.window.requestAnimationFrame(gameRender())
    <div>
      {infoModal}
    </div>
  }


  def gameRender(): Double => Unit = {
    d =>
      draw()
      nextFrame = dom.window.requestAnimationFrame(gameRender())
  }

  def gameLoop(): Unit = {
    if (wsSetup) {
      if (!justSynced) {
        update()
      } else {
        if (syncGridData.nonEmpty) {
          initSyncData(syncGridData.get)
          syncGridData = None
        }
        justSynced = false
      }
    }
  }

  def update(): Unit = {
    grid.update()
  }

  def draw(): Unit = {
    if (wsSetup) {
      val data = grid.getGridData
      drawGame.drawBackground()
      drawGame.drawGrid(myId, data)
      drawGame.drawRank(currentRank)
      if (winner == myId){
        drawGame.drawWait(2)
      }
      data.breakers.find(_.id == myId) match {
        case Some(breaker) =>
          firstCome = false
        case None =>
          if (firstCome) {
            drawGame.drawWait(3)
          } else {
            drawGame.drawWait(1)
          }
      }

    } else {
      drawGame.drawGameOff(firstCome)
    }
  }

  def initSyncData(data: GridDataSync): Unit = {
    if (syncFlag || grid.frameCount < data.frameCount){
      syncFlag = false
      grid.frameCount = data.frameCount
      grid.breakers = data.breakers.map(s => s.id -> s).toMap
      val blockMap = data.blockDetails.map(c => Point(c.x, c.y) -> Block(c.score)).toMap
      val stickMap = data.stickDetails.map(d => Point(d.position.x, d.position.y) -> Stick(d.id, d.length, d.color)).toMap
      val ballMap = data.ballDetails.map(e => Point(e.position.x, e.position.y) -> Ball(e.id, e.color, e.direction, e.speed)).toMap
      val gridMap = blockMap ++ stickMap ++ ballMap
      grid.grid = gridMap
      grid.actionMap = grid.actionMap.filterKeys(_ >= data.frameCount)
    } else {
      println("丢帧！！！同步前,后台发过来的帧号为:" + data.frameCount + "前端此时的帧号为:" + grid.frameCount)
    }
  }

  val sendBuffer = new MiddleBufferInJs(409600)
  def joinGame(): Unit = {

    val gameStream = new WebSocket(getWebSocketUri(dom.document, id, name, roomId))
    gameStream.onopen = { (event0: Event) =>
      drawGame.drawGameOn()
      wsSetup = true
      canvas.focus()
      canvas.onkeydown = {
        (e: dom.KeyboardEvent) => {
          if (watchKeys.contains(e.keyCode)) {
            println(s"key down: [${e.keyCode}]")
            if (e.keyCode == KeyCode.Escape){
              infoMsg = "退出房间？"
              infoFlag := 1
            }
            else {
              val msg: Protocol.MsgFromFront = if (e.keyCode == KeyCode.F2) {
                NetTest(myId, System.currentTimeMillis())
              } else {
                val emptyFrame = grid.addActionWithFrame(myId, e.keyCode, grid.frameCount)
                val timeStamp = System.currentTimeMillis()
                actionList += timeStamp -> (e.keyCode, emptyFrame)
                KeyWithFrame(myId, e.keyCode, emptyFrame, timeStamp)
//                gameStream.send(e.keyCode.toString)
//                grid.addAction(myId, e.keyCode)
              }
              msg.fillMiddleBuffer(sendBuffer) //encode msg
              val ab: ArrayBuffer = sendBuffer.result() //get encoded data.
              gameStream.send(ab) // send data.
              e.preventDefault()
            }
          }
        }
      }
      val tickTock = new Function0[Unit] {
        val msg: MsgFromFront = TickTock
        msg.fillMiddleBuffer(sendBuffer)
        val ab: ArrayBuffer = sendBuffer.result()
        def apply(): Unit = gameStream.send(ab)
      }
      dom.window.setInterval(tickTock, 20000)
      event0
    }

    gameStream.onerror = { (event: Event) =>
      drawGame.drawGameOff(firstCome)
      wsSetup = false

    }


    import io.circe.generic.auto._
    import io.circe.parser._
    gameStream.onmessage = { (event: MessageEvent) =>
      event.data match {
        case blobMsg: Blob =>
          val fr = new FileReader()
          fr.readAsArrayBuffer(blobMsg)
          fr.onloadend = { _: Event =>
            val buf = fr.result.asInstanceOf[ArrayBuffer] // read data from ws.
          val middleDataInJs = new MiddleBufferInJs(buf) //put data into MiddleBuffer
          val encodedData: Either[decoder.DecoderFailure, Protocol.GameMessage] = bytesDecode[Protocol.GameMessage](middleDataInJs) // get encoded data.
            encodedData match {
              case Right(data) =>
                data match {
                  case Protocol.UserInfo(id, roomIdT) =>
                    myId = id
                    idContainerDiv.innerHTML = "您的ID:" + id.toString
                    nameContainerDiv.innerHTML = "您的昵称:" + name
//                    myName.style= s"left:$start"+"px"
                    roomIdDiv.innerHTML = "房间号:" + roomIdT.toString
                    //显示退出房间按钮
                    leaveRoom.onclick = {e:MouseEvent => WelcomePage.showGame := 0}
                    leaveRoom.style = "display:block"

                  case Protocol.AllPlayerInfo(playerList) =>
                    playerList.foreach{ p =>
                      if(p.id != myId){
                        //显示对方的昵称
                        otherName.innerHTML = p.nameT
                      }
                    }

                  case Protocol.GameTime(time, countDownOrNot) =>
                    if (countDownOrNot) {
                      countDownFlag = true
                      countDownNum = time match {
                        case x => x
                      }
                    } else {
                      gameTimeDiv.innerHTML = time
                    }

                  case RoomInfo(num) =>
                    landlordNumDiv.innerHTML = "当前房间人数:" + num.toString

                  case Protocol.GameBreak =>
                    grid.init()
                    firstCome = false
                    gameOver = true
                    gameBreak = true
                    winner = myId
                    countDownNum = ""

                  case Protocol.GameOver(winnerId, gameResult) =>
                    grid.init()
                    gameOver = true
                    winner = winnerId
                    gameResultInfo = gameResult

                  case Protocol.GameInfo(errCode, msg) =>
                    errCode match {
                      case 101 => infoMsg = s"您要进的房间不存在 为您新创建了房间$msg"
                      case 102 => infoMsg = s"无可随机进入的房间 为您新创建了房间$msg"
                      case _ =>
                    }
                    infoFlag := 1

                  case Protocol.TextMsg(message) =>
                    println(s"MESSAGE: $message")

                  case a@Protocol.LandlordAction(id, keyCode, frame, timestamp) =>
                    if (id == myId) {
                      if (actionList.get(timestamp).isEmpty) {
                        //不在前端预执行之列
                        grid.addActionWithFrame(id, keyCode, frame)
                        if (frame <= grid.frameCount) { //回溯
                          //todo replay
                        }
                      } else {
                        if (frame == -1) {
                          //延迟丢弃该帧
                          grid.deleteActionWithFrame(id, actionList(timestamp)._2)
                          val tmp = grid
                          //                tmp.rePlay(actionList(timestamp)._2 - 1, grid.frameCount)
                          grid = tmp
                        }
                        actionList -= timestamp
                      }
                    } else {
                      grid.addActionWithFrame(id, keyCode, frame)
                      if (frame <= grid.frameCount) { //回溯
                        val tmp = grid
                        //              tmp.rePlay(frame - 1, grid.frameCount)
                        grid = tmp
                      }
                    }

                  case Protocol.Ranks(current) =>
                    currentRank = current

                  case diffData: Protocol.GridDataToSync =>
                    val data = diffData.data
                    syncGridData = Some(data)
                    syncFlag = diffData.flag
                    justSynced = true

                  case Protocol.NetDelayTest(createTime) =>
                    val receiveTime = System.currentTimeMillis()
                    val m = s"Net Delay Test: createTime=$createTime, receiveTime=$receiveTime, twoWayDelay=${receiveTime - createTime}"
                    println(m)
                }
              case Left(e) =>
                println(s"got error: ${e.message}")
            }
          }
      }
    }


    gameStream.onclose = { (event: Event) =>
      drawGame.drawGameOff(firstCome)
      wsSetup = false
    }

    def writeToArea(text: String): Unit ={

    }

  }

  def getWebSocketUri(document: Document, id:Long, nameOfChatParticipant: String, roomId: Long): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}/breaker/netSnake/join?id=$id&name=$nameOfChatParticipant&roomId=$roomId"
  }

  def p(msg: String) = {
    val paragraph = dom.document.createElement("p")
    paragraph.innerHTML = msg
    paragraph
  }


}
