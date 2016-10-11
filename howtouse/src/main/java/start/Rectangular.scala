package start

/**
  * Created by Wpy on 2016/9/5.
  */
trait Rectangular {
  def topLeft: Point

  def bottomRight: Point

  def left = topLeft.x

  def right = bottomRight.x

  def top = topLeft.y

  def bottom = bottomRight.y

  def width = right - left

  def height = top - bottom
}

class Point(val x: Int, val y: Int)
