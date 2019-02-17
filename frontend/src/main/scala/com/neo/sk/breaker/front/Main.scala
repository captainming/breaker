package com.neo.sk.breaker.front

import com.neo.sk.breaker.front.pages.MainPage

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("front.Main")
object Main {

  @JSExport
  def run(): Unit = {
    MainPage.show()
  }
}
