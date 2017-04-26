package org.apache.spark.sql.external

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.Attribute
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.execution.command.RunnableCommand
import org.apache.spark.sql.execution.SparkPlan

/**
  * Created by wpy on 2017/4/26.
  */
case class LogicalText(output: Seq[Attribute], path: String) extends LogicalPlan {
  override def children: Seq[LogicalPlan] = Nil
}

case class PhysicalText(output: Seq[Attribute], path: String) extends SparkPlan {
  override protected def doExecute(): RDD[InternalRow] = {
    sparkContext.textFile(path).map(_.split(",").map(InternalRow(_)))
  }

  override def children: Seq[SparkPlan] = Nil
}

case class TextExecuteCommand(cmd:RunnableCommand) extends SparkPlan{
  override protected def doExecute(): RDD[InternalRow] = ???

  override def output: Seq[Attribute] = cmd.output

  override def children: Seq[SparkPlan] = Nil
}