package scalaclass

/**
  * Created by Tex on 2016/11/10.
  */
object Counter extends App{
  private var value = Int.MaxValue
  def increment(){
    value+=1
  }
  def current() = {
    if(value<0)
      value=Int.MaxValue
    value
  }

  Counter.increment()
  println(Counter.current())
}