package com.neo.sk.breaker.front.snake

import com.neo.sk.breaker.snake.Protocol.GridDataSync
import com.neo.sk.breaker.snake._
import org.scalajs.dom
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

object NetGameHolder {


  val canvasUnit = 10
  val canvasBoundary = Point(dom.window.innerWidth.toFloat,dom.window.innerHeight.toFloat)
  val bounds = canvasBoundary / canvasUnit

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
    KeyCode.F2
  )

  object MyColors {
    val myHeader = "#FF0000"
    val myBody = "#FFFFFF"
    val otherHeader = Color.Blue.toString()
    val otherBody = "#696969"
  }

  private[this] lazy val nameField = dom.document.getElementById("name").asInstanceOf[HTMLInputElement]
  private[this] lazy val joinButton = dom.document.getElementById("join").asInstanceOf[HTMLButtonElement]
  private[this] lazy val canvas = dom.document.getElementById("GameView").asInstanceOf[Canvas]
  private[this] lazy val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
  private[this] val drawGame = new Draw(ctx, canvas)

  def start(name: String): Unit = {
    drawGame.drawGameOff(firstCome)
    canvas.width = canvasBoundary.x.toInt
    canvas.height = canvasBoundary.y.toInt

    myName = name
    joinGame(name)

    dom.window.setInterval(() => gameLoop(), Protocol.frameRate)
  }




  def gameLoop(): Unit = {
    if (wsSetup) {
      if (!justSynced) {
        update()
      } else {
        justSynced = false
      }
    }
    draw()
  }

  def update(): Unit = {
    grid.update()
  }

  def draw(): Unit = {
    if (wsSetup) {
      val data = grid.getGridData
      drawGame.drawGrid(myId, data)
      drawGame.drawRank(currentRank)
      data.snakes.find(_.id == myId) match {
        case Some(snake) =>
          firstCome = false
        case None =>
          if (firstCome) {
            ctx.font = "36px Helvetica"
            ctx.fillText("Please wait.", 150, 180)
          } else {
            ctx.font = "36px Helvetica"
            ctx.fillText("Ops, Press Space Key To Restart!", 150, 180)
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
      joinButton.disabled = false
      wsSetup = false
      nameField.focus()

    }


    import io.circe.generic.auto._
    import io.circe.parser._
    gameStream.onmessage = { (event: MessageEvent) =>
      //val wsMsg = read[Protocol.GameMessage](event.data.toString)
      val wsMsg = decode[Protocol.GameMessage](event.data.toString).right.get
      wsMsg match {
        case Protocol.Id(id) => myId = id
        case Protocol.TextMsg(message) => writeToArea(s"MESSAGE: $message")
        case Protocol.NewSnakeJoined(id, user) => writeToArea(s"$user joined!")
        case Protocol.SnakeLeft(id, user) => writeToArea(s"$user left!")
        case a@Protocol.SnakeAction(id, keyCode, frame) =>
          if (frame > grid.frameCount) {
            //writeToArea(s"!!! got snake action=$a whem i am in frame=${grid.frameCount}")
          } else {
            //writeToArea(s"got snake action=$a")
          }
          grid.addActionWithFrame(id, keyCode, frame)

        case Protocol.Ranks(current, history) =>
          //writeToArea(s"rank update. current = $current") //for debug.
          currentRank = current
          historyRank = history
        case Protocol.FeedApples(apples) =>
          writeToArea(s"apple feeded = $apples") //for debug.
          grid.grid ++= apples.map(a => Point(a.x, a.y) -> Apple(a.score, a.life))
        case data: Protocol.GridDataSync =>
          //writeToArea(s"grid data got: $msgData")
          //TODO here should be better code.
          grid.actionMap = grid.actionMap.filterKeys(_ > data.frameCount)
          grid.frameCount = data.frameCount
          grid.snakes = data.snakes.map(s => s.id -> s).toMap
          val appleMap = data.appleDetails.map(a => Point(a.x, a.y) -> Apple(a.score, a.life)).toMap
          val bodyMap = data.bodyDetails.map(b => Point(b.x, b.y) -> Body(b.id, b.life)).toMap
          val gridMap = appleMap ++ bodyMap
          grid.grid = gridMap
          justSynced = true
        //drawGrid(msgData.uid, data)
        case Protocol.NetDelayTest(createTime) =>
          val receiveTime = System.currentTimeMillis()
          val m = s"Net Delay Test: createTime=$createTime, receiveTime=$receiveTime, twoWayDelay=${receiveTime - createTime}"
          writeToArea(m)
      }
    }


    gameStream.onclose = { (event: Event) =>
      drawGame.drawGameOff(firstCome)
      playground.insertBefore(p("Connection to game lost. You can try to rejoin manually."), playground.firstChild)
      joinButton.disabled = false
      wsSetup = false
      nameField.focus()
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
