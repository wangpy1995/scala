import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.util.TimeStampedValue

/**
  * Created by w5921 on 2016/11/18.
  */
object TestSparkWordCount {

  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder().appName("sparkWordCount").master("local").getOrCreate()
    ss.sparkContext.textFile("./demo/words.log").map((_, 1)).reduceByKey(_ + _).collect().foreach(println)
  }

  /*  def testHbase(): Unit = {
      val writeCatalog = " "
      val tsSpecified = TimeStampedValue
      val conf = new SparkConf().setAppName("testHbase").setMaster("local[")
      val sqlContext = SparkSession.builder().config(conf).getOrCreate().sqlContext
      val df = sqlContext.read
        .options(Map(HBaseTableCatalog.tableCatalog -> writeCatalog, HBaseSparkConf.TIMESTAMP -> tsSpecified.toString))
        .format("org.apache.hadoop.hbase.spark")
        .load()
      df.registerTempTable("table")
      sqlContext.sql("select count(*) from table").show
    }*/
}

object HBaseTableCatalog {
  val tableCatalog = ""
}

object HBaseSparkConf {
  val TIMESTAMP = ""
}