package sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Created by wpy on 2017/4/26.
  */
object TestTextSql {
  val conf = new SparkConf().setMaster("local[*]").setAppName(getClass.getCanonicalName)
  val ss = SparkSession.builder().config(conf).getOrCreate()

  def main(args: Array[String]): Unit = {
    val sqlContext = ss.sqlContext
    val sparkContext = ss.sparkContext
  }
}
