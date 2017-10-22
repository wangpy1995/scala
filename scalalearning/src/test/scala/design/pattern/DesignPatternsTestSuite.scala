package design.pattern

import design.pattern.observer._
import design.pattern.strategy.{FlyBehaviour, MallardDuck, QuackBehaviour}
import org.scalatest.FunSuite

class DesignPatternsTestSuite extends FunSuite {

  test("strategy_duck") {
    val mallardDuck = new MallardDuck()
    mallardDuck.setFlyBehaviour(new FlyBehaviour {
      override def fly(name: String): Unit = println(name + " fly")
    })
    mallardDuck.setQuackBehaviour(new QuackBehaviour {
      override def quack(name: String): Unit = println(name + " quack-quack")
    })
    mallardDuck.performFly()
    mallardDuck.performQuack()
    mallardDuck.setFlyBehaviour(new FlyBehaviour {
      override def fly(name: String): Unit = println(name + " fly with Rocket")
    })
    mallardDuck.performFly()
    mallardDuck.display()
    mallardDuck.swim()
  }

  test("observer_weather") {
    val weatherData = new WeatherData
    val current = new CurrentConditionDisplay
    val statistics = new StatisticsDisplay
    val thirdparty = new ThirdPartyDisplay
    val forecast = new ForecastDisplay
    weatherData.registObserver(current)
    weatherData.registObserver(thirdparty)

    weatherData.setMeasurements(12.3f, 15.8f, 114.5f)
    weatherData.measurementsChanged()

    weatherData.registObserver(statistics)
    weatherData.setMeasurements(24f, 29.7f, 237.4f)
    weatherData.measurementsChanged()

    weatherData.registObserver(forecast)
    weatherData.setMeasurements(34.2f, 32.5f, 356.4f)
    weatherData.measurementsChanged()

    weatherData.removeObserver(current)
    weatherData.setMeasurements(40.7f, 42.5f, 446.4f)
    weatherData.measurementsChanged()
  }
}