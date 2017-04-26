package org.apache.spark.sql.external

import external.datasource.TextRelation
import org.apache.spark.sql.Strategy
import org.apache.spark.sql.catalyst.expressions.PredicateHelper
import org.apache.spark.sql.catalyst.planning.{GenericStrategy, QueryPlanner}
import org.apache.spark.sql.catalyst.plans.logical.{InsertIntoTable, LogicalPlan}
import org.apache.spark.sql.execution.SparkPlan
import org.apache.spark.sql.execution.command.ExecutedCommandExec
import org.apache.spark.sql.execution.datasources.{InsertIntoDataSourceCommand, LogicalRelation}

/**
  * Created by wpy on 2017/4/26.
  */
class TextStrategies extends QueryPlanner[SparkPlan] with PredicateHelper {
  override def strategies: Seq[GenericStrategy[SparkPlan]] = TextStrategy :: Nil

  object TextStrategy extends Strategy {
    override def apply(plan: LogicalPlan): Seq[SparkPlan] = {
      plan match {
        case LogicalText(output, path) => PhysicalText(output, path) :: Nil
        case LogicalRelation(TextRelation(_, _, path), output, _) => PhysicalText(output, path) :: Nil
        case i@InsertIntoTable(l@LogicalRelation(t: TextRelation, _, _), part, query, overwrite, false) if part.isEmpty =>
          ExecutedCommandExec(InsertIntoDataSourceCommand(l, query, overwrite)) :: Nil
        case _ => Nil
      }
    }
  }

  override protected def collectPlaceholders(plan: SparkPlan): Seq[(SparkPlan, LogicalPlan)] = {
    /*  plan.collect {
        case placeholder@PlanLater(logicalPlan) => placeholder -> logicalPlan
      }*/
    Nil
  }

  override protected def prunePlans(plans: Iterator[SparkPlan]): Iterator[SparkPlan] = {
    plans
  }
}
