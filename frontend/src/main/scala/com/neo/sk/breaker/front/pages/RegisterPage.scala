package com.neo.sk.breaker.front.pages

import com.neo.sk.breaker.breaker.SuccessRsp
import com.neo.sk.breaker.breaker.UserProtocol.{LoginReq, UserInfo}
import com.neo.sk.breaker.front.Routes
import com.neo.sk.breaker.front.common.Page
import com.neo.sk.frontUtils.{Http, JsFunc}
import org.scalajs.dom
import org.scalajs.dom.html.Input
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.xml.Elem
import scala.concurrent.ExecutionContext.Implicits.global

object RegisterPage extends Page{

  override def render: Elem =
    <div>
      <div class="entry">
        <div class="title">
          <h1>Breaker</h1>
        </div>
        <div class="text">
          <input type="text" class="form-control" id="userName" placeholder="UserName"></input>
          <br></br>
          <input type="password" class="form-control" id="passWord" placeholder="PassWord"></input>
          <br></br>
          <input type="password" class="form-control" id="again" placeholder="Again"></input>
        </div>
        <div class="button">
          <button type="button" class="btn btn-secondary" id="play" onclick={()=>register()}>注册</button>
        </div>
      </div>
    </div>

  def register(): Unit = {
    val username=dom.document.getElementById("userName").asInstanceOf[Input].value
    val password=dom.document.getElementById("passWord").asInstanceOf[Input].value
    val again=dom.document.getElementById("again").asInstanceOf[Input].value
    if (username =="" || password == "" || again == ""){
      JsFunc.alert("输入不能为空")
    }
    else {
      val url = Routes.User.addUser
      val data = UserInfo(username, password).asJson.noSpaces
      Http.postJsonAndParse[SuccessRsp](url, data).map {
        case Right(rsp)=>
          if (rsp.errCode==0){
            JsFunc.alert("Register Success")
            dom.window.location.href = "#/login"
          }
          else {
            println(s"error:${rsp.msg}")
            JsFunc.alert(rsp.msg)
          }
        case Left(err)=>
          println(s"err:${err}")
          JsFunc.alert("Register Error")
      }
    }
  }
}
