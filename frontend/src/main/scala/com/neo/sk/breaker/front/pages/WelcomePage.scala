package com.neo.sk.breaker.front.pages

import com.neo.sk.breaker.breaker.SuccessRsp
import com.neo.sk.breaker.breaker.UserProtocol.{LoginReq, LoginRsp}
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
import org.scalajs.dom.raw.{HTMLDivElement, HTMLInputElement, KeyboardEvent}

import scala.xml.{Elem, Node}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.xml.Elem
object WelcomePage extends Page {
  val aboutCanvas = dom.window.document.getElementById("aboutCanvas").asInstanceOf[HTMLDivElement]
  val uid = dom.window.localStorage.getItem("userId").toLong
  val nickName = dom.window.localStorage.getItem("userName")
  var roomId = 0l
  var roomIdInputFlag = 0
  val roomIdInputFlagVar = Var(0)
  val roomIdInput = roomIdInputFlagVar.map{
    case 1 =>
        <input type="text" class="form-control" id="roomId" placeholder="RoomId"></input>
    case _ =>
        <div>
        </div>
  }
  val allRules = "1.每个房间两人对战 两人都进入房间5s后游戏开始哦\n2.按enter发球,左右控制方向哦\n3.死亡后分数清零哦\n4.绿色砖块3分橘色5分钻石10分哦\n5.每人只能接自己的球哦\n6.全部球打完或到达限时（60s）后 分数高的玩家获胜哦 （若此时两人均处于死亡状态则无胜者）"
  val ruleVar = Var(0)
  val ruleShow = ruleVar.map{
    case 1 =>
      <div class="card">
          <div class="card-body">
            <h5 class="card-title">规则说明</h5>
            <p style="white-space:pre-wrap;" class="card-text">{allRules}</p>
          </div>
      </div>
    case _ =>
      <div></div>
  }

  val showGame = Var(0)
  val show  = showGame.map{
    case 0 =>
      <div>
        <div class="entry">
          <div class="title">
            <h1>Breaker</h1>
          </div>
          <div class="text" style="display:flex; justify-content:center;">
            <button type="button" class="btn btn-outline-info" style="margin: auto" onclick = {()=> createRoom()}>创建房间</button>
            <button type="button" class="btn btn-outline-info" style="margin: auto" onclick = {()=> randomEnter()}>随机进入</button>
            <button type="button" class="btn btn-outline-info" style="margin: auto" onclick = {()=> enterRoom()}>进入房间</button>
            <button type="button" class="btn btn-outline-info" style="margin: auto" onclick = {()=> showRule()}>游戏规则</button>
          </div>
          {roomIdInput}
          {ruleShow}
        </div>
      </div>

    case 1 =>
      new NetGameHolder(uid, nickName, roomId).start()
  }

  def showRule() = {
    ruleVar := 1
  }

  def createRoom() = {
    roomId = 0l
    showGame := 1
  }

  def randomEnter() = {
    roomId = -1
    showGame := 1
  }

  def enterRoom() = {
    if(roomIdInputFlag == 0){
      roomIdInputFlag = 1
      roomIdInputFlagVar := roomIdInputFlag
    }
    else {
      val tmp = dom.window.document.getElementById("roomId").asInstanceOf[HTMLInputElement].value
      if (tmp != ""){
        roomId = tmp.toLong
        showGame := 1
      }
      else {
        JsFunc.alert("请输入房间号")
      }
    }
  }

  override def render: Elem = {
    aboutCanvas.style.display = "none"
    <div>
      {show}
    </div>
  }

}
