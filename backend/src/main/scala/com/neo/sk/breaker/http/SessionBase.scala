package com.neo.sk.breaker.http

import com.neo.sk.breaker.common.AppSettings
import com.neo.sk.breaker.breaker.ErrorRsp
import com.neo.sk.utils.SessionSupport
import com.neo.sk.utils.CirceSupport
import org.slf4j.LoggerFactory

/**
  * Created by dry on 2018/4/28.
  **/
object SessionBase extends CirceSupport{

  val SessionTypeKey = "STKey"
  private val logger = LoggerFactory.getLogger(this.getClass)

  object UserSessionKey {
    val SESSION_TYPE = "breaker_userSession"
    val uid = "breaker_uid"
    val userName = "breaker_userName"
    val timestamp = "breaker_timestamp"
  }

  case class UserSessionInfo(
                              uid: Long,
                              name: String,
                            )

  case class UserSession(
                          userInfo: UserSessionInfo,
                          time: Long
                        ) {
    def toUserSessionMap: Map[String, String] = {
      Map(
        SessionTypeKey -> UserSessionKey.SESSION_TYPE,
        UserSessionKey.uid -> userInfo.uid.toString,
        UserSessionKey.userName -> userInfo.name,
      )
    }
  }

}

trait SessionBase extends SessionSupport with BaseService {

  import akka.http.scaladsl.server
  import akka.http.scaladsl.server.Directives.extractRequestContext
  import SessionBase._
  import akka.http.scaladsl.model.StatusCodes
  import akka.http.scaladsl.server.Directives._
  import akka.http.scaladsl.server.{Directive, Directive1, RequestContext}
  import akka.http.scaladsl.server.directives.BasicDirectives
  import io.circe.parser._
  import io.circe.generic.auto._

  override val sessionEncoder = SessionSupport.PlaySessionEncoder
  override val sessionConfig = AppSettings.sessionConfig
  private val sessionTimeout = 24 * 60 * 60 * 1000
  private val log = LoggerFactory.getLogger(this.getClass)

  implicit class SessionTransformer(sessionMap: Map[String, String]) {

    def toUserSession:Option[UserSession] = {
      //      log.debug(s"toAdminSession: change map to session, ${sessionMap.mkString(",")}")
      try {
        if (sessionMap.get(SessionTypeKey).exists(_.equals(UserSessionKey.SESSION_TYPE))) {
          if(sessionMap(UserSessionKey.timestamp).toLong - System.currentTimeMillis() > sessionTimeout){
            None
          } else {
            Some(UserSession(
              UserSessionInfo(
                sessionMap(UserSessionKey.uid).toLong,
                sessionMap(UserSessionKey.userName)
              ),
              sessionMap(UserSessionKey.timestamp).toLong
            ))
          }
        } else {
          log.debug("no session type in the session")
          None
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          log.warn(s"toUserSession: ${e.getMessage}")
          None
      }
    }
  }

  protected val optionalUserSession: Directive1[Option[UserSession]] = optionalSession.flatMap {
    case Right(sessionMap) => BasicDirectives.provide(sessionMap.toUserSession)
    case Left(error) =>
      logger.debug(error)
      BasicDirectives.provide(None)
  }

  private def loggingAction: Directive[Tuple1[RequestContext]] = extractRequestContext.map { ctx =>
    logger.info(s"Access uri: ${ctx.request.uri} from ip ${ctx.request.uri.authority.host.address}.")
    ctx
  }

  def noSessionError(message:String = "no session") = ErrorRsp(1000102,s"$message")

  def userAuth(f: UserSessionInfo => server.Route) = loggingAction { ctx =>
    optionalUserSession {
      case Some(session) =>
        f(session.userInfo)

      case None =>
        complete(noSessionError())
    }
  }

  def breakerAuth(f: String => server.Route) = loggingAction { ctx =>
    optionalUserSession {
      case Some(_) =>
        f("ok")

      case None =>
        complete(noSessionError())
    }
  }

}