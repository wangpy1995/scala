package start

/**
  * Created by Wpy on 2016/9/3.
  */
object QuickSort {

  def sort(arr: Array[Int], left: Int, right: Int): Array[Int] = {
    if (left < right) {
      var low = left
      var high = right
      val key = arr(low)
      while (low < high) {
        while (low < high && arr(high) > key) {
          high -= 1
        }
        arr(low) = arr(high)
        while (low < high && arr(low) < key) {
          low += 1
        }
        arr(high) = arr(low)
      }
      arr(low) = key
      sort(arr, left, low - 1)
      sort(arr, low + 1, right)
    }
    arr
  }

  def main(args: Array[String]): Unit = {
    val arr: Array[Int] = Array(1, 5, 6, 8, 7, 6, 3, 12) //, 54, 22, 98, 51, 24, 42, 36, 6, 5, 1, 4, 2, 4, 5)
    sort(arr, 1, arr.length - 1).foreach(println)
  }
}
