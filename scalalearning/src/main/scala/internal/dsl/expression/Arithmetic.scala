package internal.dsl.expression

import scala.util.parsing.combinator.JavaTokenParsers

/**
  * Created by wpy on 17-5-22.
  */
class Arithmetic extends JavaTokenParsers {

  def expr: Parser[Any] = term ~ rep("+" ~ term | "-" ~ term)

  def term: Parser[Any] = factor ~ rep("*" ~ factor | "/" ~ factor)

  def factor: Parser[Any] = floatingPointNumber | "(" ~ expr ~ ")"
}

object ParseExpr extends Arithmetic {
  def main(args: Array[String]): Unit = {
    val str = "(1.0+3.0)+95.0*1.2+1.2/1.5"
    println("input : " + str)
    println(parseAll(expr, str))
  }
}