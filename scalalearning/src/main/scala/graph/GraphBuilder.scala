package graph

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.reflect.ClassTag

object GraphBuilder {
  var sum = 0
  val x = new ArrayBuffer[Seq[_]]()

  private def breadthFirstTraversal[T](rule: (T, T) => Boolean)(data: Seq[T], results: ArrayBuffer[Seq[T]]): Unit = {
    if (data.length > 1) {
      val left = data.head
      val arr = new ArrayBuffer[T]()
      var count = 0
      val newData = data.filter { d =>
        count += 1
        val b = rule(left, d)
        if (b) arr += d
        !b
      }
      sum += count
      results += arr
      breadthFirstTraversal(rule)(newData, results)
    } else results += data
  }

  private def bfs[T](rule: (T, T) => Boolean)(left: T, right: ArrayBuffer[T], queue: mutable.Queue[T], results: ArrayBuffer[T]): Unit = {
    var l: T = left
    val a = right.filter(rule(left, _))
    right --= a
    queue.enqueue(a: _*)
    while (right.nonEmpty) {
      while (queue.nonEmpty) {
        sum += 1
        l = queue.dequeue()
        results += l
        val b = right.filter(rule(l, _))
        right --= b
        queue.enqueue(b: _*)
      }
      println(results.mkString(","))
      if(right.length>1) {
        results.clear()
        queue.enqueue(right.head)
      }
    }
  }

  def build[T](rule: (T, T) => Boolean)(data: ArrayBuffer[T]): ArrayBuffer[T] = {
    val results = new ArrayBuffer[T]()
    val queue = new mutable.Queue[T]()
    bfs(rule)(data.head, data, queue, results)
    results
  }

}

sealed class Node[T](val v: T) {
  var left: Node[_ <: T] = null
  var right: Node[_ <: T] = null
}
