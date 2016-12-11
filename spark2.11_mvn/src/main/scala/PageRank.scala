import org.apache.spark.{HashPartitioner, SparkConf}
import org.apache.spark.sql.SparkSession

/**
  * (pageID,linkList)
  * (pageID,rank)
  * Created by wpy on 16-12-12.
  */
object PageRank {
  private val session = SparkSession.builder().config(new SparkConf().setMaster("local[4]")).getOrCreate()
  private val sc = session.sparkContext

  def pageRank(): Unit = {
    //collect data
    val links = sc.textFile("/home/wpy/文档/IdeaProjects/scala/spark2.11_mvn/src/main/resources/link.data")
      .map(x => (x.split(" ")(0), x.split(" ").drop(1).toSeq)).partitionBy(new HashPartitioner(1)).persist()
    var ranks = links.mapValues(v => 1.0)
    //join
    //    for (i <- 1 to 10) {
    //eq: (1000,(WrappedArray(www.baidu.com, www.sina.com, www.163.com),1.0))
    val contribution = links.join(other = ranks).flatMap {
      case (pageId, (link, rank)) => link.map(dest => (dest, rank / link.size))
    }
    //eq: (www.sina.com,0.3333333333333333)
    ranks = contribution.reduceByKey(_ + _).mapValues(v => 0.15 + 0.85 * v)
    //    }
    ranks.saveAsTextFile("/home/wpy/文档/IdeaProjects/scala/spark2.11_mvn/src/main/resources/rank")
    //    ranks.foreach(println)
  }

  def main(args: Array[String]): Unit = {
    pageRank()
  }
}
