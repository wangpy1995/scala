package typeparam.equality

import typeparam.euqality.{Branch, EmptyTree}

/**
  * Created by wpy on 2017/5/5.
  */
object TestTree {
  def main(args: Array[String]): Unit = {
    val b1 = new Branch[Int](1, new Branch[Int](3, EmptyTree, new Branch[Int](4, EmptyTree, EmptyTree)), EmptyTree)
    val b2 = new Branch[Int](1, new Branch[Int](3, EmptyTree, new Branch[Int](4, EmptyTree, EmptyTree)), EmptyTree)
    println(b1.elem)
    println(b1 == b2)
  }
}