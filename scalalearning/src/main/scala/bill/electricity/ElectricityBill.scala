package bill.electricity

import scala.math.BigDecimal.RoundingMode

/**
  * Created by wpy on 2017/5/5.
  * 缴纳电费时表格打印
  */
class ElectricityBill {

  val lastMonth = Array[Double](761.7, 15389.9, 17189.1, 13380.1, 23482.8)
  val thisMonth = Array[Double](774.0, 15656.0, 17526.0, 13850.0, 23739.0)
  val names = Array[String]("公用", "上北", "上南", "下北", "下南")
  val money = 710.30

  val charge = money / (thisMonth.sum - lastMonth.sum)

  val b = new Array[Bill](lastMonth.length)
  for (i <- lastMonth.indices) {
    val c = BigDecimal((thisMonth(i) - lastMonth(i)) * charge).setScale(2, RoundingMode.HALF_UP).doubleValue()
    b(i) = Bill(names(i), thisMonth(i), lastMonth(i), BigDecimal(thisMonth(i) - lastMonth(i)).setScale(2, RoundingMode.HALF_UP).doubleValue(), c)
  }


}


case class Bill(name: String, last_month: Double, this_month: Double, diff: Double, charge: Double)