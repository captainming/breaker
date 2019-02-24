package com.neo.sk.utils

import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import slick.jdbc.PostgresProfile.api._

/**
  * User: Jason
  * Date: 2018/10/22
  * Time: 14:50
  */





object DBUtil {
  val log = LoggerFactory.getLogger(this.getClass)
  private val dataSource = createDataSource()


  import com.neo.sk.breaker.common.AppSettings
  private def createDataSource() = {

    val dataSource = new org.postgresql.ds.PGSimpleDataSource()

    //val dataSource = new MysqlDataSource()

//    log.info(s"connect to db: $slickUrl")
    dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres")
    dataSource.setUser("teemo")
    dataSource.setPassword("960812lan")

    val hikariDS = new HikariDataSource()
    hikariDS.setDataSource(dataSource)
    hikariDS.setMaximumPoolSize(3)
    hikariDS.setConnectionTimeout(30000)
    hikariDS.setIdleTimeout(300000)
    hikariDS.setMaxLifetime(900000)
    hikariDS.setAutoCommit(true)
    hikariDS
  }


  val db = Database.forDataSource(dataSource, Some(3))




}