package com.neo.sk.breaker.front.pages

import com.neo.sk.breaker.front.common.Page
import mhtml._
import org.scalajs.dom
import org.scalajs.dom.html.Input
import com.neo.sk.breaker.front.snake.NetGameHolder
import org.scalajs.dom.KeyboardEvent
import org.scalajs.dom.raw.{HTMLButtonElement, HTMLInputElement}

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
            <input type="text" class="form-control" id="userName"></input>
          </div>
          <div class="button">
            <button type="button" class="btn" id="play" onclick={()=>joinGame()}>Play</button>
          </div>
        </div>
      </div>

    case 1 =>
      <div>
        {canvas}
        {playground}
      </div>
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
