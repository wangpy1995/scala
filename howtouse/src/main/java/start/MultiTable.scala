package start

/**
  * Created by Wpy on 2016/9/1.
  */
object MultiTable {

  def makeRowSeq(row: Int) =
    for (col <- 1 to row) yield {
      val prod = (row * col).toString
      val padding = " " * (4 - prod.length)
      padding + prod
    }


  def makeRow(row: Int) = makeRowSeq(row).mkString

  def multiTable() = {
    val tableSeq =
      for (row <- 1 to 10) yield
        makeRow(row)
    tableSeq.mkString("\n")
  }

  def main(args: Array[String]) {
    println(multiTable())
  }
}
