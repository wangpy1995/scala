package cases

/**
  * 偏函数
  * Created by w5921 on 2016/11/6.
  */
object PartialFuncDemo {
  def func1: PartialFunction[String, Int] = {
    case "one" => 1
    case "two" => {
      println("two")
      2
    }
    case _ => -1
  }

  def func2(num: String): Int = num match {
    case "onr" => 1
    case "two" => 2
    case _ => -1
  }

  def main(args: Array[String]): Unit = {
    println(func1("two"))
    println(func2("two"))
  }
}
