package typeparam.privatedata

/**
  * Created by wpy on 17-1-8.
  */
class Queue[+T] private(
                         private[this] var leading: List[T],
                         private[this] var trailing: List[T]
                       ) {

  private def mirror() = if (leading.isEmpty) {
    while (trailing.nonEmpty) {
      leading = trailing.head :: leading
      trailing = trailing.tail
    }
  }

  def head: T = {
    mirror()
    leading.head
  }

  def tail: Queue[T] = {
    mirror()
    new Queue(leading.tail, trailing)
  }

  def append[U >: T](x: U): Queue[U] = {
    new Queue[U](leading, x :: trailing)
  }
}
