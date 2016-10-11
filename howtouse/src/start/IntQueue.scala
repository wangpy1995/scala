package start

/**
  * Created by Wpy on 2016/9/5.
  */
abstract class IntQueue {

  def put(x: Int)

  def get(): Int

}

class BasicIntQueue extends IntQueue {
  private val bufferIntQueue = new scala.collection.mutable.ArrayBuffer[Int]

  override def put(x: Int): Unit = bufferIntQueue += x

  override def get(): Int = bufferIntQueue.remove(0)
}


trait Increment extends IntQueue {
  abstract override def put(x: Int): Unit = {
    super.put(x + 1)
  }
}

trait Doubled extends IntQueue {
  abstract override def put(x: Int): Unit = {
    super.put(2 * x)
  }
}

object test {
  def main(args: Array[String]) {
    val q = new BasicIntQueue with Doubled with Increment
    q.put(20)
    q.get()
  }
}