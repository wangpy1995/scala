package abstractfield.field

/**
  * Created by wpy on 17-1-8.
  */
class AbstractField {

}

trait RationalTrait {
  val numArg: Int
  val denimArg: Int

  private lazy val g: Int = {
    require(denimArg != 0)
    gcd(numArg, denimArg)
  }
  lazy val number: Int = numArg / g
  lazy val denim: Int = denimArg / g

  def gcd(a: Int, b: Int): Int = {
    if (b == 0) a else gcd(b, a % b)
  }

  override def toString: String = number + "/" + denim
}

object Test {
  //初始化抽象成员方法
  def main(args: Array[String]): Unit = {
    val x = 2
    //预初始化field
    println(new {
      val numArg: Int = 1 * x
      val denimArg: Int = 2 * x
    } with RationalTrait)

    //使用懒值
    println(new RationalTrait {
      override val numArg: Int = 1 * x
      override val denimArg: Int = 2 * x
    })
  }
}