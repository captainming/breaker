package com.neo.sk.breaker.front.pages

import com.neo.sk.breaker.front.common.PageSwitcher
import mhtml._
import org.scalajs.dom
import scala.xml.Elem

/**
  * created bt ltm on 2/16/2019
  */
object MainPage extends PageSwitcher{

  private val currentPage: Rx[Elem] = currentHashVar.map{

    case "login" :: Nil => EntryPage.render
    case "register" :: Nil => RegisterPage.render
    case _ => println("default"); EntryPage.render
  }

  def show(): Cancelable = {
    switchPageByHash()
    val page =
      <div>
        {currentPage}
      </div>
    mount(dom.document.body, page)
  }

}
