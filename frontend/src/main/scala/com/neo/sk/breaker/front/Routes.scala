package com.neo.sk.breaker.front

object Routes {
  val baseUrl = "/breaker"


  object User {
    val base= baseUrl+"/user"

    val login= base + "/login"
    val logout= base + "/logout"
    val addUser= base + "/addUser"
    val judUser= base + "/judUser"
    val changePsw=base + "/changePsw"
  }
}
