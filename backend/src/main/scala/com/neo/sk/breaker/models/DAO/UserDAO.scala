package com.neo.sk.breaker.models.DAO


import com.neo.sk.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.breaker.models.SlickTables._
import com.neo.sk.breaker.Boot.executor
import net.sf.ehcache.search.aggregator.Count

import scala.concurrent.Future
object UserDAO {
  def searchUser(id: Long) = {
    db.run(tUsers.filter(_.id === id).result.head)
  }

  def searchUserByName(name: String) = {
    db.run(tUsers.filter(i => i.name === name).result.headOption)
  }


  def changePsw(id:Long,newpsw:String)={
    db.run(tUsers.filter(_.id === id).map(_.psw).update(newpsw))
  }

  def adduser(name:String,psw:String)={
    db.run(tUsers += rUsers(1, name, psw))
  }


}
