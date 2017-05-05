package spark

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by wpy on 2017/5/2.
  */
object SparkConstants {

  lazy val sparkConf = new SparkConf().setMaster("local[*]").setAppName("for_test") /*.registerKryoClasses(Array(classOf[Int]))*/

  def sc = SparkContext.getOrCreate(sparkConf)
}
