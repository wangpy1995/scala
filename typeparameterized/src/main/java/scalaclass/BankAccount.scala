package scalaclass

/**
  * Created by Tex on 2016/11/10.
  */
class BankAccount() {
  private[this] var balance: Int = 0
  val deposit = (money: Int) => balance += money
  val withdraw = (money: Int) => {
    if (balance >= money)
      balance -= money
    else
      println("your balance is not enough")
  }
}

object BankAccount {
  def main(args: Array[String]): Unit = {
    println(new BankAccount().deposit(100))
  }
}
