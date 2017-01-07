package typeparam.privatedata

/**
  * Created by wpy on 17-1-8.
  */

//协变
class Cell[+T, -U](init: T) {
  private[this] val current: T = init
  private[this] var next: U = _

  def get: T = current

  def newParent[R >: T](x: R): Cell[R, U] = {
    new Cell[R, U](x)
  }

  def setValue(n: U): Unit = {
    next = n
  }

  def printValue(): Unit = {
    println(current + " " + next)
  }
}
