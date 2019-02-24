package com.neo.sk.breaker.breaker

import com.neo.sk.breaker.breaker.{Bk, Boundary}


object BkMap {

  def map1 = {
    var f = List.empty[Bk]
    val widthUnit = Boundary.w / 30
    val heightUnit = Boundary.h / 30
    for (i <- 4 to 24 if (i % 2 == 0)){
      f ::= Bk(3, i * widthUnit, 2 * heightUnit)
      f ::= Bk(5, i * widthUnit, 4.toFloat * heightUnit)
      f ::= Bk(10, i * widthUnit, 6 * heightUnit)
      f ::= Bk(5, i * widthUnit, 8.toFloat * heightUnit)
      f ::= Bk(3, i * widthUnit, 10 * heightUnit)
    }
    f
  }

  def map2 = {
    var f = List.empty[Bk]
    val widthUnit = Boundary.w / 30
    val heightUnit = Boundary.h / 30

  }

  def BlockMap : Map[Int, List[Bk]] = {
    Map(1 -> map1)
  }
}
