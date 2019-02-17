package com.neo.sk.breaker.front.snake

import com.neo.sk.breaker.front.snake.NetGameHolder.{MyColors, bounds, canvasBoundary, canvasUnit}
import org.scalajs.dom.ext.Color
import com.neo.sk.breaker.snake.{Ap, Bd, Point, Score}
import com.neo.sk.breaker.snake.Protocol.GridDataSync
import org.scalajs.dom
import org.scalajs.dom.html.Canvas


class Draw(ctx: dom.CanvasRenderingContext2D,canvas: Canvas) {
  private val window = Point(dom.document.documentElement.clientWidth - 12,dom.document.documentElement.clientHeight - 12 )
  private val textLineHeight = 14

  def drawGameOn(): Unit = {
    ctx.fillStyle = Color.Black.toString()
    ctx.fillRect(0, 0, canvas.width, canvas.height)
  }

  def drawGameOff(firstCome: Boolean): Unit = {
    ctx.fillStyle = Color.Black.toString()
    ctx.fillRect(0, 0, bounds.x * canvasUnit, bounds.y * canvasUnit)
    ctx.fillStyle = "rgb(250, 250, 250)"
    if (firstCome) {
      ctx.font = "36px Helvetica"
      ctx.fillText("Welcome.", 150, 180)
    } else {
      ctx.font = "36px Helvetica"
      ctx.fillText("Ops, connection lost.", 150, 180)
    }
  }

  def drawGrid(uid: Long, data: GridDataSync): Unit = {

    ctx.fillStyle = Color.Black.toString()
    ctx.fillRect(0, 0, window.x, window.y)

    val snakes = data.snakes
    val bodies = data.bodyDetails
    val apples = data.appleDetails

    ctx.fillStyle = MyColors.otherBody
    bodies.foreach { case Bd(id, life, x, y) =>
      //println(s"draw body at $p body[$life]")
      if (id == uid) {
        ctx.save()
        ctx.fillStyle = MyColors.myBody
        ctx.fillRect(x * canvasUnit + 1, y * canvasUnit + 1, canvasUnit - 1, canvasUnit - 1)
        ctx.restore()
      } else {
        ctx.fillRect(x * canvasUnit + 1, y * canvasUnit + 1, canvasUnit - 1, canvasUnit - 1)
      }
    }

    apples.foreach { case Ap(score, life, x, y) =>
      ctx.fillStyle = score match {
        case 10 => Color.Yellow.toString()
        case 5 => Color.Blue.toString()
        case _ => Color.Red.toString()
      }
      ctx.fillRect(x * canvasUnit + 1, y * canvasUnit + 1, canvasUnit - 1, canvasUnit - 1)
    }

    ctx.fillStyle = MyColors.otherHeader
    snakes.foreach { snake =>
      val id = snake.id
      val x = snake.header.x
      val y = snake.header.y
      if (id == uid) {
        ctx.save()
        ctx.fillStyle = MyColors.myHeader
        ctx.fillRect(x * canvasUnit + 2, y * canvasUnit + 2, canvasUnit - 4, canvasUnit - 4)
        ctx.restore()
      } else {
        ctx.fillRect(x * canvasUnit + 2, y * canvasUnit + 2, canvasUnit - 4, canvasUnit - 4)
      }
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
      drawTextLine(s"[$index]: ${score.n.+("   ").take(5)} kill=${score.k} len=${score.l}", leftBegin, index, currentRankBaseLine)
    }

  }

  def drawTextLine(str: String, x: Int, lineNum: Int, lineBegin: Int = 0) = {
    ctx.fillText(str, x, (lineNum + lineBegin - 1) * textLineHeight)
  }
}
