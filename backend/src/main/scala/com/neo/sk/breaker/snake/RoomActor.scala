package com.neo.sk.breaker.snake

import java.awt.event.KeyEvent
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.util.Random
import scala.util.{Left, Right}
import com.neo.sk.breaker.Boot.{scheduler, timeout}
import com.neo.sk.breaker.Boot.executor
import com.neo.sk.breaker.breaker.Protocol._
import com.neo.sk.breaker.snake.RoomManager._
import com.neo.sk.utils.MiddleBufferInJvm
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorAttributes, OverflowStrategy, Supervision}
import akka.stream.typed.scaladsl.{ActorSink,ActorSource}
import com.neo.sk.breaker.breaker.{Boundary, Point, Protocol}
import com.neo.sk.breaker.breaker._
import com.neo.sk.utils.byteObject.ByteObject._

/**
  * created by ltm on 2/21/2019
  */

object RoomActor {
  private val log = LoggerFactory.getLogger(this.getClass)
  private final case object BehaviorChangeKey
  val idGenerator = new AtomicInteger(1000000)
  val bounds = Point(Boundary.w, Boundary.h)
  val syncTimerPrefix = "sync"

  sealed trait Command
  case class TimeOut(msg: String) extends Command
  case class JoinReqInActor(id:Long, name: String, roomId: Long, ref: ActorRef[JoinRsq], state: Int) extends  Command
  case class JoinRoom(id: Long, name: String, roomId: Long, state: Int, subscriber: ActorRef[WsSourceMsg]) extends Command
  case class LeftRoom(id: Long, name: String, subscriber: ActorRef[WsSourceMsg]) extends Command
  case class StreamCompleteMsg(id: Long) extends Command
  case class StreamFailedMsg(e: Throwable) extends Command
  case class FromFront(orderOpt: Option[MsgFromFront]) extends Command
  case object SyncData extends Command

  private[this] def switchBehavior(ctx: ActorContext[Command],
     behaviorName: String,
     behavior: Behavior[Command],
     durationOpt: Option[FiniteDuration] = None,
     timeOut: TimeOut = TimeOut("busy time error"))
  (implicit stashBuffer: StashBuffer[Command], timer:TimerScheduler[Command]) = {
    log.debug(s"${ctx.self.path} becomes $behaviorName behavior")
    timer.cancel(BehaviorChangeKey)
    durationOpt.foreach(timer.startSingleTimer(BehaviorChangeKey, timeOut, _))
    stashBuffer.unstashAll(ctx, behavior)
  }

  def create(roomId: Long, joinerLimit: Int, timeLimit: Int, countDownLimit: Int, manager: ActorRef[RoomManager.Command]): Behavior[Command] ={
    log.debug(s"roomActor-$roomId is starting...")
    Behaviors.setup[Command] {
      ctx =>
        implicit val stashBuffer = StashBuffer[Command](Int.MaxValue)
        Behaviors.withTimers[Command]{
          implicit  timer =>
            switchBehavior(ctx, "idle", idle(roomId, joinerLimit, timeLimit, countDownLimit, manager))
        }
    }
  }

