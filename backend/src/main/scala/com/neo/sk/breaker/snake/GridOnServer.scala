package com.neo.sk.breaker.breaker

import com.neo.sk.breaker.breaker.BkMap
import org.slf4j.LoggerFactory

import scala.util.Random

/**
  * User: Taoz
  * Date: 9/3/2016
  * Time: 9:55 PM
  */
class GridOnServer(override val boundary: Point) extends Grid {


  private[this] val log = LoggerFactory.getLogger(this.getClass)

  override def debug(msg: String): Unit = log.debug(msg)

  override def info(msg: String): Unit = log.info(msg)


  var waitingJoin = Map.empty[Long, String]
  var currentRank = List.empty[Score]
  private[this] var historyRankMap = Map.empty[Long, Score]
  var historyRankList = historyRankMap.values.toList.sortBy(_.s).reverse

  private[this] var historyRankThreshold = if (historyRankList.isEmpty) -1 else historyRankList.map(_.s).min
  def addPlayer(id: Long, name: String) = waitingJoin += (id -> name)


  override def init(): Unit = {
    super.init()
    waitingJoin = Map.empty[Long, String]
    currentRank = List.empty[Score]
  }

  private[this] def genWaitingBreaker() = {
    waitingJoin.filterNot(kv => breakers.contains(kv._1)).foreach { case (id, name) =>
      val header1 = Point(25, 50)
      val center1 = Point(35, 49)
      val header2 = Point(85, 50)
      val center2 = Point(95, 49)
      if (breakers.isEmpty)
        breakers += id -> Breaker(id, name, Sk(id, header1, 15, 1), Bl(id, center1, 1, Point(0, 0)))
      else
        breakers += id -> Breaker(id, name, Sk(id, header2, 15, 2), Bl(id, center2, 2, Point(0, 0)))
    }
    waitingJoin = Map.empty[Long, String]
  }


  private[this] def updateRanks() = {
    currentRank = breakers.values.map(s => Score(s.id, s.name, s.score)).toList.sortBy(s => s.s)
  }


  override def update(): Unit = {
    super.update()
    genWaitingBreaker()
    updateRanks()
  }


}
