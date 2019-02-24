package com.neo.sk.breaker.front.pages

import com.neo.sk.breaker.breaker.SuccessRsp
import com.neo.sk.breaker.breaker.UserProtocol.LoginReq
import com.neo.sk.breaker.front.Routes
import com.neo.sk.breaker.front.common.Page
import mhtml._
import org.scalajs.dom
import org.scalajs.dom.html.Input
import com.neo.sk.breaker.front.breaker.NetGameHolder
import com.neo.sk.frontUtils.{Http, JsFunc}
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalajs.dom.raw.KeyboardEvent

import scala.xml.{Elem, Node}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.xml.Elem

object EntryPage extends Page{

  val canvas = <canvas id ="GameView" tabindex="1"> </canvas>
  val playground = <div id="playground"></div>
  val showGame = Var(0)
  val show : Rx[Elem] = showGame.map{
    case 0 =>
      <div>
        <div class="entry">
          <div class="title">
            <h1>Breaker</h1>
          </div>
          <div class="text">
            <input type="text" class="form-control" id="userName" placeholder="UserName"></input>
            <br></br>
            <input type="password" class="form-control" id="passWord" placeholder="PassWord"></input>
          </div>
          <div class="button">
            <button type="button" class="btn btn-secondary" id="play" onclick = {()=>login()}>登录</button>
            <button type="button" class="btn btn-secondary" onclick = {()=> dom.window.location.href = "#/register"}>注册</button>
          </div>
        </div>
      </div>

    case 1 =>
      <div>
        {canvas}
        {playground}
      </div>
  }

  def login(): Unit = {
    val username=dom.document.getElementById("userName").asInstanceOf[Input].value
    val password=dom.document.getElementById("passWord").asInstanceOf[Input].value
    if (username=="" || password==""){
      JsFunc.alert("用户名和密码不能为空")
    }
    else {
      val url = Routes.User.login
      val data = LoginReq(username, password).asJson.noSpaces
      Http.postJsonAndParse[SuccessRsp](url, data).map {
        case Right(rsp)=>
          if (rsp.errCode==0){
            JsFunc.alert("Login Success")
            joinGame()
          }
          else {
            println(s"error:${rsp.msg}")
            JsFunc.alert(rsp.msg)
          }
        case Left(err)=>
          println(s"err:${err}")
          JsFunc.alert("Login Error")
      }
    }
  }


  def joinGame() ={
    println("joinGame")
    val userName = dom.document.getElementById("userName").asInstanceOf[Input].value
    showGame := 1
    NetGameHolder.start(userName)
  }

  override def render: Elem =
    <div>
      {show}
    </div>

}
