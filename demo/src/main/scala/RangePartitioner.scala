import org.apache.spark.rdd.RDD
import org.apache.spark.util.random.BernoulliSampler

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag
import scala.util.Random
import scala.util.hashing.byteswap32

/**
  * Created by w5921 on 2016/12/28.
  */

trait Partitioner {
  protected val sampleSize: Double

  /**
    * Sketches the input Data via reservoir sampling on each partition.
    *
    * @param seq                    the input to sketch
    * @param sampleSizePerPartition max sample size per partition
    * @tparam K
    * @return (total number of items, an array of (partitionId, number of items, sample))
    */
  def sketch[K: ClassTag](
                           seq: Seq[K],
                           sampleSizePerPartition: Int): (Long, Array[(Int, Array[K])])
}

class RangePartitioner(partitions: Double) extends Partitioner {
  override protected val sampleSize: Double = math.min(20.0 * partitions, 1e6)
  val sampleSizePerPartition: Int = math.ceil(3.0 * sampleSize / partitions).toInt

  override def sketch[K: ClassTag](seq: Seq[K], sampleSizePerPartition: Int): (Long, Array[(Int, Array[K])]) = {
    // val classTagK = classTag[K] // to avoid serializing the entire partitioner object
    val (sample, n) = reservoirSampleAndCount(seq.iterator, sampleSizePerPartition)
    val sketched = Iterator((n, sample)).toArray
    val numItems = sketched.map(_._1.toLong).sum

    //    val fraction = math.min(sampleSize / math.max(numItems, 1L), 1.0)
    //    val candidates = ArrayBuffer.empty[(K, Float)]
    //    val imbalancedPartitions = mutable.Set.empty[Array[K]]
    //
    //    sketched.foreach { case (n, sample) =>
    //      /* I: 应该采样的数据比实际采样的数据要大 */
    //      if (fraction * n > sampleSizePerPartition) {
    //        imbalancedPartitions += sample
    //      } else {
    //        // The weight is 1 over the sampling probability.
    //        val weight = (n.toDouble / sample.size).toFloat
    //        for (key <- sample) {
    //          candidates += ((key, weight))
    //        }
    //      }
    //    }
    //    if (imbalancedPartitions.nonEmpty) {
    //      // Re-sample imbalanced partitions with the desired sampling probability.
    //      val reSampled = new BernoulliSampler(fraction)
    //      val weight = (1.0 / fraction).toFloat
    //      candidates ++= (reSampled, weight)
    //    }
    (numItems, sketched)
  }

  /**
    * Reservoir sampling implementation that also returns the input size.
    *
    * @param input input size
    * @param k     reservoir size
    * @param seed  random seed
    * @tparam T
    * @return (samples, input size)
    */
  def reservoirSampleAndCount[T: ClassTag](input: Iterator[T], k: Int,
                                           seed: Long = Random.nextLong()): (Array[T], Int) = {
    val reservoir = new Array[T](k)
    // Put the first k elements in the reservoir.
    var i = 0
    while (i < k && input.hasNext) {
      val item = input.next()
      reservoir(i) = item
      i += 1
    }

    // If we have consumed all the elements, return them. Otherwise do the replacement.
    if (i < k) {
      // If input size < k, trim the array to return only an array of input size.
      val trimReservoir = new Array[T](i)
      System.arraycopy(reservoir, 0, trimReservoir, 0, i)
      (trimReservoir, i)
    } else {
      // If input size > k, continue the sampling process.
      val rand = new Random(seed)
      while (input.hasNext) {
        val item = input.next()
        val replacementIndex = rand.nextInt(i)
        if (replacementIndex < k) {
          reservoir(replacementIndex) = item
        }
        i += 1
      }
      (reservoir, i)
    }
  }

  def determineBounds[K: Ordering : ClassTag](
                                               candidates: ArrayBuffer[(K, Float)],
                                               partitions: Int): Array[K] = {
    val ordering = implicitly[Ordering[K]]
    val ordered = candidates.sortBy(_._1)
    val numCandidates = ordered.size
    val sumWeights = ordered.map(_._2.toDouble).sum
    val step = sumWeights / partitions
    var cumWeight = 0.0
    var target = step
    val bounds = ArrayBuffer.empty[K]
    var i = 0
    var j = 0
    var previousBound = Option.empty[K]
    while ((i < numCandidates) && (j < partitions - 1)) {
      val (key, weight) = ordered(i)
      cumWeight += weight
      if (cumWeight >= target) {
        // Skip duplicate values.
        if (previousBound.isEmpty || ordering.gt(key, previousBound.get)) {
          bounds += key
          target += step
          j += 1
          previousBound = Some(key)
        }
      }
      i += 1
    }
    bounds.toArray
  }

}

