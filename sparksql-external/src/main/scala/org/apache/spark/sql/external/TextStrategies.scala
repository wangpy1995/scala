package org.apache.spark.sql.external

import org.apache.commons.dbutils.QueryRunner
import org.apache.spark.sql.Strategy
import org.apache.spark.sql.catalyst.planning.{GenericStrategy, QueryPlanner}
import org.apache.spark.sql.catalyst.plans.logical.{InsertIntoTable, LogicalPlan}
import org.apache.spark.sql.execution.{SparkPlan, SparkPlanner, SparkStrategies}
import org.apache.spark.sql.execution.datasources.LogicalRelation

/**
  * Created by wpy on 2017/4/26.
  */
class TextStrategies extends SparkStrategies with SparkPlanner {
  override def strategies: Seq[GenericStrategy[SparkPlan]] = TextStrategy :: Nil

  object TextStrategy extends Strategy {
    override def apply(plan: LogicalPlan): Seq[SparkPlan] = {
      plan match {
        case LogicalText(output, path) => PhysicalText(output, path) :: Nil
        case LogicalRelation(relation: TextRelation, _, _) => PhysicalText(relation)
        case TextExecuteCommand(cmd: InsertIntoTable) => Nil
        case _ => Nil
      }
    }
  }

}
