package com.neo.sk.breaker.snake

/**
  * User: Taoz
  * Date: 8/29/2016
  * Time: 9:40 PM
  */
object Protocol {

  sealed trait GameMessage

  case class GridDataSync(
    frameCount: Long,
    breakers: List[Breaker],
    blockDetails: List[Bk],
    stickDetails: List[Sk],
    ballDetails: List[Bl]
  ) extends GameMessage



  case class BlockInit(
    breaks: List[Bk]
  ) extends GameMessage


  case class TextMsg(
    msg: String
  ) extends GameMessage

  case class Id(id: Long) extends GameMessage

  case class NewSnakeJoined(id: Long, name: String) extends GameMessage

  case class SnakeAction(id: Long, keyCode: Int, frame: Long) extends GameMessage

  case class BreakerLeft(id: Long, name: String) extends GameMessage

  case class Ranks(currentRank: List[Score], historyRank: List[Score]) extends GameMessage

  case class NetDelayTest(createTime: Long) extends GameMessage

  val frameRate = 150

}
