package start

import java.io.{File, PrintStream, PrintWriter}

import scala.io.Source

/**
  * Created by Wpy on 2016/8/23.
  */
object LongLines {

  def processFile(fileName: String, width: Int): Unit = {
    def processLine(line: String): Unit = {
      if (line.length > width)
        println(fileName + ": " + line.trim)
    }

    val source = Source.fromFile(fileName)
    for (line <- source.getLines())
      processLine(line)
  }

  def printTime(out: PrintStream = Console.out, divisor: Int = 1) = out.println("time = " + System.currentTimeMillis() / divisor)

  /*def main(args: Array[String]): Unit = {
    //    processFile("E:\\projects\\scala\\howtouse\\src\\start\\ChecksumAccumulator.scala", 28)
    printTime(divisor = 200000)
  }*/

  private def fileHere = new File(".").listFiles()

  //使用高階函數
  def fileMatch(mather: String => Boolean) = {
    for (file <- fileHere if mather(file.getName))
      yield file
  }

  def fileContains(query: String) = fileMatch(_.contains(query))

  def fileEnd(query: String) = fileMatch(_.endsWith(query))

  def fileRegex(query: String) = fileMatch(_.matches(query))

  def containsNeg(x: List[Int]) = x.exists(_ < 0)

  def withPrintWriter(file: File)(op: PrintWriter => Unit): Unit = {
    val writer = new PrintWriter(file)
    try {
      op(writer)
    } finally {
      writer.close()
    }
  }

  def op(x: Int)(ff: (Double, Double) => Double) = {
    println(ff(x + 1.0, x))
  }

  val en = false

  def boolNameAssert(predict: => Boolean) {
    if (en && !predict) {
      println("xxxxxxxxxxx")
    }
  }

  def boolAssert(predict: Boolean): Unit = {
    if (en && !predict) {
      println("xxxxxxxxxxx")
    }
  }

  def main(args: Array[String]) {
    /*op(1)(_ - _)
    val file = new File(".")
    withPrintWriter(file)(writer => writer.println(new Date))*/

    boolNameAssert(100 / 0 == 0)
  }
}
