package org.apache.spark.sql.external

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.expressions.{Attribute, GenericInternalRow, UnsafeProjection}
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.catalyst.{CatalystTypeConverters, InternalRow}
import org.apache.spark.sql.execution.SparkPlan
import org.apache.spark.sql.execution.command.{ExecutedCommandExec, RunnableCommand}
import org.apache.spark.sql.types.DataType

/**
  * Created by wpy on 2017/4/26.
  */
case class LogicalText(output: Seq[Attribute], path: String) extends LogicalPlan {
  override def children: Seq[LogicalPlan] = Nil
}

case class PhysicalText(output: Seq[Attribute], path: String) extends SparkPlan {
  override protected def doExecute(): RDD[InternalRow] = {
    /* sparkContext.textFile(path).map { row =>
       UnsafeProjection.create(schema)(toRow(row, ","))
     }*/
    sparkContext.wholeTextFiles(path + "/[0-9]*").flatMap(_._2.split("\n")).map { msg =>
      UnsafeProjection.create(schema)(toRow(msg, ","))
    }
  }

  def toRow(msg: String, separator: String): InternalRow = {
    InternalRow.fromSeq(msg.split(separator).zipWithIndex.map { col =>
      CatalystTypeConverters.createToCatalystConverter(schema.fields(col._2).dataType)(col._1)
    })
  }

  def setCorrectedType(row: GenericInternalRow, kv: (String, Int), field: DataType): Unit = {
  }

  override def children: Seq[SparkPlan] = Nil
}

case class TextExecuteCommand(cmd: RunnableCommand) extends SparkPlan {
  override protected def doExecute(): RDD[InternalRow] = {
    ExecutedCommandExec(cmd).execute()
  }

  override def output: Seq[Attribute] = cmd.output

  override def children: Seq[SparkPlan] = Nil
}