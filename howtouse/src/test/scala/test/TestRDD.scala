package test

import java.io.{File, IOException}
import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousFileChannel, CompletionHandler}
import java.nio.file.StandardOpenOption
import java.util.concurrent.CountDownLatch

import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.{Partition, SparkConf, SparkContext, TaskContext}

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag
import scala.util.Sorting

/**
  * Created by wpy on 17-6-8.
  */
object TestRDD extends Logging {
  val sparkConf = new SparkConf().setAppName("test_RDD").setMaster("local[*]")
  val sc = SparkContext.getOrCreate(sparkConf)
  val path = "/home/wpy/tmp/test/"
  val out = "/home/wpy/tmp/out"

  def main(args: Array[String]): Unit = {
    val files = new File(path).listFiles()
    new MyRDD[(Int, String)](sc, files).mapPartitions { part =>
      val data = part.toSeq.filter(_ != null)
      Sorting.stableSort(data, (a: (Int, String), b: (Int, String)) => a._1 < b._1).iterator
    }.saveAsTextFile(out)
    while (true)
      Thread.sleep(1000)
  }
}

class MyPartition[T](idx: Int, val value: ArrayBuffer[T]) extends Partition {
  override def index: Int = idx
}

class MyRDD[T: ClassTag](@transient sc: SparkContext, files: Array[File]) extends RDD[T](sc, Nil) {
  override def compute(split: Partition, context: TaskContext): Iterator[T] = {
    val splits = split.asInstanceOf[MyPartition[T]]
    splits.value.iterator
  }

  override protected def getPartitions: Array[Partition] = {
    val countDownLatch = new CountDownLatch(10)
    val partitionedData = new Array[ArrayBuffer[(Int, String)]](10)
    for (i <- partitionedData.indices) {
      partitionedData(i) = ArrayBuffer.empty[(Int, String)]
    }
    files.foreach { file =>
      val reader = AsynchronousFileChannel.open(file.toPath, StandardOpenOption.READ)
      val buffer = ByteBuffer.allocate(file.length.toInt)
      val completionHandler = new CompletionHandler[Integer, Array[ArrayBuffer[(Int, String)]]] {
        override def failed(exc: Throwable, attachment: Array[ArrayBuffer[(Int, String)]]): Unit = exc.printStackTrace()

        override def completed(result: Integer, attachment: Array[ArrayBuffer[(Int, String)]]): Unit = {
          if (result > 0) {
            buffer.flip()
            val lines = new String(buffer.array()).split("\n")
            lines.foreach { line =>
              val elements = line.split(" ", 2)
              try {
                val key = elements.head.toInt
                partitionedData(key / 1000000) += ((key, elements.last))
              } catch {
                case e: IOException => e.printStackTrace()
                case _: NumberFormatException => log.warn(elements.head + "--------format")
                case _: ArrayIndexOutOfBoundsException => log.warn(elements.head + "===== out")
              }
            }
            buffer.clear()
          } else reader.close()
          countDownLatch.countDown()
        }
      }
      reader.read(buffer, 0L, partitionedData, completionHandler)
    }

    /*files.foreach { file =>
      val reader = new BufferedReader(new FileReader(file))
      val line = reader.lines().iterator()
      while (line.hasNext) {
        val elements = line.next().split(" ", 2)
        try {
          val key = elements.head.toInt
          partitionedData(key / 1000000) += ((key, elements.last))
        } catch {
          case e: IOException => e.printStackTrace()
          case _: NumberFormatException => log.warn(elements.head + "--------format")
          case _: ArrayIndexOutOfBoundsException => log.warn(elements.head.toInt / 1000000 + "===== out")
        }
      }
    }*/
    countDownLatch.await()
    partitionedData.filter(_.nonEmpty).zipWithIndex.map(p => new MyPartition(p._2, p._1))
  }

}
