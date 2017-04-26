package org.apache.spark.sql.external

import org.apache.spark.SparkContext
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SQLContext

/**
  * Created by wpy on 2017/4/26.
  */
class Text4SQLContext(sc: SparkContext, sqlContext: SQLContext) extends SQLContext(sc) with Logging {
}