package bill.electricity

import scala.math.BigDecimal.RoundingMode

/**
  * Created by wpy on 2017/5/5.
  * 缴纳电费时表格打印
  */
class ElectricityBill {

  val lastMonth = Array[Double](775.0, 12345.0, 123.0, 234.0, 356.0)
  val thisMonth = Array[Double](815.0, 12456.0, 234.0, 345.0, 465.0)
  val names = Array[String]("公用", "上北", "上南", "下北", "下南")
  val money = 1299.0

  val charge = BigDecimal(money / (thisMonth.sum - lastMonth.sum)).setScale(3,RoundingMode.HALF_UP).doubleValue()

  val b = new Array[Bill](lastMonth.length)
  for (i <- lastMonth.indices) {
    val c = (thisMonth(i) - lastMonth(i)) * charge
    b(i) = Bill(names(i), thisMonth(i), lastMonth(i), thisMonth(i) - lastMonth(i), c)
  }


}


case class Bill(name: String, last_month: Double, this_month: Double, diff: Double, charge: Double)