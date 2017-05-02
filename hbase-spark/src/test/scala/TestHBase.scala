import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client.{ConnectionFactory, Put, Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding
import org.apache.hadoop.hbase.mapreduce.{IdentityTableMapper, TableInputFormat, TableMapReduceUtil}
import org.apache.hadoop.hbase.regionserver.BloomType
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.deploy.SparkHadoopUtil
import org.apache.spark.rdd.NewHadoopRDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.{HBaseContext, SparkConf, SparkContext}

import scala.util.Random

/**
  * Created by wpy on 17-4-27.
  */
object TestHBase {


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
    val tableName = "wpy:test"
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
    val tableName = "wpy:test"
    val table = conn.getTable(TableName.valueOf(tableName))
    for (i <- 1 to 100) {
      val put = new Put(Bytes.toBytes(i.formatted("%03d").toString))
      put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("1"), Bytes.toBytes(i.toString))
      put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("2"), Bytes.toBytes(Random.nextInt(1000).formatted("%04d").toString))
      table.put(put)
    }
    table.close()
  }

  def sql(): Unit = {
    val tn = "wpy:test"
    val sparkConf = new SparkConf().setAppName("HBaseDistributedScanExample " + tn).setMaster("local[*]")
    sparkConf.registerKryoClasses(Array(classOf[ImmutableBytesWritable]))
    val sc = new SparkContext(sparkConf)
    val hc = new HBaseContext(sc, conf)
    val ss = SparkSession.builder().config(sparkConf).getOrCreate()
    val catalog =
      s"""{
         |"table":{"namespace":"default", "name":"htable"},
         |"rowkey":"key1:key2",
         |"columns":{
         |"col1":{"cf":"rowkey", "col":"key1", "type":"string"},
         |"col2":{"cf":"rowkey", "col":"key2", "type":"double"},
         |"col3":{"cf":"cf1", "col":"col2", "type":"binary"},
         |"col4":{"cf":"cf1", "col":"col3", "type":"timestamp"}
         |}
         |}""".stripMargin
    ss.sql(
      s"""
         |CREATE TEMPORARY TABLE test
         |USING org.apache.spark.DefaultSource
      """.stripMargin)

    ss.sql(
      """
        |INSERT INTO test(
        |id,name,age
        |) VALUES(01,wang_py,54)
        |USING org.apache.spark.DefaultSource
      """.stripMargin)

    ss.sql(
      """
        |SELECT * FROM test
      """.stripMargin).show()

    ss.close()
  }

  def main(args: Array[String]): Unit = {
    //    createTable(tableName)
    //    put()
    //    distributeScan()
    sql()
    //    conn.close()
  }


}
