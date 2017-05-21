package spark.bill

import bill.electricity.ElectricityBill
import org.apache.spark.sql.SparkSession
import spark.SparkConstants
import spark.rdd.SinglePartitionRDD

/**
  * Created by wpy on 2017/5/5.
  */
object TestElectricBill {

  val ss = SparkSession.builder().config(SparkConstants.sparkConf).getOrCreate()

  def main(args: Array[String]): Unit = {
    val sc = ss.sparkContext
    val bills = new ElectricityBill()
    import ss.implicits._
    //    val df = sc.parallelize(bills.b).toDF("名称", "当月", "上月", "度数", "价格")
    val df = new SinglePartitionRDD(sc, sc.parallelize(bills.b), null).toDF("房间", "本次度数", "上次度数", "总用电量", "应缴电费")
    df.createOrReplaceTempView("ELECTRIC_BILL")

    ss.sql(
      """
        |SELECT * FROM ELECTRIC_BILL
      """.stripMargin).show
  }

}
