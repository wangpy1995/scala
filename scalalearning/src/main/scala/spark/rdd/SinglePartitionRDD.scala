package spark.rdd

import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.util.CollectionAccumulator

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

/**
  * Created by wpy on 2017/5/2.
  */
class SinglePartitionRDD[T: ClassTag, +U](sc: SparkContext, rdd: RDD[T], accumulator: CollectionAccumulator[U]) extends RDD[T](sc, Nil) {

  val l = rdd.getNumPartitions.asInstanceOf[U]

  override def getDependencies: Seq[Dependency[_]] = {
    val deps = new ArrayBuffer[Dependency[_]]
    var pos = 0
    deps += new RangeDependency(rdd, 0, pos, rdd.partitions.length)
    pos += rdd.partitions.length
    deps
  }

  override def compute(s: Partition, context: TaskContext): Iterator[T] = {
    accumulator.add(l)
    val parts = s.asInstanceOf[TempPartition]
    //    part => parent[T](part.parentRddIndex).iterator(part.parentPartition, context)
    parts.parentPartition.flatMap(part => parent[T](parts.index).iterator(part, context)).iterator
  }

  override protected def getPartitions: Array[Partition] = {
    Array(new TempPartition(rdd.partitions))
  }

  class TempPartition(parts: Array[Partition]) extends Partition {
    var parentPartition = parts

    override def index: Int = 0
  }

}
