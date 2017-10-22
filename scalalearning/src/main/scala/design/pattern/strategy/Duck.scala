package design.pattern.strategy

import java.util.Locale

/**
  * 抽象类
  */
abstract class Duck {
  val name = getClass.getSimpleName.toLowerCase(Locale.ROOT)

  def swim() = println(name + " swim")

  def display() = println(name)
}

/**
  * 行为
  */
trait FlyBehaviour {
  def fly(name: String)
}

/**
  * 行为
  */
trait QuackBehaviour {
  def quack(name: String)
}

class MallardDuck(private var flyBehaviour: FlyBehaviour, private var quackBehaviour: QuackBehaviour) extends Duck {

  def this() = this(null, null)

  def setFlyBehaviour(flyBehaviour: FlyBehaviour) = this.flyBehaviour = flyBehaviour

  def setQuackBehaviour(quackBehaviour: QuackBehaviour) = this.quackBehaviour = quackBehaviour

  def performFly() = if (flyBehaviour != null) flyBehaviour.fly(name)

  def performQuack() = if (quackBehaviour != null) quackBehaviour.quack(name)
}

class RedheadDuck extends Duck {

}