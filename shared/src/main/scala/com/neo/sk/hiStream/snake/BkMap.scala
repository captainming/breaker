package com.neo.sk.hiStream.snake

import com.neo.sk.breaker.snake.{Bk, Boundary}


object BkMap {

  def map1 = {
    var f = List.empty[Bk]
    val widthUnit = Boundary.w / 36
    val heightUnit = Boundary.h / 20
    for (i <- 6 to 30 if (i % 2 == 0)){
      f ::= Bk(3, i * widthUnit, 2 * heightUnit)
      f ::= Bk(5, i * widthUnit, 3.5.toFloat * heightUnit)
      f ::= Bk(10, i * widthUnit, 5 * heightUnit)
      f ::= Bk(5, i * widthUnit, 6.5.toFloat * heightUnit)
      f ::= Bk(3, i * widthUnit, 8 * heightUnit)
    }
    f
  }

  def BlockMap : Map[Int, List[Bk]] = {
    Map(1 -> map1)
  }
}
