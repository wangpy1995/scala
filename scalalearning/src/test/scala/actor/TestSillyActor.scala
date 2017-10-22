package actor

import org.apache.spark.rdd.RDD
import org.apache.spark.{Partition, SparkConf, SparkContext, TaskContext}

import scala.reflect.ClassTag
import scala.util.Random

/**
  * Created by wpy on 2017/5/6.
  */
object TestSillyActor {

  def main(args: Array[String]): Unit = {

    /*val sys = ActorSystem("SillyActorSystem")
    val sillyActor = sys.actorOf(Props[SillyActor], "silly_actor")
    sillyActor ! 1*/
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("test_rdd")
    val sc = SparkContext.getOrCreate(sparkConf)
    val d = new Array[Int](100)
    val data = d.map(_ => Random.nextInt(1000))
    val rdd1 = new MyRDD[Int, Int](sc, data, a => a.map(b => (b / 100, b)).filter(c => c._2 / 100 == 0 || c._2 / 100 == 9))
    val rdd2 = new MyRDD[Int, Int](sc, data, a => a.map(b => (b / 100, b)).filter(c => c._2 / 100 == 4 || c._2 / 100 == 9))
    //    val rdd3 = new MyRDD[Int](sc, data, a => a.filter(a => a / 100 == 4 || a / 100 == 0))

    rdd1.map(_._2).union(rdd2.map(_._2)).foreach(println)
    rdd1.join(rdd2).map(_._2).groupBy(_._1).foreach(println)

    while (true) Thread.sleep(1000)
  }

}


class MyRDD[T: ClassTag, U: ClassTag](sc: SparkContext, data: Seq[T], filter: Seq[T] => Seq[(T, U)]) extends RDD[(T, U)](sc, Nil) {
  override def compute(split: Partition, context: TaskContext): Iterator[(T, U)] = {
    split.asInstanceOf[MyPartition[T, U]].data.iterator
  }

  override protected def getPartitions: Array[Partition] = Array(MyPartition(0, filter(data)))
}

case class MyPartition[T, U](idx: Int, data: Seq[(T, U)]) extends Partition {
  override def index: Int = idx
}