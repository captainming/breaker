package com.neo.sk.breaker.snake

import java.awt.event.KeyEvent

import com.neo.sk.hiStream.snake.BkMap

import scala.util.Random


/**
  * User: Taoz
  * Date: 9/1/2016
  * Time: 5:34 PM
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
  var room = 1
  var grid = Map[Point, Spot]()
  var snakes = Map.empty[Long, SkDt]
  var breakers = Map.empty[Long, Breaker]
  var actionMap = Map.empty[Long, Map[Long, Int]]


  def removeSnake(id: Long): Option[SkDt] = {
    val r = snakes.get(id)
    if (r.isDefined) {
      snakes -= id
    }
    r
  }

  def removeBreaker(id: Long) = {
    val s = breakers.get(id)
    if (s.isDefined) {
      breakers -= id
    }
  }

  def genBlocks() = {
    val blocks = BkMap.BlockMap(room)
    grid ++= blocks.map ( b =>
      Point(b.x, b.y) -> Block(b.score)
    )
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

  def feedApple(appleCount: Int): Unit

  private[this] def updateSpots() = {
    debug(s"grid: ${grid.mkString(";")}")
    var appleCount = 0
    grid = grid.filter { case (p, spot) =>
      spot match {
        case Block(_) => true
        case Ball(_, _, _, _) => true
        case Stick(id, _, _) if breakers.contains(id) => true
        case _ => false
      }
    }

    feedApple(appleCount)
  }


  def randomEmptyPoint(): Point = {
    var p = Point(random.nextInt(boundary.x.toInt), random.nextInt(boundary.y.toInt))
    while (grid.contains(p)) {
      p = Point(random.nextInt(boundary.x.toInt), random.nextInt(boundary.y.toInt))
    }
    p
  }


  private[this] def updateBreakers() ={

    def updateABreaker(breaker: Breaker, actMap: Map[Long, Int]) = {
      val keyCode = actMap.get(breaker.id)
      println(s" breaker[${breaker.id}] feels key: $keyCode  at frame = $frameCount")
      val keyDirection = keyCode match {
        case Some(KeyEvent.VK_LEFT) => Point(-1, 0)
        case Some(KeyEvent.VK_RIGHT) => Point(1, 0)
        case _ => Point(0, 0)
      }
      var newHeader = (keyDirection * 3 + breaker.header)
      if (newHeader.x <= 0  || newHeader.x >= boundary.x - 20)
      newHeader = breaker.header

      grid -= breaker.header


      Right(breaker.copy(header = newHeader))
    }

    var updatedBreakers = List.empty[Breaker]
    val acts = actionMap.getOrElse(frameCount, Map.empty[Long, Int])
    breakers.values.map(updateABreaker(_, acts)).foreach {
      case Right(s) => updatedBreakers ::= s
    }

    grid ++= updatedBreakers.map(s => s.header -> Stick(s.id, s.sLength, "#696969"))
    breakers = updatedBreakers.map(s => (s.id, s)).toMap
  }

  def updateAndGetGridData() = {
    update()
    getGridData
  }

  def getGridData = {
    var bodyDetails: List[Bd] = Nil
    var appleDetails: List[Ap] = Nil
    var blockDetails: List[Bk] = Nil
    var stickDetails: List[Sk] = Nil
    var ballDetails: List[Bl] = Nil

    grid.foreach {
      case (p, Body(id, life)) => bodyDetails ::= Bd(id, life, p.x, p.y)
      case (p, Apple(score, life)) => appleDetails ::= Ap(score, life, p.x, p.y)
      case (p, Header(id, life)) => bodyDetails ::= Bd(id, life, p.x, p.y)
      case (p, Block(score)) => blockDetails ::= Bk(score, p.x, p.y)
      case (p, Stick(id, length, color)) => stickDetails ::= Sk(id, p, length, color)
      case (p, Ball(id, color, direction, speed)) => ballDetails ::= Bl(id, p, color, direction, speed)
    }
    Protocol.GridDataSync(
      frameCount,
      snakes.values.toList,
      breakers.values.toList,
      bodyDetails,
      appleDetails,
      blockDetails,
      stickDetails,
      ballDetails
    )
  }


}
