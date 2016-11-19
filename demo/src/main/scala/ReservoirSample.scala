import java.io.{FileWriter, PrintWriter}

import scala.io.Source
import scala.util.Random

/**
  * Created by w5921 on 2016/11/19.
  */
class ReservoirSample {
  def reservoir(file: String, k: Int): Array[String] = {
    val lines = Source.fromFile(file).getLines()
    val arr = new Array[String](k)
    var i = 0

    while (i < k) {
      if (lines.hasNext) {
        arr(i) = lines.next()
        i += 1
      }
    }

    while (lines.hasNext) {
      val line = lines.next()
      val p = new Random(System.nanoTime()).nextInt(i + 1)
      if (p < k)
        arr(p) = line
      i += 1
    }
    arr
  }

}

object TestRandom {
  def main(args: Array[String]): Unit = {
    val writer = new PrintWriter(new FileWriter("words.log", true))
    val reservoir = new ReservoirSample
    for (i <- 1 to 10000000) {
      reservoir.reservoir("words.txt", 1).foreach(line => writer.write(line + "\n"))
    }
    writer.close()
  }

}