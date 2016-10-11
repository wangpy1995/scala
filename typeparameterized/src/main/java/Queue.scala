import scala.collection.immutable.Nil

/**
  * 函数式队列实现
  * 分为两部分，实现接近常量时间的append，tail，head操作
  *
  * Created by Tex on 2016/10/11.
  */
/*class Queue[T] private(
                        private val leading: List[T],
                        private val trailing: List[T]
                      ) {
  private def mirror =
    if (leading isEmpty)
      new Queue(trailing.reverse, Nil)
    else
      this

  def head = mirror.leading.head

  def tail = {
    val q = mirror
    new Queue(q.leading.tail, q.trailing)
  }

  def append(x: T) =
    new Queue(leading, x :: trailing)
}


//伴生对象的apply工厂方法
object Queue {
  //用初始元素xs构造队列
  def apply[T](xs: T*) = new Queue[T](xs.toList, Nil)
}*/
trait Queue[T] {
  def head: T

  def tail: Queue[T]

  def append(x: T): Queue[T]
}

//私有类隐藏类信息
object Queue {
  def apply[T](xs: T*): Queue[T] =
    new QueueImpl[T](xs.toList, Nil)

  private class QueueImpl[T](
                              private val leading: List[T],
                              private val trailing: List[T]
                            ) extends Queue[T] {
    def mirror =
      if (leading isEmpty)
        new QueueImpl(trailing.reverse, Nil)
      else
        this

    def head: T = mirror.leading.head

    def tail: QueueImpl[T] = {
      val q = mirror
      new QueueImpl(trailing.tail, q.trailing)
    }

    def append(x: T) =
      new QueueImpl(leading, x :: trailing)
  }

}