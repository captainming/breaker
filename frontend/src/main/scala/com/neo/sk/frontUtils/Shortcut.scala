package com.neo.sk.frontUtils

import org.scalajs.dom

import scala.scalajs.js.Date

/**
  * User: Taoz
  * Date: 12/2/2016
  * Time: 11:12 AM
  */
object Shortcut {

  def redirect(url: String): Unit = {
    val origin=dom.window.location.origin
    dom.window.location.href = origin + url
  }
  def hashChange(hash:String):Unit={
   if(hash!= dom.window.location.hash){
     dom.window.localStorage.setItem("localHash",hash)
     dom.window.location.hash=hash
   }
  }

  def setTitle(title: String): Unit = {
    dom.document.title = title
  }

  def addMobileMeta() = {
    import scalatags.JsDom.short._
    val oMeta =
      meta(
        *.name := "viewport",
        *.content := "width=device-width, initial-scale=1, maximum-scale=1"
      ).render
    dom.document.head.appendChild(oMeta)
  }

  def getUrlParams: Map[String, String] = {
    val paramStr =
      Option(dom.document.getElementById("fakeUrlSearch"))
        .map(_.innerHTML).getOrElse(dom.window.location.search)

    val str1 = paramStr.substring(1)
    val pairs = str1.split("&").filter(s => s.length > 0)
    val tmpMap = pairs.map(_.split("=", 2)).filter(_.length == 2)
    tmpMap.map(d => (d(0), d(1))).toMap
  }


  def errorDetailMsg(t: Throwable, line: Int = 5): String = {
    val stack = t.getStackTrace.take(line).map(t => t.toString).mkString("\n")
    val msg = t.getMessage
    val localMsg = t.getLocalizedMessage
    s"msg: $msg \nlocalMsg: $localMsg \n stack: $stack"
  }


  def formatyyyyMMdd(date: Date) = {
    val y = date.getFullYear()
    val m = date.getMonth() + 1 match {
      case x if x <= 9 => "0" + x
      case x => x.toString
    }
    val d = date.getDate() match {
      case x if x <= 9 => "0" + x
      case x => x.toString
    }
    y + m + d
  }

  def formatyyyyMM(date: Date) = {
    val y = date.getFullYear()
    val m = date.getMonth() + 1 match {
      case x if x <= 9 => "0" + x
      case x => x.toString
    }
    y + m
  }

  def formatyyyyMMddHHmm(timeMs: Long) = {
    val date = new Date(timeMs)
    val y = date.getFullYear()
    val m = date.getMonth() + 1 match {
      case x if x <= 9 => "0" + x
      case x => x.toString
    }
    val d = date.getDate() match {
      case x if x <= 9 => "0" + x
      case x => x.toString
    }

    val h = date.getHours() match {
      case x if x <= 9 => "0" + x
      case x => x.toString
    }

    val mm = date.getMinutes() match {
      case x if x <= 9 => "0" + x
      case x => x.toString
    }

    y +"-"+ m +"-"+ d + "  " + h + ":" + mm
  }


}
