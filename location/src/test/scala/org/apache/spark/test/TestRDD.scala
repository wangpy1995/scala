package org.apache.spark.test

import java.io.{BufferedReader, File, FileReader}

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
      val data = part.asInstanceOf[Iterator[(Int, String)]].toSeq
      Sorting.stableSort(data, (a: (Int, String), b: (Int, String)) => a._1 < b._1).iterator
    }.saveAsTextFile(out)
    while (true)
      Thread.sleep(1000)
  }
}

class MyPartition[T](idx: Int, val value: ArrayBuffer[T]) extends Partition {
  override def index: Int = idx
}

class MyRDD[T: ClassTag](@transient sc: SparkContext, files: Array[File]) extends RDD(sc, Nil) {
  override def compute(split: Partition, context: TaskContext): Iterator[T] = {
    val splits = split.asInstanceOf[MyPartition[T]]
    splits.value.iterator
  }

  override protected def getPartitions: Array[Partition] = {
//    SamplingUtils.reservoirSampleAndCount()
    val parts = new Array[Partition](10)
    val partitionedData = new Array[ArrayBuffer[(Int, String)]](10)
    for (i <- partitionedData.indices) {
      partitionedData(i) = ArrayBuffer.empty[(Int, String)]
    }
    files.foreach { file =>
      val reader = new BufferedReader(new FileReader(file))
      var line = reader.readLine()
      while (line != null) {
        val elements = line.split(" ", 2)
        val key = elements.head.toInt
        partitionedData(key / 10) += ((key, elements(1)))
        line = reader.readLine()
      }
      reader.close()
    }
    for (i <- partitionedData.indices) {
      parts(i) = new MyPartition[(Int, String)](i, partitionedData(i))
    }
    parts
  }

}