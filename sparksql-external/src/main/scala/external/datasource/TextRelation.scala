package external.datasource

import java.io.File

import org.apache.spark.sql.sources.{BaseRelation, InsertableRelation}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SQLContext}

import scala.util.Random

/**
  * Created by wpy on 2017/4/26.
  */
case class TextRelation(sqlContext: SQLContext, schema: StructType, path: String) extends BaseRelation with InsertableRelation {
  def subPath = path + s"/${Random.nextInt(math.abs(this.hashCode()))}"

  override def insert(data: DataFrame, overwrite: Boolean): Unit = {
    val sub = subPath
    if (!new File(sub).exists())
      data.rdd.map(_.mkString(",")).saveAsTextFile(sub)
    else {
      insert(data, overwrite)
    }
  }
}
