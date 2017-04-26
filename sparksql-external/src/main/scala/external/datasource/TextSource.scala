package external.datasource

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.sources.{BaseRelation, SchemaRelationProvider}
import org.apache.spark.sql.types.StructType

/**
  * Created by wpy on 2017/4/26.
  */
class TextSource extends SchemaRelationProvider {
  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String], schema: StructType): BaseRelation = {

    val path = parameters.getOrElse("path", "/home/wpy/tmp/external_sql/testSql")

    TextRelation(sqlContext, schema, path)
  }
}
