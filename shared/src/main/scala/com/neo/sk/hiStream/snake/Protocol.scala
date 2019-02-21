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
    snakes: List[SkDt],
    breakers: List[Breaker],
    bodyDetails: List[Bd],
    appleDetails: List[Ap],
    blockDetails: List[Bk],
    stickDetails: List[Sk],
    ballDetails: List[Bl]
  ) extends GameMessage


  case class FeedApples(
    aLs: List[Ap]
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

  case class SnakeLeft(id: Long, name: String) extends GameMessage

  case class Ranks(currentRank: List[Score], historyRank: List[Score]) extends GameMessage

  case class NetDelayTest(createTime: Long) extends GameMessage

  val frameRate = 150

}
