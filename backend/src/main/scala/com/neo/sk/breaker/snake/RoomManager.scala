package com.neo.sk.breaker.snake

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import akka.actor.typed.scaladsl.AskPattern._
import akka.stream.scaladsl.Flow
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.util.Random
import com.neo.sk.breaker.snake.RoomActor.JoinReqInActor

object RoomManager {
  private val log = LoggerFactory.getLogger(this.getClass)
  private final case object BehaviorChangeKey
  val random = new Random()
  val roomIdGenerator = new AtomicInteger(100)
  //房间人数控制
  val joinerLimit = 2
  //游戏时间限制
  val timeLimit = 60
  //倒计时时间限制
  val countDownLimit = 3
  //在营房间号维护
  var roomInUse = Set.empty[(Long,Int)]

  trait Command

  case class ChildDead(roomId: Long, childRef:ActorRef[RoomActor.Command]) extends Command
  case class TimeOut(msg:String) extends Command
  case class JoinReqInManager(id: Long, name: String, roomId: Long,ref: ActorRef[JoinRsq]) extends Command
  case class JoinRsq(flow: Flow[Message,Message,Any]) extends Command
  case class LeftRoomInfoBack(roomerId: Long,roomId: Long) extends Command
  case class Test(flow: Either[Error,Flow[Message,Message,Any]]) extends Command

  private[this] def switchBehavior( ctx: ActorContext[Command],
                                    behaviorName: String,
                                    behavior: Behavior[Command],
                                    durationOpt: Option[FiniteDuration] = None,
                                    timeOut: TimeOut  = TimeOut("busy time error"))
                                  (implicit stashBuffer: StashBuffer[Command], timer:TimerScheduler[Command]) = {
    log.debug(s"${ctx.self.path} becomes $behaviorName behavior.")
    timer.cancel(BehaviorChangeKey)
    durationOpt.foreach(timer.startSingleTimer(BehaviorChangeKey,timeOut,_))
    stashBuffer.unstashAll(ctx,behavior)
  }

  def create():Behavior[Command] = {
    Behaviors.setup[Command] {
      ctx =>
        implicit val stashBuffer = StashBuffer[Command](Int.MaxValue)
        Behaviors.withTimers[Command] { implicit timer =>
          switchBehavior(ctx,"idle",idle())
        }
    }
  }

  def idle()(implicit stashBuffer: StashBuffer[Command],timer: TimerScheduler[Command]):Behavior[Command] = {
    Behaviors.receive[Command] {(ctx,msg) =>
      msg match {

        case JoinReqInManager(id, name,roomId,ref) =>
          log.debug(s"${ctx.self.path} receive msg: $msg")

          if(roomId == 0L){
            //创建房间请求
            val roomIdT = roomIdGenerator.getAndIncrement().toLong
            getRoomActor(ctx,roomIdT) ! JoinReqInActor(id, name,roomIdT,ref,0)
            val temp = (roomIdT,1)
            roomInUse += temp
            log.debug(s"roomInUse: $roomInUse")
          }else if(roomId == -1L) {

            //随机进入房间请求
            val roomsCanBeUsedHere = roomInUse.filter(_._2 < joinerLimit).toList
            if(roomsCanBeUsedHere.lengthCompare(0) == 0) {
              //证明没有空房间,则新创建一个房间
              val roomIdT = roomIdGenerator.getAndIncrement().toLong
              getRoomActor(ctx,roomIdT) ! JoinReqInActor(id, name,roomIdT,ref,2)
              val temp = (roomIdT,1)
              roomInUse += temp
              log.debug(s"roomInUse: $roomInUse")
            }else{
              //有空房间,则随机分配一个进入
              val roomIdList = roomsCanBeUsedHere.map(_._1)
              val roomIdT = roomIdList(random.nextInt(roomIdList.length))
              getRoomActor(ctx,roomIdT) ! JoinReqInActor(id, name, roomIdT, ref, 0)
              roomInUse = roomInUse.map{
                case (id,roomerNum) if id == roomIdT => (id,roomerNum + 1)
                case x => x
              }
              log.debug(s"roomInUse: $roomInUse")
            }

          } else{
            //进入房间请求
            if(roomInUse.map(_._1).contains(roomId)){
              //待进入房间在营
              roomInUse = roomInUse.map{
                case (id,roomerNum) if id == roomId => (id,roomerNum + 1)
                case x => x
              }
              log.debug(s"roomInUse: $roomInUse")
              getRoomActor(ctx,roomId) ! JoinReqInActor(id, name,roomId,ref,0)
            }else{
              //待进入房间不在营,则创建新房间
              val roomIdT = roomIdGenerator.getAndIncrement().toLong
              getRoomActor(ctx,roomIdT) ! JoinReqInActor(id, name,roomIdT,ref,1)
              val temp = (roomIdT,1)
              roomInUse += temp
              log.debug(s"roomInUse: $roomInUse")
            }
          }
          Behaviors.same

        case ChildDead(child,childRef) =>
          log.warn(s"${ctx.self.path} child=$child is dead")
          ctx.unwatch(childRef)
          roomInUse = roomInUse.filterNot(_._1 == child)
          log.debug(s"roomInUse: $roomInUse")
          Behaviors.same

        case LeftRoomInfoBack(_,roomId) =>
          log.debug(s"${ctx.self.path} receive msg: $msg")
          roomInUse = roomInUse.map{
            case (id,roomerNum) if id == roomId => (id,roomerNum - 1)
            case x => x
          }
          log.debug(s"roomInUse: $roomInUse")
          Behaviors.same

        case unknow =>
          log.warn(s"${ctx.self.path} receive a unknown msg when idle:${unknow}")
          Behaviors.same


      }
    }
  }

  private def getRoomActor(ctx: ActorContext[Command],roomId: Long): ActorRef[RoomActor.Command] = {
    val childName = s"$roomId"
    ctx.child(childName).getOrElse{
      val actor = ctx.spawn(RoomActor.create(roomId, joinerLimit, timeLimit, countDownLimit, ctx.self),childName)
      ctx.watchWith(actor,ChildDead(roomId,actor))
      actor
    }.upcast[RoomActor.Command]
  }
}
