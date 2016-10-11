package start

import java.io.File

/**
  * Created by Wpy on 2016/8/22.
  */
class Rational(n: Int, d: Int) {
  require(d != 0)
  println(n + "/" + d)

  val number = n
  val denom = d
  private val g = gcd(number, denom)

  override def toString = n / g + "/" + d / g

  def add(that: Rational) =
    println(new Rational(number * that.denom + denom * that.number, denom * that.denom))

  def lessThan(that: Rational) =
    number * that.denom < that.number * denom

  def max(that: Rational) =
    if (lessThan(that)) that else this

  private def gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)

  def +(i: Int) =
    new Rational(number + i * denom, denom)

  def *(i: Int) =
    new Rational(number * i, denom)
}

object Rational {
  def main(args: Array[String]): Unit = {
    println(foundIt(0))
  }

  val file = new File("E:\\projects\\scala\\howtouse\\src\\start").listFiles()


  def scalaFiles =
    for {
      f <- file if f.getName.endsWith(".scala")
    } yield f

  def foundIt(i: Int): Boolean = {
    if (i < file.length - 1)
      if (file(i).getName.startsWith("X"))
        file(i).getName.endsWith(".scala")
      else
        foundIt(i + 1)
    else
      false
  }


}


