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

  private def bfs[T](rule: (T, T) => Boolean)(left: T, right: ListBuffer[T], queue: mutable.Queue[T], results: ListBuffer[T]): Unit = {
    val b = new ListBuffer[T]()
    var l: T = left
    results += l
    var i = 0
    right.remove(0)
    right.foreach { r =>
      i += 1
      if (rule(l, r)) {
        queue.enqueue(r)
        i -= 1
        right.remove(i)
      }
      sum+=1
    }
    while (right.nonEmpty) {
      while (queue.nonEmpty) {
        l = queue.dequeue()
        results += l
        i=0
        right.foreach { r =>
          i += 1
          if (rule(l, r)) {
            queue.enqueue(r)
            i -= 1
            right.remove(i)
          }
          sum+=1
        }
      }
      println(results.mkString(","))
      x += results
      if (right.length > 1) {
        results.clear()
        queue.enqueue(right.head)
        right.remove(0)
      }else{
        println(right.mkString(","))
      }
    }
  }

  def build[T](rule: (T, T) => Boolean)(data: ListBuffer[T]): ListBuffer[T] = {
    val results = new ListBuffer[T]()
    val queue = new mutable.Queue[T]()
    bfs(rule)(data.head, data, queue, results)
    results
  }

}

sealed class Node[T](val v: T) {
  var left: Node[_ <: T] = null
  var right: Node[_ <: T] = null
}
