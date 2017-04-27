import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client.{ConnectionFactory, Put, Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding
import org.apache.hadoop.hbase.mapreduce.{IdentityTableMapper, TableInputFormat, TableMapReduceUtil}
import org.apache.hadoop.hbase.regionserver.BloomType
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.deploy.SparkHadoopUtil
import org.apache.spark.rdd.NewHadoopRDD

import scala.util.Random

/**
  * Created by wpy on 17-4-27.
  */
object TestHBase {

  val tableName = "wpy:test"

  val conf = HBaseConfiguration.create()
  val conn = ConnectionFactory.createConnection(conf)

  def createTable(tn: String): Unit = {
    val ns = tn.split(":")(0)
    val admin = conn.getAdmin
    //    admin.createNamespace(NamespaceDescriptor.create(ns).build())
    val td = new HTableDescriptor(TableName.valueOf(tn))
    val cf = new HColumnDescriptor(Bytes.toBytes("cf"))
    cf.setBlockCacheEnabled(true)
    cf.setBloomFilterType(BloomType.ROW)
    cf.setDataBlockEncoding(DataBlockEncoding.PREFIX_TREE)
    td.addFamily(cf)
    admin.createTable(td)
    admin.close()
  }

  def distributeScan(): Unit = {
    val sparkConf = new SparkConf().setAppName("HBaseDistributedScanExample " + tableName).setMaster("local[*]")
    sparkConf.registerKryoClasses(Array(classOf[ImmutableBytesWritable]))
    val sc = new SparkContext(sparkConf)
    val job: Job = Job.getInstance(conf)

    val scan = new Scan()
    scan.setCaching(100)
    TableMapReduceUtil.initTableMapperJob(TableName.valueOf(tableName), scan,
      classOf[IdentityTableMapper], null, null, job)
    val jConf = new JobConf(job.getConfiguration)
    SparkHadoopUtil.get.addCredentials(jConf)
    val rdd = new NewHadoopRDD[ImmutableBytesWritable, Result](sc,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result], job.getConfiguration).map((r: (ImmutableBytesWritable, Result)) => r)
    rdd.foreach(v => println(Bytes.toString(v._1.get())))
    println("Length: " + rdd.map(r => r._1.copyBytes()).collect().length)
  }

  def put(): Unit = {
    val table = conn.getTable(TableName.valueOf(tableName))
    for (i <- 1 to 100) {
      val put = new Put(Bytes.toBytes(i.formatted("%03d").toString))
      put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("1"), Bytes.toBytes(i.toString))
      put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("2"), Bytes.toBytes(Random.nextInt(1000).formatted("%04d").toString))
      table.put(put)
    }
    table.close()
  }

  def main(args: Array[String]): Unit = {
    //    createTable(tableName)
    //    put()
    distributeScan()
    //    conn.close()
  }


}
