package com.neo.sk.breaker.front.snake

import com.neo.sk.breaker.front.snake.NetGameHolder.{bounds, canvasBoundary, canvasUnit}
import org.scalajs.dom.ext.Color
import com.neo.sk.breaker.snake._
import com.neo.sk.breaker.snake.Protocol.GridDataSync
import scala.math._
import org.scalajs.dom
import org.scalajs.dom.html.{Canvas, Image}



class Draw(ctx: dom.CanvasRenderingContext2D,canvas: Canvas) {
  private val window = Point(dom.document.documentElement.clientWidth - 12,dom.document.documentElement.clientHeight - 12 )
  private val textLineHeight = 14


  private val mapImg = dom.document.createElement("img").asInstanceOf[Image]
  mapImg.setAttribute("src","/breaker/static/img/background.jpeg")
  private val block1 = dom.document.createElement("img").asInstanceOf[Image]
  block1.setAttribute("src","/breaker/static/img/block1.png")
  private val block2 = dom.document.createElement("img").asInstanceOf[Image]
  block2.setAttribute("src","/breaker/static/img/block2.png")
  private val block3 = dom.document.createElement("img").asInstanceOf[Image]
  block3.setAttribute("src","/breaker/static/img/block3.png")
  private val stick1 = dom.document.createElement("img").asInstanceOf[Image]
  stick1.setAttribute("src","/breaker/static/img/stick1.png")
  private val stick2 = dom.document.createElement("img").asInstanceOf[Image]
  stick2.setAttribute("src","/breaker/static/img/stick2.png")
  private val stick3 = dom.document.createElement("img").asInstanceOf[Image]
  stick3.setAttribute("src","/breaker/static/img/stick3.png")
  private val ballImg = dom.document.createElement("img").asInstanceOf[Image]

  def drawGameOn(): Unit = {
    ctx.fillStyle = Color.Black.toString()
    ctx.fillRect(0, 0, canvas.width, canvas.height)
  }

  def drawGameOff(firstCome: Boolean): Unit = {
    ctx.fillStyle = Color.Black.toString()
    ctx.fillRect(start.x, start.y, bounds.x * canvasUnit, bounds.y * canvasUnit)
    ctx.fillStyle = "rgb(250, 250, 250)"
    if (firstCome) {
      ctx.font = "36px Helvetica"
      ctx.fillText("Welcome.", 150, 180)
    } else {
      ctx.font = "36px Helvetica"
      ctx.fillText("Ops, connection lost.", 150, 180)
    }
  }

  def drawBackground() = {
    ctx.drawImage(mapImg, 0, 0, canvasBoundary.x, canvasBoundary.y)
  }

  def drawGrid(uid: Long, data: GridDataSync): Unit = {

    val blocks = data.blockDetails
    val sticks = data.stickDetails
    val balls = data.ballDetails

    blocks.foreach{ case Bk(score, x, y) =>
      val blockImg = score match {
        case 10 => block3
        case 5 => block2
        case _ => block1
      }
      ctx.drawImage(blockImg, x * canvasUnit, y * canvasUnit, b_width * canvasUnit, b_height * canvasUnit)
    }

    sticks.foreach { case Sk(id, position, length, color) =>
      val stickImg = color match {
        case 1 => stick1
        case 2 => stick2
        case 3 => stick3
        case _ => stick1
      }
      ctx.drawImage(stickImg, position.x * canvasUnit , position.y * canvasUnit , length * canvasUnit , 2 * canvasUnit )
    }

    balls.foreach{ case Bl(_, position, color, _, _) =>
      ballImg.setAttribute("src", s"/breaker/static/img/ball${color}.png")
      ctx.drawImage(ballImg, (position.x - radius) * canvasUnit, (position.y - radius) * canvasUnit, 2 * canvasUnit, 2 * canvasUnit)
    }
  }

  def drawRank(rank: List[Score]) {

    ctx.fillStyle = "rgb(250, 250, 250)"
    ctx.textAlign = "left"
    ctx.textBaseline = "top"

    val leftBegin = 10
    val rightBegin = canvasBoundary.x.toInt - 150

    ctx.font = "12px Helvetica"
    val currentRankBaseLine = 5
    var index = 0
    drawTextLine(s" --- Current Rank --- ", leftBegin, index, currentRankBaseLine)
    rank.foreach { score =>
      index += 1
      drawTextLine(s"[$index]: ${score.n.+("   ").take(5)} score=${score.s} }", leftBegin, index, currentRankBaseLine)
    }

  }

  def drawTextLine(str: String, x: Int, lineNum: Int, lineBegin: Int = 0) = {
    ctx.fillText(str, x, (lineNum + lineBegin - 1) * textLineHeight)
  }
}
