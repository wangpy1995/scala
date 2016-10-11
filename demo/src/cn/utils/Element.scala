package cn.utils

import cn.utils.Element.elem

/**
  * Created by Wpy on 2016/9/23.
  */
abstract class Element {

  def contents: Array[String]

  def height: Int = contents.length

  def width: Int = if (height == 0) 0 else contents(0).length

  def above(that: Element): Element = {
    val this1 = this widen that.width
    val that1 = that widen this.width
    elem(this1.contents ++ that1.contents)
  }

  def beside(that: Element): Element = {
    val this1 = this heighten that.height
    val that1 = that heighten this.height
    elem(for ((line1, line2) <- this1.contents zip that1.contents) yield line1 + line2)
  }

  def widen(w: Int): Element = {
    if (w <= width)
      this
    else {
      val left = elem(' ', (w - width) / 2, height)
      val right = elem(' ', w - width - left.width, height)
      left beside this beside right
    }
  }

  def heighten(h: Int): Element = {
    if (h <= height)
      this
    else {
      val top = elem(' ', width, (h - height) / 2)
      val bot = elem(' ', width, h - height - top.height)
      top above this above bot
    }
  }

  override def toString = contents mkString "\n"

}

object Element {
  def elem(con: Array[String]): Element = {
    new ArrayElement(con)
  }

  def elem(line: String): Element = {
    new LineElement(line)
  }

  def elem(ch: Char, width: Int, height: Int): Element = {
    new UniformElement(ch, width, height)
  }
}

private class ArrayElement(con: Array[String]) extends Element {
  override def contents: Array[String] = con
}

private class LineElement(s: String) extends Element {
  override def contents: Array[String] = Array(s)

  override val height = 1
  override val width = s.length
}

private class UniformElement(ch: Char, width: Int, height: Int) extends Element {
  private val line = ch.toString * width

  //填充字符串
  override def contents: Array[String] = Array.fill(height) {
    line
  }
}