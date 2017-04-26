package org.apache.spark.sql.external

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.{Attribute, UnsafeProjection}
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.execution.SparkPlan
import org.apache.spark.sql.execution.command.{ExecutedCommandExec, RunnableCommand}
import org.apache.spark.unsafe.types.UTF8String

/**
  * Created by wpy on 2017/4/26.
  */
case class LogicalText(output: Seq[Attribute], path: String) extends LogicalPlan {
  override def children: Seq[LogicalPlan] = Nil
}

case class PhysicalText(output: Seq[Attribute], path: String) extends SparkPlan {
  override protected def doExecute(): RDD[InternalRow] = {
    sparkContext.textFile(path).map { row =>
      val fields = row.split(",").map(UTF8String.fromString)

      /* val ur = new UnsafeRow(fields.length)
       fields.foreach(f => ur.pointTo(f, f.length))
       ur*/
//      GenerateUnsafeProjection.generate(expressions)(InternalRow.fromSeq(fields))
      UnsafeProjection.create(schema)(InternalRow.fromSeq(fields))
    }
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