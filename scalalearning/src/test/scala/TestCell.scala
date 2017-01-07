import typeparam.privatedata.Cell

/**
  * Created by wpy on 17-1-8.
  */
object TestCell {
  def main(args: Array[String]): Unit = {
    val c = new Cell[Int, Any](1)
    c.printValue()
    c.setValue(new Object)
    c.printValue()

    val d: Cell[Any, Int] = c
    d.printValue()
  }

}
