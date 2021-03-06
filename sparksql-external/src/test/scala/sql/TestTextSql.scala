package sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.external.Text4SQLContext

/**
  * Created by wpy on 2017/4/26.
  */
object TestTextSql {
  val conf = new SparkConf().setMaster("local[*]").setAppName(getClass.getCanonicalName)
  val ss = SparkSession.builder().config(conf).getOrCreate()

  def main(args: Array[String]): Unit = {
    val sqlContext = ss.sqlContext
    val sparkContext = ss.sparkContext
    val ts = new Text4SQLContext(sparkContext, sqlContext)

    ts.sql(
      """create table test1(
        |word string,
        |num string
        |) using external.datasource.TextSource
        |options(
        |path '/home/wpy/tmp/external_sql/test1'
        |)
      """.stripMargin)
    ts.sql("select * from test1").show

    print("=============================================\n")
    /*    ts.sql(
          """create table test2(
            |word string,
            |num int
            |) using external.datasource.TextSource
            |options(
            |path '/home/wpy/tmp/external_sql/test2'
            |)
          """.stripMargin)*/
    ts.sql(
      """
        |CREATE TABLE test2
        |USING external.datasource.TextSource
        | OPTIONS(
        |path '/home/wpy/tmp/external_sql/test2'
        |)
        |as select * from test1
      """.stripMargin)
    ts.sql("select * from wpy.test2 order by word").show
    //    val rdd = sparkContext.wholeTextFiles("/home/wpy/tmp/external_sql/test2/[0-9]*").collect()
    //    rdd.foreach(println)
  }
}
