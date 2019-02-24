package com.neo.sk.breaker.http


import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.neo.sk.breaker.breaker._
import com.neo.sk.breaker.breaker.UserProtocol._
import com.neo.sk.breaker.http.SessionBase._
import com.neo.sk.breaker.models.DAO._
import com.neo.sk.utils.SecureUtil
import io.circe.Error
import io.circe.generic.auto._
import org.slf4j.LoggerFactory

/**
  * Created by Zx0 on 2018/06/26.
  **/
trait UserService extends BaseService with SessionBase{

  private val log = LoggerFactory.getLogger(this.getClass)

  private val login = (path("login") & post & pathEndOrSingleSlash){
    entity(as[Either[Error,LoginReq]]){
      case Right(req)=>
        dealFutureResult{
          UserDAO.searchUserByName(req.username).map{
            case Some(user)=>
              if (user.psw == req.password) {
                setSession(
                  UserSession(UserSessionInfo(user.id, user.name), System.currentTimeMillis()).toUserSessionMap
                ) { ctx =>
                  ctx.complete(SuccessRsp())
                }
              }
              else {
                log.info("password is wrong.")
                complete(ErrorRsp(101,"密码错误"))
              }
            case None=>
              log.info("no user.")
              complete(ErrorRsp(102,"用户不存在"))
          }
        }
      case Left(_) =>
        complete(ErrorRsp(103,"parse error"))
    }
  }

  private val addUser = (path("addUser") & post & pathEndOrSingleSlash){
    entity(as[Either[Error, UserInfo]]) {
      case Right(req) =>
        dealFutureResult {
          UserDAO.searchUserByName(req.username).map {
            case Some(user) =>
              log.info("user already exist")
              complete(ErrorRsp(201, "用户名已存在"))
            case None =>
              UserDAO.adduser(req.username, req.password)
              complete(SuccessRsp())
          }
        }

      case Left(_) =>
        complete(ErrorRsp(202, "parse error"))
    }
  }

  private val judUser=(path("judUser") & post & pathEndOrSingleSlash){
    entity(as[Either[Error,JudReq]]){
      case Right(req)=>
        dealFutureResult{
          UserDAO.searchUser(req.uId).map{
            case ctx=>
              if (ctx.psw == req.psw) {
                complete(SuccessRsp())
              }
              else {
                complete(ErrorRsp(101,"原密码不正确"))
              }
          }
        }
      case Left(e)=>
        complete(ErrorRsp(102,"传输错误"))
    }
  }

  private val changepsw=(path("changepsw")&post&pathEndOrSingleSlash){
    entity(as[Either[Error,PswReq]]){
      case Right(req)=>
        dealFutureResult{
          UserDAO.searchUser(req.uId).map{
            case ctx=>
              UserDAO.changePsw(req.uId, req.newpsw)
              complete(SuccessRsp())
          }
        }
      case Left(e)=>
        complete(ErrorRsp(101,"传输错误"))
    }
  }

  val logout =(path("logout")){
    invalidateSession {
      complete(SuccessRsp())
    }
  }


  val userRoutes = pathPrefix("user"){
    login ~ addUser ~ logout ~ changepsw ~ judUser
  }
}
