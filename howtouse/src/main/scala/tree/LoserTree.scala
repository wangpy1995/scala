package tree

import scala.collection.mutable.ArrayBuffer

/**
  * b[x] = ls[(x+k)/2]
  *
  * Created by wpy on 2017/3/28.
  */
class LoserTree(val b: Array[Int]) {

  var sum: Int = 0

  private[this] val ls: Array[Int] = new Array[Int](LoserTree.k)

  private val r = new ArrayBuffer[Int]()

  def printR = {
    r.foreach { rs => print(rs + "\t") }
    println
  }

  def create(): Unit = {
    //初始化
    for (i <- ls.indices) {
      ls(i) = LoserTree.MIN_KEY
    }
    var i = LoserTree.k - 1
    while (i >= 0) {
      adjust(ls, i)
      i -= 1
    }
  }

  /**
    * 记录败者到父节点，胜者继续与上一层做比较
    *
    * @param ls
    * @param s
    */
  private[this] def adjust(ls: Array[Int], s: Int): Unit = {
    var x = s
    var tmp = (s + LoserTree.k) / 2
    while (tmp > 0) {
      if (b(x) > b(ls(tmp))) {
        val t = x
        x = ls(tmp)
        ls(tmp) = t
      }
      sum += 1
      tmp /= 2
    }
    ls(0) = x
  }

  def printCount: Unit = {
    println(s"比较次数: $sum")
  }

  def merge(): Unit = {
    for (i <- 0 until LoserTree.k) LoserTree.input(i)
    create()
    r += b(ls(0))
    while (b(ls(0)) != LoserTree.MAX) {
      LoserTree.input(ls(0))
      adjust(ls, ls(0))
      r += b(ls(0))
    }
  }
}

object LoserTree {
  //归并路数
  val k: Int = 5

  //最小key标识比较结束
  val MIN_KEY: Int = 0

  val MAX = 999

  private[this] val b: Array[Int] = new Array[Int](LoserTree.k + 1)
  for (i <- b.indices) b(i) = 0
  b(LoserTree.k) = LoserTree.MIN_KEY

  val a1 = Array[Int](1, 3, 11, 13)
  var t1 = 0
  val a2 = Array[Int](2, 4, 8)
  var t2 = 0
  val a3 = Array[Int](6, 7, 9)
  var t3 = 0
  val a4 = Array[Int](5, 9, 12)
  var t4 = 0
  val a5 = Array[Int](4, 8, 9, 15)
  var t5 = 0

  def input(i: Int) = {
    i match {
      case 0 => if (t1 < a1.length) {
        b(i) = a1(t1)
        t1 += 1
      } else b(i) = MAX
      case 1 => if (t2 < a2.length) {
        b(i) = a2(t2)
        t2 += 1
      } else b(i) = MAX
      case 2 => if (t3 < a3.length) {
        b(i) = a3(t3)
        t3 += 1
      } else b(i) = MAX
      case 3 => if (t4 < a4.length) {
        b(i) = a4(t4)
        t4 += 1
      } else b(i) = MAX
      case 4 => if (t5 < a5.length) {
        b(i) = a5(t5)
        t5 += 1
      } else b(i) = MAX
    }
  }

  def main(args: Array[String]): Unit = {

    val tree = new LoserTree(b)
    tree.merge()
    tree.printCount
    tree.printR
  }
}