  def idle(roomId: Long, joinerLimit: Int, timeLimit: Int, countDownLimit: Int, manager: ActorRef[RoomManager.Command])(implicit stashBuffer: StashBuffer[Command],timer: TimerScheduler[Command]): Behavior[Command] = {
    Behaviors.receive[Command] {

      var joinerCountInActor = 0
      var tickCount = 0
      var switcher = false
      var mustOrNot = false
      var subscribers = List.empty[(Long, ActorRef[WsSourceMsg])]
      var userMap = Map.empty[Long, String]
      val grid = new GridOnServer(bounds)
      var dataDeposit = GridDataSync(0l, List.empty[Breaker], List.empty[Bk], List.empty[Sk], List.empty[Bl])

      (ctx, msg) =>
        msg match {
          case JoinReqInActor(id, name, roomId, ref, state) =>
            log.debug(s"${ctx.self.path} receive msg: $msg")
            val flow = createFlow(id, name, roomId, state, ctx.self)
            ref ! JoinRsq(flow)
            Behaviors.same

          case JoinRoom(id, name, roomId, state, subscriber) =>
            log.info(s"$id $name join room-$roomId")

            if (joinerCountInActor >= joinerLimit) {
              manager ! LeftRoomInfoBack(id, roomId)
              subscriber ! GameError(1001, "房间已满")
            }
            else {
              joinerCountInActor += 1
              userMap += (id -> name)
              subscribers ::= (id, subscriber)
              grid.addPlayer(id, name)
              ctx.watchWith(subscriber, LeftRoom(id, name, subscriber))
              subscriber ! UserInfo(id, roomId)
              subscribers.map(_._2) foreach (s => s ! RoomInfo(joinerCountInActor))
              if (joinerCountInActor == 2) {
                grid.genBlocks()
                val userInfo = userMap.toList.map(item =>
                  PlayerInfo(item._1, item._2)
                )
                subscribers.map(_._2).foreach(s => s ! AllPlayerInfo(userInfo))
              }
              if (state == 1) {
                subscriber ! GameInfo(101, roomId.toString)
              } else if (state == 2) {
                subscriber ! GameInfo(102, roomId.toString)
              } else {

              }
              if (userMap.keys.toList.lengthCompare(joinerLimit) == 0) {
                userMap.foreach { u => grid.addPlayer(u._1, u._2) }
                switcher = true
                timer.startPeriodicTimer(syncTimerPrefix + roomId, SyncData, frameRate.millis)
              }
            }
            Behaviors.same

          case LeftRoom(id, name, subscriber) =>
            log.debug(s"${ctx.self.path} receive msg: $msg")
            log.debug(s"$id - $name left room - $roomId")
            grid.removeBreaker(id)
            subscribers = subscribers.filterNot(s => s._1 == id)
            subscribers.find(_._1 == id).map(_._2).foreach(s => ctx.unwatch(s))
            userMap -= id
            subscriber ! Protocol.BreakerLeft(id, name)
            Behaviors.same

          case StreamCompleteMsg(id) =>
            log.debug(s"${ctx.self.path} receive msg: $msg")
            if (subscribers.map(_._1).contains(id)) {
              manager ! LeftRoomInfoBack(id, roomId)
              grid.removeBreaker(id)
              subscribers.find(_._1 == id).map(_._2).foreach(s => ctx.unwatch(s))
              subscribers = subscribers.filterNot(_._1 == id)
              userMap -= id
              if (switcher) {
                timer.cancel(syncTimerPrefix + roomId)
                tickCount = 0
                grid.init()
                subscribers.map(_._2).foreach(s => s ! GridDataToSync(dataDeposit.compress(grid.getGridData), false))
                dataDeposit = GridDataSync(0L, List.empty[Breaker], List.empty[Bk], List.empty[Sk], List.empty[Bl])
                subscribers.map(_._2).foreach(s => s ! Protocol.GameBreak)
                switcher = false
                mustOrNot = false
              }
              joinerCountInActor -= 1
              subscribers.map(_._2).foreach(s => s ! RoomInfo(joinerCountInActor))
            }
            if (joinerCountInActor <= 0) {
              Behaviors.stopped
            } else {
              Behaviors.same
            }

          case SyncData =>
            if (tickCount * frameRate / 1000 >= (timeLimit + countDownLimit)) {

              val winnerId =
                if (grid.scoreMap.values.toList.distinct.lengthCompare(1) == 1) {
                  grid.scoreMap.toList.maxBy(_._2)._1
                } else {
                  0
                }

              val gameResult = grid.scoreMap.map(s => GameResultInfo(s._1, s._2)).toList
              subscribers.map(_._2).foreach(s => s ! Protocol.GameOver(winnerId, gameResult))
              grid.init()
              subscribers.map(_._2).foreach(s => s ! GridDataToSync(dataDeposit.compress(grid.getGridData), false))
              dataDeposit = GridDataSync(0L, List.empty[Breaker], List.empty[Bk], List.empty[Sk], List.empty[Bl])

              timer.cancel(syncTimerPrefix + roomId)
              switcher = false
              mustOrNot = false
              tickCount = 0

              if (userMap.keys.toList.lengthCompare(joinerLimit) == 0) {
                userMap.foreach { u => grid.addPlayer(u._1, u._2) }
                switcher = true
                timer.startPeriodicTimer(syncTimerPrefix + roomId, SyncData, frameRate.millis)
              }

            } else {
              //限制时间之内
              tickCount += 1
              if (tickCount * frameRate / 1000 <= countDownLimit) {
                //倒计时时间
                if (tickCount == 1 || tickCount % 10 == 0) {
                  subscribers.map(_._2).foreach(s => s ! Protocol.GameTime((countDownLimit - tickCount * frameRate / 1000).toString, true))
                }
              } else {
                mustOrNot = if (grid.waitingJoin.nonEmpty) true
                else false
                grid.update()

                //转块数
                if (grid.blockNum <= 0) {
                  timer.cancel(syncTimerPrefix + roomId)
                  switcher = false
                  mustOrNot = false
                  tickCount = 0

                  subscribers.map(_._2).foreach(s => s ! Protocol.Ranks(grid.currentRank))

                  val winnerId = grid.scoreMap.toList.maxBy(_._2)._1
                  val gameResult = grid.scoreMap.map(s => GameResultInfo(s._1, s._2)).toList
                  subscribers.map(_._2).foreach(s => s ! Protocol.GameOver(winnerId, gameResult))
                  grid.init()
                  subscribers.map(_._2).foreach(s => s ! GridDataToSync(dataDeposit.compress(grid.getGridData), false))
                  dataDeposit = GridDataSync(0L, List.empty[Breaker], List.empty[Bk], List.empty[Sk], List.empty[Bl])
                  if (userMap.keys.toList.lengthCompare(joinerLimit) == 0) {
                    userMap.foreach { u => grid.addPlayer(u._1, u._2) }
                    switcher = true
                    timer.startPeriodicTimer(syncTimerPrefix + roomId, SyncData, frameRate.millis)
                  }
                } else {
                  if (tickCount % 10 == 2) {
                    val data1 = grid.getGridData
                    val data2 = dataDeposit.compress(data1)
                    val data = if (tickCount == ((countDownLimit + 1) * 1000 / frameRate + 2)) {
                      //第一次同步数据
                      GridDataToSync(data2, true)
                    } else {
                      GridDataToSync(data2, mustOrNot)
                    }
                    subscribers.map(_._2).foreach(s => s ! data)
                    dataDeposit = data1
                  }

                  if (tickCount % 10 == 1) {
                    subscribers.map(_._2).foreach(s => s ! Protocol.Ranks(grid.currentRank))
                  }

                  if (tickCount % 10 == 0) {
                    subscribers.map(_._2).foreach(s => s ! Protocol.GameTime((timeLimit + countDownLimit - tickCount * frameRate / 1000).toString, false))
                  }


                }
              }
            }
            Behaviors.same

          case FromFront(msgOpt) =>
            msgOpt match {
              case Some(frontMsg: MsgFromFront) =>
                frontMsg match {
                  case KeyWithFrame(id, keyCode, frameId, timeStamp) =>
                    log.debug(s"${ctx.self.path} receive msg: $msg")
                    if (switcher) {
                      if (keyCode == KeyEvent.VK_SPACE) {
                        grid.addPlayer(id, userMap.getOrElse(id, "unknown"))
                      } else {
                        if (frameId < grid.frameCount) {
                          //如果超时 则丢弃该帧
                          subscribers.find(_._1 == id).map(_._2).foreach(s => s ! Protocol.LandlordAction(id, keyCode, -1L, timeStamp))
                        }
                        else {
                          grid.addActionWithFrame(id, keyCode, frameId)
                          subscribers.map(_._2).foreach(s => s ! Protocol.LandlordAction(id, keyCode, Math.max(frameId, grid.frameCount), timeStamp))
                        }

                      }
                    }

                  case NetTest(id, createTime) =>
                    log.debug(s"${ctx.self.path} receive msg: $msg")
                    val subscriber = subscribers.find(_._1 == id).map(_._2)
                    subscriber.foreach(s => s ! Protocol.NetDelayTest(createTime))

                  case TickTock =>
                    log.debug(s"${ctx.self.path} receive ticktock")

                  case _ =>
                }
              case _ =>
            }
            Behaviors.same

          case unKnown =>
            log.warn(s"${ctx.self.path} receive a unknown msg when idle:${unKnown}")
            Behaviors.same
        }

    }

  }

