import org.apache.spark.sql.SparkSession

/**
  * Created by w5921 on 2016/11/18.
  */
object TestSparkWordCount {

  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder().appName("sparkWordCount").master("local").getOrCreate()
    ss.sparkContext.textFile("e:/words.log").map((_, 1)).reduceByKey(_ + _).collect().foreach(println)
  }
}
