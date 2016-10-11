package cn.variables

/**
  * Created by Wpy on 2016/10/9.
  *
  * 用於測試擴展變量為getter setter方法
  */
class Time {

  private[this] var h = 12
  private[this] var m = 60

  def hour: Int = h

  def hour_=(x: Int) {
    require(0 <= x && x <= 24)
    h = x
  }

  def minute: Int = m

  def minute_=(x: Int) {
    require(0 <= x && x <= 60)
    m = x
  }
}

/**
  * 攝氏轉華氏
  *
  * 只定義變量而不關聯字段
  */
class Thermometer {

  var celsius: Float = _

  def fahrenheit = celsius * 9 / 5 + 32

  def fahrenheit_=(f: Float) {
    celsius = (f - 32) * 5 / 9
  }

  override def toString = fahrenheit + "℉/" + celsius + "℃"
}
/*
object Thermometer {
  def main(args: Array[String]) {
    val t = new Thermometer
    t.fahrenheit_=(92.32f)
    println(t)
  }
}*/

