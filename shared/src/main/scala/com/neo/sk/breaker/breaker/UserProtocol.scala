package com.neo.sk.breaker.breaker

/**
  * Created by Zx0 on 2018/06/26.
  **/
object UserProtocol {
  case class LoginReq(
                       username:String,
                       password:String
                     )

  case class UserInfo(
                       username:String,
                       password:String,
                     )

  case class userLogin(
                        uId:Long,
                        username:String,
                      )

  case class LoginRsp(
                       data:Option[userLogin],
                       errCode:Int = 0,
                       msg:String = "ok"
                     )
  case class PswReq(
                     uId:Long,
                     psw:String,
                     newpsw:String
                   )
  case class JudReq(
                     uId:Long,
                     psw:String
                   )
}
