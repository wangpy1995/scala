package external.datasource

import java.io.File

import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.sql.sources.{BaseRelation, InsertableRelation}
import org.apache.spark.sql.types.StructType

/**
  * Created by wpy on 2017/4/26.
  */
case class TextRelation(sqlContext: SQLContext, schema: StructType, path: String) extends BaseRelation with InsertableRelation {
  override def insert(data: DataFrame, overwrite: Boolean): Unit = {
    if (!new File(path).exists())
      data.rdd.map(_.mkString(",")).saveAsTextFile(path)
  }
}
