package com.neo.sk.breaker.breaker

import java.awt.event.KeyEvent

import com.neo.sk.breaker.breaker.BkMap

import scala.collection.mutable
import scala.math._
import scala.util.Random


/**
  * User: Taoz
  * Date: 9/1/2016
  * Time: 5:34 PM
  */
/**
  * used by ltm on 2/19/2019
  */
trait Grid {

  val boundary: Point

  def debug(msg: String): Unit
  def info(msg: String): Unit

  val random = new Random(System.nanoTime())


  val defaultLength = 10
  val appleNum = 6
  val appleLife = 50
  val historyRankLength = 5

  var frameCount = 0l
  var map = 1
  var grid = Map[Point, Spot]()
  var scoreMap = mutable.HashMap.empty[Long, Int]
  var snakes = Map.empty[Long, SkDt]
  var breakers = Map.empty[Long, Breaker]
  var actionMap = Map.empty[Long, Map[Long, Int]]
  var blockNum = 0

  def init() = {
    actionMap = Map.empty[Long, Map[Long, Int]]
    scoreMap = mutable.HashMap.empty[Long, Int]
    grid = Map.empty[Point, Spot]
  }

  def removeBreaker(id: Long) = {
    val s = breakers.get(id)
    if (s.isDefined) {
      breakers -= id
    }
    s
  }

  def genBlocks() = {
    val blocks = BkMap.BlockMap(map)
    grid ++= blocks.map ( b =>
      Point(b.x, b.y) -> Block(b.score)
    )
    blockNum = blocks.size
  }

  def addAction(id: Long, keyCode: Int) = {
    addActionWithFrame(id, keyCode, frameCount)
  }

  def addActionWithFrame(id: Long, keyCode: Int, frame: Long) = {
    val map = actionMap.getOrElse(frame, Map.empty)
    val tmp = map + (id -> keyCode)
    actionMap += (frame -> tmp)
  }


  def update() = {
    updateBreakers()
    updateSpots()
    actionMap -= frameCount
    frameCount += 1
  }


  private[this] def updateSpots() = {
    debug(s"grid: ${grid.mkString(";")}")
    grid = grid.filter { case (p, spot) =>
      spot match {
        case Block(_) => true
        case Stick(_, _, _)  => true
        case Ball(_, _, _, _) => true
        case _ => false
      }
    }

  }

  def updateABreaker(breaker: Breaker, actMap: Map[Long, Int]) = {
    val id = breaker.id
    val keyCode = actMap.get(id)
    println(s" breaker[${id}] feels key: $keyCode  at frame = $frameCount")
    //update stick
    val keyDirection = keyCode match {
      case Some(KeyEvent.VK_LEFT) => Point(-1, 0)
      case Some(KeyEvent.VK_RIGHT) => Point(1, 0)
      case _ => Point(0, 0)
    }
    var newHeader = (keyDirection * 5 + breaker.stick.position)
    if (newHeader.x + 15 <= 0  || newHeader.x >= boundary.x - 15)
      newHeader = breaker.stick.position

    //update ball
    var direction = breaker.ball.direction
    var newScore = breaker.score
    if (direction == Point(0, 0)){
      direction = keyCode match {
        case Some(13) => Point(1, -1)
        case _ => Point(0, 0)
      }
    }

    val newPosition = breaker.ball.position + getDirection(direction) * breaker.ball.speed
    var impact = 0
    if (newPosition.x >= boundary.x - radius || newPosition.x <= 0 + radius) impact = 1
    if (newPosition.y <= 0) impact =2
    if (newPosition.y >= boundary.y) impact = 3
    grid.filter{ case (p, s) =>
      s match {
        case Block(_) => true
        case Stick(id, _, _) => true
        case _ => false
      }
    }.foreach {
      case (p, Block(s)) =>
        if (judgeFlied(p, b_height, b_width, newPosition)) {
          impact = 2
          newScore += s
          grid -= p
          scoreMap(id) += s
          blockNum -= 1
        }
      case (p, Stick(_, length, _)) =>
        println("find stick")
        if (judgeFlied(p, 2, length, newPosition)) {
          impact = 2
        }
      case _ =>
    }
    grid -= breaker.ball.position
    grid -= breaker.stick.position

    if (impact != 3) {
      val newDirection = impact match {
        case 1 => Point(-direction.x, direction.y)
        case 2 => Point(direction.x, -direction.y)
        case _ => direction
      }
      val newBall = breaker.ball.copy(position = newPosition, direction = newDirection)
      val newStick = breaker.stick.copy(position = newHeader)
      Right(breaker.copy(stick = newStick, ball = newBall, score = newScore))
    }
    else{
      Left(id)
    }

  }


  private[this] def updateBreakers() = {
    var updatedBreakers = List.empty[Breaker]
    val acts = actionMap.getOrElse(frameCount, Map.empty[Long, Int])
    breakers.values.map(updateABreaker(_, acts)).foreach {
      case Right(s) => updatedBreakers ::= s
      case Left(id) => removeBreaker(id)
    }

    grid ++= updatedBreakers.map { s =>
      s.stick.position -> Stick(s.id, s.stick.length, s.stick.color)
    }
    grid ++= updatedBreakers.map { s =>
      s.ball.position -> Ball(s.id, s.ball.color, s.ball.direction, s.ball.speed)
    }
    breakers = updatedBreakers.map(s => (s.id, s)).toMap
  }




  def getDirection(d: Point) = {
    val length = sqrt(d.x * d.x + d.y * d.y).toFloat
    val p = if (length != 0) d / length else d
    p
  }

  def judgeFlied(head: Point, h: Double, w: Double, p: Point): Boolean = {
    if (p.x + radius >= head.x && p.x - radius <= head.x + w && p.y + radius >= head.y && p.y - radius <= head.y + h) true
    else false
  }

  def updateAndGetGridData() = {
    update()
    getGridData
  }

  def getGridData = {

    var blockDetails: List[Bk] = Nil
    var stickDetails: List[Sk] = Nil
    var ballDetails: List[Bl] = Nil

    grid.foreach {
      case (p, Block(score)) => blockDetails ::= Bk(score, p.x, p.y)
      case (p, Stick(id, length, color)) => stickDetails ::= Sk(id, p, length, color)
      case (p, Ball(id, color, direction, speed)) => ballDetails ::= Bl(id, p, color, direction, speed)
      case _ =>
    }
    Protocol.GridDataSync(
      frameCount,
      breakers.values.toList,
      blockDetails,
      stickDetails,
      ballDetails
    )
  }

  def randomEmptyPoint(): Point = {
    var p = Point(random.nextInt(boundary.x.toInt), random.nextInt(boundary.y.toInt))
    while (grid.contains(p)) {
      p = Point(random.nextInt(boundary.x.toInt), random.nextInt(boundary.y.toInt))
    }
    p
  }

}
