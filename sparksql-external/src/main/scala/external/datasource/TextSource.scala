package external.datasource

import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}
import org.apache.spark.sql.sources.{BaseRelation, CreatableRelationProvider, SchemaRelationProvider}
import org.apache.spark.sql.types.StructType

/**
  * Created by wpy on 2017/4/26.
  */
class TextSource extends SchemaRelationProvider with CreatableRelationProvider {
  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String], schema: StructType): BaseRelation = {

    val path = parameters.getOrElse("path", "/home/wpy/tmp/external_sql/testSql")
    Schemas.schema = schema
    TextRelation(sqlContext, schema, path)
  }

  override def createRelation(sqlContext: SQLContext, mode: SaveMode, parameters: Map[String, String], data: DataFrame): BaseRelation = {
    val tableName = parameters.getOrElse("table_name", "test")
    val path = parameters.getOrElse("path", "/home/wpy/tmp/external_sql/testSql")
    sqlContext.sparkSession.catalog.createTable(tableName, path)
    TextRelation(sqlContext, Schemas.schema, path)
  }
}

object Schemas {
  var schema: StructType = _
}