package com.neo.sk.breaker

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.dispatch.MessageDispatcher
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.neo.sk.breaker.http.HttpService
import akka.actor.typed.scaladsl.adapter._
import com.neo.sk.breaker.snake.RoomManager
import scala.language.postfixOps

/**
  * User: Taoz
  * Date: 8/26/2016
  * Time: 10:25 PM
  */
object Boot extends HttpService {

  import concurrent.duration._
  import com.neo.sk.breaker.common.AppSettings._


  override implicit val system = ActorSystem("breaker", config)

  override implicit val executor = system.dispatchers.lookup("akka.actor.my-blocking-dispatcher")
  override implicit val materializer = ActorMaterializer()
  override implicit val scheduler = system.scheduler
  override val timeout = Timeout(20 seconds) // for actor asks

  val log: LoggingAdapter = Logging(system, getClass)

  val roomManager = system.spawn(RoomManager.create(), "roomManager")

  def main(args: Array[String]) {
    log.info("Starting.")
    Http().bindAndHandle(routes, httpInterface, httpPort)
    log.info(s"Listen to the $httpInterface:$httpPort")
    log.info("Done.")
  }






}
