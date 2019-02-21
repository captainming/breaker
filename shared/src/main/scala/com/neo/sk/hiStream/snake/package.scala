package com.neo.sk.breaker

import javafx.scene.effect.Light.Spot


/**
  * User: Taoz
  * Date: 8/29/2016
  * Time: 9:48 PM
  */
/**
  * used by ltm on 2/17/2019
  */
package object snake {

  sealed trait Spot
  case class Body(id: Long, life: Int) extends Spot
  case class Header(id: Long, life: Int) extends Spot
  case class Apple(score: Int, life: Int) extends Spot
  case class Block(score: Int) extends Spot
  case class Stick(id: Long, length: Int, color: String) extends Spot
  case class Ball(id: Long, color: String, direction: Point, speed: Int = start) extends Spot

  case class Score(id: Long, n: String, k: Int, l: Int, t: Option[Long] = None)
  case class Bd(id: Long, life: Int, x: Float, y: Float)
  case class Ap(score: Int, life: Int, x: Float, y: Float)
  case class Bk(score:Int, x: Float, y: Float)
  case class Sk(id: Long, position: Point, length: Int, color: String)
  case class Bl(id: Long, position: Point, color: String, direction: Point, speed: Int = start)

  case class Point(x: Float, y: Float) {
    def +(other: Point) = Point(x + other.x, y + other.y)

    def -(other: Point) = Point(x - other.x, y - other.y)

    def *(n: Int) = Point(x * n, y * n)

    def /(value: Float) = Point(x / value, y / value)

    def %(other: Point) = Point(x % other.x, y % other.y)
  }

  case class SkDt(
    id: Long,
    name: String,
    header: Point = Point(20, 20),
    direction: Point = Point(1, 0),
    length: Int = 4,
    kill: Int = 0
  )

  case class Breaker(
    id: Long,
    name: String,
    header: Point,
    sLength: Int = 20,
    score: Int = 0
  )

  object Boundary{
    val w = 120
    val h = 60
  }

  def start = 16



}
