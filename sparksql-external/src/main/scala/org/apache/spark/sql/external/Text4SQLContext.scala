package org.apache.spark.sql.external

import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, SQLContext}

/**
  * Created by wpy on 2017/4/26.
  */
class Text4SQLContext(sc: SparkContext, sqlContext: SQLContext){

  sqlContext.experimental.extraStrategies = new TextStrategies().TextStrategy :: Nil

  def sql(sqlText: String): DataFrame = {
    sqlContext.sql(sqlText)
  }
}