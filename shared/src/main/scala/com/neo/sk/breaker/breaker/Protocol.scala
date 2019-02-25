package com.neo.sk.breaker.breaker

/**
  * User: Taoz
  * Date: 8/29/2016
  * Time: 9:40 PM
  */
object Protocol {

  def diffResult[T](aList: List[T],bList: List[T]): List[T] = {
    aList.diff(bList) ::: bList.diff(aList)
  }

  sealed trait WsSourceMsg

  case object CompleteMsg extends WsSourceMsg
  case class FailMsg(ex: Exception) extends WsSourceMsg


  sealed trait GameMessage extends WsSourceMsg

  case class GridDataSync(
    frameCount: Long,
    breakers: List[Breaker],
    blockDetails: List[Bk],
    stickDetails: List[Sk],
    ballDetails: List[Bl]
  ) extends GameMessage{
    def compress(other: GridDataSync) =
      GridDataSync(other.frameCount,
        other.breakers,
        diffResult[Bk](blockDetails,other.blockDetails),
        diffResult[Sk](stickDetails,other.stickDetails),
        diffResult[Bl](ballDetails,other.ballDetails)
        )
  }

  case class BlockInit(
    breaks: List[Bk]
  ) extends GameMessage


  case class Id(id: Long) extends GameMessage
  case class NewBreakerJoined(id: Long, name: String) extends GameMessage
  case class BreakerAction(id: Long, keyCode: Int, frame: Long) extends GameMessage
  case class BreakerLeft(id: Long, name: String) extends GameMessage


  case class GridDataToSync(data: GridDataSync,flag: Boolean) extends GameMessage
  case class TextMsg(msg: String) extends GameMessage
  case class UserInfo(id: Long, roomId: Long) extends GameMessage
  case class RoomInfo(userNum: Int) extends GameMessage
  case class PlayerInfo(id: Long,nameT: String)
  case class AllPlayerInfo(playerList:List[PlayerInfo]) extends GameMessage
  case class GameError(errCode: Int, msg: String) extends GameMessage
  case class GameInfo(errCode: Int,msg: String) extends GameMessage

  case object GameBreak extends GameMessage

  case class GameResultInfo(id: Long, score: Int)
  case class GameOver(winnerId: Long,gameResult: List[GameResultInfo]) extends GameMessage
  case class GameTime(time: String,state:Boolean) extends GameMessage
  case class LandlordAction(id: Long, keyCode: Int, frame: Long, timestamp: Long) extends GameMessage
  case class Ranks(currentRank: List[Score]) extends GameMessage
  case class NetDelayTest(createTime: Long) extends GameMessage
  case class PongData(createTime: Long) extends GameMessage


  sealed trait MsgFromFront

  case class Key(id: Long, keyCode: Int) extends MsgFromFront
  case class KeyWithFrame(id: Long, keyCode: Int, frameId: Long, timestamp: Long) extends MsgFromFront
  case class NetTest(id: Long, createTime: Long) extends MsgFromFront
  case class TextInfo(msg: String) extends MsgFromFront
  case object TickTock extends MsgFromFront
  case class PingData(id: Long,createTime: Long) extends MsgFromFront

  val frameRate = 100

}
