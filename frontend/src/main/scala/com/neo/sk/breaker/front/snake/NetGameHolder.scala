package com.neo.sk.breaker.front.breaker

import com.neo.sk.breaker.breaker.Protocol.GridDataSync
import com.neo.sk.breaker.breaker._
import org.scalajs.dom
import org.scalajs.dom.ext
import org.scalajs.dom.ext.{Color, KeyCode}
import org.scalajs.dom.html.{Document => _, _}
import org.scalajs.dom.raw._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

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

  val grid = new GridOnClient(bounds)
  var firstCome = true
  var wsSetup = false
  var justSynced = false

  val watchKeys = Set(
    KeyCode.Space,
    KeyCode.Left,
    KeyCode.Up,
    KeyCode.Right,
    KeyCode.Down,
    KeyCode.F2,
    KeyCode.Enter
  )

  val canvas = dom.window.document.getElementById("GameView").asInstanceOf[HTMLCanvasElement]
  val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
  private[this] val drawGame = new Draw(ctx, canvas)

  def start(): xml.Node = {
    drawGame.drawGameOff(firstCome)
    canvas.width = canvasBoundary.x.toInt
    canvas.height = canvasBoundary.y.toInt

    myName = name
    joinGame(name)

    dom.window.setInterval(() => gameLoop(), Protocol.frameRate)
    nextFrame = dom.window.requestAnimationFrame(gameRender())
    <div>
      <canvas id ="GameView" tabindex="1"> </canvas>
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


  def joinGame(name: String): Unit = {
    val playground = dom.document.getElementById("playground")
    playground.innerHTML = s"Trying to join game as '$name'..."
    val gameStream = new WebSocket(getWebSocketUri(dom.document, name))
    gameStream.onopen = { (event0: Event) =>
      drawGame.drawGameOn()
      playground.insertBefore(p("Game connection was successful!"), playground.firstChild)
      wsSetup = true
      canvas.focus()
      canvas.onkeydown = {
        (e: dom.KeyboardEvent) => {
          println(s"keydown: ${e.keyCode}")
          if (watchKeys.contains(e.keyCode)) {
            println(s"key down: [${e.keyCode}]")
            if (e.keyCode == KeyCode.F2) {
              gameStream.send("T" + System.currentTimeMillis())
            } else {
              gameStream.send(e.keyCode.toString)
              grid.addAction(myId, e.keyCode)
            }
            e.preventDefault()
          }
        }
      }
      event0
    }

    gameStream.onerror = { (event: Event) =>
      drawGame.drawGameOff(firstCome)
      playground.insertBefore(p(s"Failed: code: ${event.`type`}"), playground.firstChild)
      wsSetup = false

    }


    import io.circe.generic.auto._
    import io.circe.parser._
    gameStream.onmessage = { (event: MessageEvent) =>
      //val wsMsg = read[Protocol.GameMessage](event.data.toString)
      val wsMsg = decode[Protocol.GameMessage](event.data.toString).right.get
      wsMsg match {
        case Protocol.Id(id) => myId = id
        case Protocol.TextMsg(message) => writeToArea(s"MESSAGE: $message")
        case Protocol.NewBreakerJoined(id, user) => writeToArea(s"$user joined!")
        case Protocol.BreakerLeft(id, user) => writeToArea(s"$user left!")
        case a@Protocol.BreakerAction(id, keyCode, frame) =>
          if (frame > grid.frameCount) {
            //writeToArea(s"!!! got breaker action=$a whem i am in frame=${grid.frameCount}")
          } else {
            //writeToArea(s"got breaker action=$a")
          }
          grid.addActionWithFrame(id, keyCode, frame)

        case Protocol.Ranks(current) =>
          //writeToArea(s"rank update. current = $current") //for debug.
          currentRank = current

        case Protocol.BlockInit(blocks) =>
          grid.grid ++= blocks.map(a => Point(a.x, a.y) -> Block(a.score))

        case data: Protocol.GridDataSync =>
          //writeToArea(s"grid data got: $msgData")
          //TODO here should be better code.
          grid.actionMap = grid.actionMap.filterKeys(_ > data.frameCount)
          grid.frameCount = data.frameCount
          grid.breakers = data.breakers.map(s => s.id -> s).toMap
          val blockMap = data.blockDetails.map(c => Point(c.x, c.y) -> Block(c.score)).toMap
          val stickMap = data.stickDetails.map(d => Point(d.position.x, d.position.y) -> Stick(d.id, d.length, d.color))
          val ballMap = data.ballDetails.map(e => Point(e.position.x, e.position.y) -> Ball(e.id, e.color, e.direction, e.speed))
          val gridMap = blockMap ++ stickMap ++ ballMap
          grid.grid = gridMap
          justSynced = true

        case Protocol.NetDelayTest(createTime) =>
          val receiveTime = System.currentTimeMillis()
          val m = s"Net Delay Test: createTime=$createTime, receiveTime=$receiveTime, twoWayDelay=${receiveTime - createTime}"
          writeToArea(m)
      }
    }


    gameStream.onclose = { (event: Event) =>
      drawGame.drawGameOff(firstCome)
      playground.insertBefore(p("Connection to game lost. You can try to rejoin manually."), playground.firstChild)
      wsSetup = false
    }

    def writeToArea(text: String): Unit =
      playground.insertBefore(p(text), playground.firstChild)
  }

  def getWebSocketUri(document: Document, nameOfChatParticipant: String): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}/breaker/netSnake/join?name=$nameOfChatParticipant"
  }

  def p(msg: String) = {
    val paragraph = dom.document.createElement("p")
    paragraph.innerHTML = msg
    paragraph
  }


}
