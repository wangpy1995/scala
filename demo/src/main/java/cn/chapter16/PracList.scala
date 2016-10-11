package cn.chapter16

/**
  * Created by Wpy on 2016/9/26.
  */
object PracList {

  /**
    * List類型
    *
    * @example val fruit: List[String] = fruit
    * @example val nums: List[Int] = nums
    * @example val diag3: List[ List [Int] ] = diag3
    * @example val empty: List[Nothing] = empty
    **/
  //列表字面量
  val fruit = List("apple", "orange", "banana", "mango", "grape")
  val nums = List(2, 4, 6, 8, 10)
  val diag3 = List(
    List(0, 0, 1),
    List(0, 1, 0),
    List(1, 0, 0)
  )
  val empty = List()

  /**
    * 使用模式匹配做拆分的插入排序
    *
    * @param xs
    * @return
    */
  def isort(xs: List[Int]): List[Int] = xs match {
    case List() => List()
    case x :: xs1 => insert(x, isort(xs1))
  }

  def insert(x: Int, xs: List[Int]): List[Int] = xs match {
    case List() => List()
    case y :: ys => if (x <= y) x :: ys else y :: insert(x, ys)
  }

  /**
    * List中"分治"的"手工"實現
    *
    * @param xs
    * @param ys
    * @tparam T
    * @return
    */
  def append[T](xs: List[T], ys: List[T]): List[T] = xs match {
    case List() => ys
    case x :: xs1 => x :: append(xs1, ys)
  }

  def mergeSort[T](xs: List[T])(compare: (T, T) => Boolean): List[T] = {
    def merge(xs: List[T], ys: List[T]): List[T] = (xs, ys) match {
      case (Nil, _) => ys
      case (_, Nil) => xs
      case (x :: xs1, y :: ys1) => if (compare(x, y)) x :: merge(xs1, ys) else y :: merge(xs, ys1)
    }

    val n = xs.length / 2
    if (n == 0) xs
    else {
      val (ys, zs) = xs splitAt n
      merge(mergeSort(ys)(compare), mergeSort(zs)(compare))
    }
  }

  /**
    * 左結合實現列表倒序
    *
    * @param xs
    * @tparam T
    * @return
    */
  def reverseLeft[T](xs: List[T]) = (List[T]() /: xs) ((ys, y) => y :: ys)

  //  def reverseRight[T](xs: List[T]) = (xs :\ List[T]()) ((ys, y) => y :: ys)

  def main(args: Array[String]) {
    val l = List(2, 8, 5, 6, 9, 7, 1, 5, 7, 54, 56, 46, 8, 131, 3, 11)
    for (m <- mergeSort(l)(_ < _)) {
      print(m + " ")
    }
  }
}