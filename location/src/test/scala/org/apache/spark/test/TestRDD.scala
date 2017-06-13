package org.apache.spark.test

import java.io.{File, IOException}
import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousFileChannel, CompletionHandler}

import org.apache.spark.rdd.RDD
import org.apache.spark.{Partition, SparkConf, SparkContext, TaskContext}

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag
import scala.util.Sorting

/**
  * Created by wpy on 17-6-8.
  */
object TestRDD {
  val sparkConf = new SparkConf().setAppName("test_RDD").setMaster("local[*]")
  val sc = SparkContext.getOrCreate(sparkConf)
  val path = "/home/wpy/tmp/test/"
  val out = "/home/wpy/tmp/out"

  def main(args: Array[String]): Unit = {
    val files = new File(path).listFiles()
    new MyRDD[(Int, String)](sc, files).mapPartitions { part =>
      val data = part.toSeq
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
    //    SamplingUtils.reservoirSampleAndCount()
    val partitionedData = new Array[ArrayBuffer[(Int, String)]](10)
    for (i <- partitionedData.indices) {
      partitionedData(i) = ArrayBuffer.empty[(Int, String)]
    }
    //非阻塞IO(回调方式)
    files.foreach { file =>
      //TODO 是否需要线程池
      val reader = AsynchronousFileChannel.open(file.toPath)
      //      此处若使用堆外内存会导致数据无法直接获取
      val dst = ByteBuffer.allocate(file.length().toInt)
      val completion = new CompletionHandler[Integer, Array[ArrayBuffer[(Int, String)]]] {
        override def failed(exc: Throwable, attachment: Array[ArrayBuffer[(Int, String)]]): Unit = {
          exc.printStackTrace()
        }

        override def completed(result: Integer, attachment: Array[ArrayBuffer[(Int, String)]]): Unit = {
          if (result > 0) {
            dst.flip()
            try {
              val lines = new String(dst.array()).split("\n")
              lines.foreach { line =>
                val elements = line.split(" ", 2)
                val key = elements.head.toInt
                attachment(key / 10) += ((key, elements(1)))
              }
            } catch {
              case e: IOException => e.printStackTrace()
            }
            dst.clear()
          } else reader.close()
        }
      }
      reader.read(dst, 0L, partitionedData, completion)
      //TODO do other things if needed
    }

    /* files.foreach { file =>
       val reader = new BufferedReader(new FileReader(file))
       var line = reader.readLine()
       while (line != null) {
         val elements = line.split(" ", 2)
         val key = elements.head.toInt
         partitionedData(key / 10) += ((key, elements(1)))
         line = reader.readLine()
       }
       reader.close()
     }*/
    partitionedData.filter(_.nonEmpty).zipWithIndex
      .map(p => new MyPartition[(Int, String)](p._2, p._1))
  }

}