  def playInSink(id: Long, name: String, roomId: Long, actor: ActorRef[Command]) = ActorSink.actorRef[Command](
    ref = actor,
    onCompleteMessage = StreamCompleteMsg(id),
    onFailureMessage = StreamFailedMsg.apply
  )

  def flow(id: Long, name: String, roomId: Long, state: Int, selfRef: ActorRef[Command]): Flow[Command, WsSourceMsg, Any] = {

    val in =
      Flow[Command]
        .to(playInSink(id, name, roomId, selfRef))

    val out =
      ActorSource.actorRef[WsSourceMsg](
        completionMatcher = {
          case CompleteMsg ⇒
        },
        failureMatcher = {
          case FailMsg(e)  ⇒ e
        },
        bufferSize = 64,
        overflowStrategy = OverflowStrategy.dropHead
      ).mapMaterializedValue(outActor => selfRef ! JoinRoom(id,name,roomId,state,outActor))

    Flow.fromSinkAndSource(in, out)
  }

  import akka.util.ByteString
  import com.neo.sk.utils.byteObject.ByteObject._

  def createFlow(id:Long, name: String, roomId: Long, state: Int, selfRef: ActorRef[Command]): Flow[Message,Message,Any] = {
    import scala.language.implicitConversions

    implicit def parseJsonString2WsMsgFront(s:String):Option[MsgFromFront] = {
      import io.circe.generic.auto._
      import io.circe.parser._

      try {
        val wsMsg = decode[MsgFromFront](s).right.get
        Some(wsMsg)
      }catch {
        case e:Exception =>
          log.warn(s"parse front msg failed when json parse,s=${s}")
          None
      }
    }

    Flow[Message]
      .collect {
        case TextMessage.Strict(msg) =>
          FromFront(msg)

        case BinaryMessage.Strict(bMsg) =>
          val buffer = new MiddleBufferInJvm(bMsg.asByteBuffer)
          val msg =
            bytesDecode[MsgFromFront](buffer) match {
              case Right(v) => FromFront(Some(v))
              case Left(e) =>
                println(s"decode error: ${e.message}")
                FromFront(None)
            }
          msg
      }
      .via(flow(id, name, roomId, state, selfRef))
      .map {
        case t: GameMessage =>
          val sendBuffer = new MiddleBufferInJvm(409600)
          BinaryMessage.Strict(ByteString(
            t.fillMiddleBuffer(sendBuffer).result()
          ))

        case x =>
          TextMessage.apply("")


      }.withAttributes(ActorAttributes.supervisionStrategy(decider))
  }

  val decider: Supervision.Decider = {
    e: Throwable =>
      e.printStackTrace()
      println(s"WS stream failed with $e")
      Supervision.Resume

  }

}
