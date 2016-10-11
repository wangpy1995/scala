package cn.result

import cn.utils.Element
import cn.utils.Element.elem

/**
  * Created by Wpy on 2016/9/2.
  */

object Spiral {
  val space = elem(" ")
  val corner = elem("+")

  def spiral(nEdges: Int, direction: Int): Element = {
    if (nEdges == 1) elem("+")
    else {
      val sp = spiral(nEdges - 1, (direction + 3) % 4)
      val verticalBar = elem('|', 1, sp.height)
      val horizontalBar = elem('-', sp.width, 1)
      if (direction == 0) {
        (corner beside horizontalBar) above (sp beside space)
      }
      else if (direction == 1) {
        (sp above space) beside (corner above verticalBar)
      }
      else if (direction == 2) {
        (space beside sp) above (horizontalBar beside corner)
      }
      else
        (verticalBar above corner) beside (space above sp)
    }
  }

  def main(args: Array[String]) {
    val nSides = 11
    println(spiral(nSides, 0))
  }
}
