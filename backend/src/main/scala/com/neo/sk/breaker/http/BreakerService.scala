package com.neo.sk.breaker.http

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Flow
import akka.stream.{ActorAttributes, Materializer, Supervision}
import com.neo.sk.breaker.snake.RoomManager._
import com.neo.sk.breaker.Boot.roomManager
import akka.util.Timeout
import com.neo.sk.breaker.breaker.PlayGround
import com.neo.sk.breaker.snake.RoomManager.JoinRsq
import org.slf4j.LoggerFactory
import akka.http.scaladsl.server.Directives._
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.Scheduler
import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * User: Taoz
  * Date: 9/1/2016
  * Time: 4:13 PM
  */
trait BreakerService extends BaseService{


  import io.circe.generic.auto._
  import io.circe.syntax._
//
//  implicit val system: ActorSystem
//
//  implicit def executor: ExecutionContextExecutor
//
//  implicit val materializer: Materializer

  implicit val timeout: Timeout
  implicit val scheduler: Scheduler

  private[this] val log = LoggerFactory.getLogger("SnakeService")


  val webSocketRoute = {
    (pathPrefix("netSnake") & get) {
      pathEndOrSingleSlash {
        getFromResource("html/netBreaker.html")
      } ~
      path("join") {
        parameter('id.as[Long], 'name.as[String] ,'roomId.as[Long]) {
          case (id, name, roomId) =>
            val rstFuture: Future[JoinRsq] = roomManager ? (ref => JoinReqInManager(id, name, roomId, ref))
            dealFutureResult{
              rstFuture.map{ rst =>
                handleWebSocketMessages(rst.flow)
              }
            }
        }
      }
    }
  }
//
//
//  def webSocketSnakeFlow(sender: String): Flow[Message, Message, Any] =
//    Flow[Message]
//      .collect {
//        case TextMessage.Strict(msg) =>
//          log.debug(s"msg from webSocket: $msg")
//          msg
//        // unpack incoming WS text messages...
//        // This will lose (ignore) messages not received in one chunk (which is
//        // unlikely because chat messages are small) but absolutely possible
//        // FIXME: We need to handle TextMessage.Streamed as well.
//      }
//      .via(playGround.joinGame(idGenerator.getAndIncrement().toLong, sender)) // ... and route them through the chatFlow ...
//      .map { msg => TextMessage.Strict(msg.asJson.noSpaces) // ... pack outgoing messages into WS JSON messages ...
//      //.map { msg => TextMessage.Strict(write(msg)) // ... pack outgoing messages into WS JSON messages ...
//    }.withAttributes(ActorAttributes.supervisionStrategy(decider))    // ... then log any processing errors on stdin
//
//
//  private val decider: Supervision.Decider = {
//    e: Throwable =>
//      e.printStackTrace()
//      println(s"WS stream failed with $e")
//      Supervision.Resume
//  }




}
