package com.neo.sk.breaker.snake

import com.neo.sk.hiStream.snake.BkMap
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


  private[this] var waitingJoin = Map.empty[Long, String]


  var currentRank = List.empty[Score]
  private[this] var historyRankMap = Map.empty[Long, Score]
  var historyRankList = historyRankMap.values.toList.sortBy(_.s).reverse

  private[this] var historyRankThreshold = if (historyRankList.isEmpty) -1 else historyRankList.map(_.s).min
  def addPlayer(id: Long, name: String) = waitingJoin += (id -> name)


  private[this] def genWaitingBreaker() = {
    waitingJoin.filterNot(kv => breakers.contains(kv._1)).foreach { case (id, name) =>
      val header = Point(50, 50)
      val center = Point(60, 49)
      breakers += id -> Breaker(id, name, Sk(id, header, 20, Random.nextInt(3) + 1), Bl(id, center, random.nextInt(3) + 1, Point(0, 0)))
    }
    waitingJoin = Map.empty[Long, String]
  }


  private[this] def updateRanks() = {
    currentRank = breakers.values.map(s => Score(s.id, s.name, s.score)).toList.sortBy(s => s.s)
    var historyChange = false
    currentRank.foreach { cScore =>
      historyRankMap.get(cScore.id) match {
        case Some(oldScore) if cScore.s > oldScore.s =>
          historyRankMap += (cScore.id -> cScore)
          historyChange = true
        case None if cScore.s > historyRankThreshold =>
          historyRankMap += (cScore.id -> cScore)
          historyChange = true
        case _ =>
      }
    }

    if (historyChange) {
      historyRankList = historyRankMap.values.toList.sortBy(t => t.s).take(historyRankLength)
      historyRankThreshold = historyRankList.lastOption.map(_.s).getOrElse(-1)
      historyRankMap = historyRankList.map(s => s.id -> s).toMap
    }

  }


  override def update(): Unit = {
    super.update()
    genWaitingBreaker()
    updateRanks()
  }


}
