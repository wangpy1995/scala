package typeparam.euqality

/**
  * Created by wpy on 2017/5/5.
  */
trait Tree[+T] {
  def elem: T

  def left: Tree[T]

  def right: Tree[T]
}

/**
  * 空树
  */
object EmptyTree extends Tree[Nothing] {
  override def elem: Nothing = throw new NoSuchElementException("EmptyTree.elem")

  override def left: Tree[Nothing] = throw new NoSuchElementException("EmptyTree.left")

  override def right: Tree[Nothing] = throw new NoSuchElementException("EmptyTree.right")
}

/**
  * 表示非空树
  *
  * @param elem
  * @param left
  * @param right
  * @tparam T
  */
class Branch[+T](
                  val elem: T,
                  val left: Tree[T],
                  val right: Tree[T]
                ) extends Tree[T] {
  override def equals(obj: scala.Any): Boolean = obj match {
    case that: Branch[t] => this.elem == that.elem &&
      this.left == that.left &&
      this.right == that.right
    case _ => false
  }

  override def hashCode(): Int = 41 * (41 * (41 + elem.hashCode()) + left.hashCode()) + right.hashCode()

  def canEqual(obj: Any): Boolean = obj.isInstanceOf[Branch[_]]
}
