package org.apache.spark.sql.external

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.sources.BaseRelation
import org.apache.spark.sql.types.StructType

/**
  * Created by wpy on 2017/4/26.
  */
case class TextRelation(sqlContext: SQLContext, schema: StructType) extends BaseRelation {

}
