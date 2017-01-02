import java.io.{BufferedWriter, File, FileOutputStream, FileWriter}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.KeyValue
import org.apache.hadoop.hbase.regionserver.StoreFile
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.SparkSession
import org.apache.spark.{Partitioner, SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
  * Created by w5921 on 2017/1/2.
  */
object Rank {

  val splitKey: Array[Int] = Array[Int](10000, 20000, 30000, 40000, 50000)
  val random = new Random()
  //  def main(args: Array[String]): Unit = {
  //    val writer = new BufferedWriter(new FileWriter("D:\\temp\\text.txt"))
  //    for (_ <- 1 to 500000) {
  //      writer.append(random.nextInt(50000).formatted("%05d") + "\n")
  //    }
  //    writer.flush()
  //    writer.close()
  //  }

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[20]").setAppName("rank")
    val sc = new SparkContext(conf)
    sc.textFile("D:/temp/text.txt").map((_, 1)).partitionBy(new MergePartitioner(splitKey)).sortByKey().foreachPartition { part =>
      val writer = new BufferedWriter(new FileWriter(new File("D:/temp/" + System.currentTimeMillis())))
      part.foreach(item => {
        writer.append(item._1 + " " + item._2 + " " + item._1 + System.nanoTime() + "\n")
      })
      writer.close()
    }
    //    print(new MergePartitioner(splitKey).getPartition(49999))
  }

}

class MergePartitioner(splitKey: Array[Int]) extends Partitioner {
  override def numPartitions: Int = splitKey.length

  override def getPartition(key: Any): Int = {
    val k = key.toString.toInt
    var part = -1
    for (i <- splitKey.indices) {
      if (k < splitKey(i)) {
        part = i
        return part
      }
    }
    part
  }

}