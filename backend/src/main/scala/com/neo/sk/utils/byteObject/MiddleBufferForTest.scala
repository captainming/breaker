package com.neo.sk.utils.byteObject

import com.neo.sk.hiStream.utils.MiddleBuffer

import scala.collection.mutable.ArrayBuffer

/**
  * User: Taoz
  * Date: 7/15/2018
  * Time: 10:50 AM
  */
class MiddleBufferForTest(
  private val internalList: ArrayBuffer[String] = new ArrayBuffer[String]()
) extends MiddleBuffer {


  private var index = 0

  override def clear(): Unit = {
    internalList.clear()
    index = 0
  }

  override def result(): List[String] = internalList.toList

  override def putByte(b: Byte): MiddleBuffer = {
    internalList.append(b.toString)
    this
  }

  override def putInt(i: Int): MiddleBuffer = {
    internalList.append(i.toString)
    this
  }

  override def putFloat(f: Float): MiddleBuffer = {
    internalList.append(f.toString)
    this
  }

  override def getByte(): Byte = {
    val b = internalList(index).toByte
    index += 1
    b
  }

  override def getInt(): Int = {
    val i = internalList(index).toInt
    index += 1
    i
  }


  override def getFloat(): Float = {
    val f = internalList(index).toFloat
    index += 1
    f
  }

  override def getString(): String = {
    val s = internalList(index)
    index += 1
    s
  }

/*
  def back(): Unit = {
    index -= 1
  }
*/

  override def putString(s: String): MiddleBuffer = {
    internalList.append(s)
    this
  }

}
