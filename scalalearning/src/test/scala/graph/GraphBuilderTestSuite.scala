package graph

import org.scalatest.FunSuite
import typeparam.privatedata.Queue

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.Random

class GraphBuilderTestSuite extends FunSuite {

  val testData = Seq(0, 1, 2, 6, 7, 8, 9, 11, 14, 15, 17)

  val rules = (a: Int, b: Int) => Math.abs(a - b) < 3

  test("build") {
    println(testData.mkString(","))
    GraphBuilder.build(rules)(ListBuffer.empty ++= testData)
    println(GraphBuilder.x.mkString("\n"))
    println(GraphBuilder.sum)
  }

  test("tree") {
    val nodes = (0 to 10).map(new Node(_))
    nodes(1).left = nodes(2)
    nodes(1).right = nodes(3)

    nodes(2).left = nodes(4)
    nodes(2).right = nodes(5)

    nodes(3).left = nodes(6)

    nodes(6).left = nodes(7)
    nodes(6).right = nodes(8)

    nodes(8).left = nodes(9)
    nodes(8).right = nodes(10)

    val top = nodes(1)

    def bfs[T](top: Node[T]): Unit = {
      val queue = new mutable.Queue[Node[_<:T]]()
      if (top.left != null) queue.enqueue(top.left)
      if (top.right != null) queue.enqueue(top.right)
      while (queue.nonEmpty){
        val node = queue.dequeue()
        println(node.v)
        if (node.left != null) queue.enqueue(node.left)
        if (node.right != null) queue.enqueue(node.right)
      }
    }

    bfs(top)
  }

}
