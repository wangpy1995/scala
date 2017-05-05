package spark.broadcast

import spark.SparkConstants
import spark.rdd.TestAccumulatorRDD

/**
  * Created by wpy on 2017/5/2.
  */
object TestAccumulator {

  def main(args: Array[String]): Unit = {
    val sc = SparkConstants.sc
    val rdd = sc.parallelize(Array[String]("2", "3", "4", "5"))
    val accumulator = sc.collectionAccumulator[Any]("test")
    //    new SinglePartitionRDD(sc, rdd, accumulator).collect().foreach(println)
    val state = 0
    val v = sc.broadcast(state)
    new TestAccumulatorRDD(sc, rdd, v).collect().foreach(println)
    println(accumulator)
  }
}
