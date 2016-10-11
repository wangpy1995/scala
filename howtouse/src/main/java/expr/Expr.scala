package expr

import start.Element
import start.Element.elem

/**
  * 使用二維佈局展示數學表達式
  * Created by Wpy on 2016/9/19.
  */
sealed abstract class Expr

case class Var(name: String) extends Expr

case class Number(num: Double) extends Expr

case class UnOp(operator: String, arg: Expr) extends Expr

case class BinOp(operator: String, left: Expr, right: Expr) extends Expr

/**
  * 表達式格式化
  */
class ExprFormatter {

  //定義遞增優先級的操作符
  private val opGroups = Array(
    Set("|", "||"),
    Set("&", "&&"),
    Set("^"),
    Set("=", "!="),
    Set("<", "<=", ">", ">="),
    Set("+", "-"),
    Set("*", "%")
  )

  //操作符優先級的映射
  private val precedence = {
    val assocs =
      for {
        i <- 0 until opGroups.length
        op <- opGroups(i)
      } yield op -> i
    Map() ++ assocs
  }

  //一元操作符比二元操作符優先級高
  private val unaryPrecedence = opGroups.length

  //分數使用垂直佈局，優先級定為-1方便操作
  private val fractionPrecedence = -1

  private def format(e: Expr, enclPrec: Int): Element = {

    e match {
      case Var(name) => elem(name)
      case Number(num) =>
        def stripDots(s: String) =
          if (s endsWith ".0") s.substring(0, s.length - 2)
          else s
        elem(stripDots(num.toString))
      case UnOp(operator, arg) =>
        elem(operator) beside format(arg, unaryPrecedence)
      case BinOp("/", left, right) =>
        val top = format(left, fractionPrecedence)
        val bot = format(right, fractionPrecedence)
        val line = elem('-', top.width max bot.width, 1)
        val frac = top above line above bot
        if (enclPrec != fractionPrecedence) frac
        else elem(" ") beside frac beside elem(" ")
      case BinOp(operator, left, right) =>
        val opPrec = precedence(operator)
        val l = format(left, opPrec)
        val r = format(right, opPrec)
        val op = l beside elem(" " + operator + " ") beside r
        if (enclPrec <= opPrec) op
        else elem("(") beside op beside elem(")")
    }
  }

  def format(e: Expr): Element = format(e, 0)
}

object ExprFormatter {
  def main(args: Array[String]) {
    val f = new ExprFormatter

    val a = new Number(20)
    val b = new Number(12)
    val c = new Number(25)

    val d = new BinOp("/", b, a)
    val e = new BinOp("+", c, d)

    val g = new BinOp("/", d, e)

    println("\n\n")
    println(f format g)
  }
}
