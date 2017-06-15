package org.apache.spark.test

import java.io.File

import hadoop.mapreduce.input.MyKeyValueRecordReader
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapred.{FileInputFormat, JobConf}
import org.apache.hadoop.mapreduce._
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.NewHadoopRDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.Sorting

/**
  * Created by wpy on 2017/6/14.
  */

object Test {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("test")
    val sc = SparkContext.getOrCreate(sparkConf)
    val files = new File("/home/wpy/tmp/test/").listFiles()
    val conf = new JobConf()
    FileInputFormat.setInputPaths(conf, files.map(_.getPath).mkString(","))
    val job = Job.getInstance(conf)
    FileMapperUtil.initFileMapJob(files, classOf[IdentityFileMapper], classOf[LongWritable], classOf[Text], job, classOf[MyFileInputFormat])
    new NewHadoopRDD(sc, classOf[MyFileInputFormat], classOf[LongWritable], classOf[Text], conf).mapPartitions { kv =>
      val data = kv.toSeq
      Sorting.stableSort(data, (a: (LongWritable, Text), b: (LongWritable, Text)) => a._1.get() < b._1.get()).iterator
    }.saveAsTextFile("/home/wpy/tmp/hadoop_out")
    while (true) Thread.sleep(1000)
  }
}

object FileMapperUtil {
  def initFileMapJob[K, V](file: Array[File],
                           mapper: Class[_ <: FileMapper[K, V]],
                           outputKeyClass: Class[K],
                           outputValueClass: Class[V],
                           job: Job,
                           inputFormatClass: Class[_ <: InputFormat[K, V]]): Unit = {
    job.setInputFormatClass(inputFormatClass)
    if (outputValueClass != null) job.setMapOutputValueClass(outputValueClass)
    if (outputKeyClass != null) job.setMapOutputKeyClass(outputKeyClass)
    job.setMapperClass(mapper)
    job.setPartitionerClass(classOf[MyPartitioner])
  }
}

abstract class FileMapper[KEYOUT, VALUEOUT] extends Mapper[LongWritable, Text, KEYOUT, VALUEOUT]

class IdentityFileMapper extends FileMapper[LongWritable, Text] with Logging {
  override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, LongWritable, Text]#Context): Unit = {
    context.write(key, value)
  }
}

class MyFileInputFormat extends TextInputFormat {
  override def createRecordReader(split: InputSplit, context: TaskAttemptContext): RecordReader[LongWritable, Text] =
    new MyKeyValueRecordReader(context.getConfiguration)
}

class MyPartitioner extends Partitioner[LongWritable, Text] {
  override def getPartition(key: LongWritable, value: Text, numPartitions: Int): Int = {
    key.get().toInt / 1000000
  }
}