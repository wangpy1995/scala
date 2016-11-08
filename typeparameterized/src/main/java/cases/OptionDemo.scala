package cases

/**
  * Created by w5921 on 2016/11/6.
  */
object OptionDemo {
  def main(args: Array[String]): Unit = {
    val map = Map("a" -> 1, "b" -> 2)

    map.get("a") match {
      case Some(i) => i
      case None => 0
    }
    val v = map.get("b") match {
      case Some(i) => i
      case None => 0
    }
    println(v)
    //等同上述match语句
    val v1 = map.getOrElse("c", 0)
    println(v1)
  }
}
