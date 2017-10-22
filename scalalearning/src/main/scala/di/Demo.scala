package di

trait Logger{
  def log(msg:String)
}

trait Auth{
  auth:Logger=>
  def act(msg:String) = auth.log(msg)
}

object DI extends Auth with Logger {
  override def log(msg: String): Unit = println(msg)
}

object DependencyInjection{
  def main(args: Array[String]): Unit = {
    DI.log("xxxx")
  }
}