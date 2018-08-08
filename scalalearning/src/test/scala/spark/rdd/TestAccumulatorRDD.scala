package spark.rdd

import org.apache.spark._
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

import scala.annotation.meta.param
import scala.reflect.ClassTag

/**
  * Created by wpy on 2017/5/2.
  */
class TestAccumulatorRDD[T: ClassTag, +U](@(transient@param) sc: SparkContext, rdd: RDD[T], state: Broadcast[Int])
  extends RDD[T](sc, Nil) {
  override def compute(split: Partition, context: TaskContext): Iterator[T] = {
    val v = state.value
    println(v)
    parent(0).iterator(split, context)
  }

  override protected def getPartitions: Array[Partition] = rdd.partitions

  override protected def getDependencies: Seq[Dependency[_]] = {
    Seq(new OneToOneDependency[T](rdd))
  }
}
