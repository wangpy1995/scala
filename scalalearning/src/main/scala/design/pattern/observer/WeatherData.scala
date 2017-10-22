package design.pattern.observer

import java.util.Locale

import scala.collection.mutable

/**
  * Subject
  */
class WeatherData extends Subject {
  val observerSet = new mutable.HashSet[Observer]()

  var pressure: Float = _
  var humidity: Float = _
  var temperature: Float = _

  private var state: Boolean = false

  def setMeasurements(temperature: Float, humidity: Float, pressure: Float) = {
    this.pressure = pressure
    this.humidity = humidity
    this.temperature = temperature
    state = true
  }

  def measurementsChanged() = {
    if (state) {
      notifyObservers(this, None)
      state = false
      println()
    }
  }

  override def registObserver(observer: Observer): Unit = observerSet.add(observer)

  override def notifyObservers(subject: this.type, obj: Any): Unit = observerSet.foreach(_.update(subject, obj))

  override def removeObserver(observer: Observer): Unit = observerSet -= observer
}

trait Subject {
  def registObserver(observer: Observer)

  def notifyObservers(subject: this.type, obj: Any)

  def removeObserver(observer: Observer)
}

trait Observer {
  var temperature: Float = _
  var humidity: Float = _
  var pressure: Float = _


  def update(subject: Subject, obj: Any)
}

trait DisplayElement {
  val name = getClass.getSimpleName.toLowerCase(Locale.ROOT)

  protected def func(): Unit

  def display() = {
    println("=======" + name + "=======")
    func()
  }
}

class CurrentConditionDisplay extends Observer with DisplayElement {
  override def update(subject: Subject, obj: Any): Unit = {
    obj match {
      case None =>
        val weather = subject.asInstanceOf[WeatherData]
        this.temperature = weather.temperature
    }
    display()
  }

  override protected def func(): Unit = {
    println("temperature: " + temperature)
  }
}

class StatisticsDisplay extends Observer with DisplayElement {
  override def update(subject: Subject, obj: Any): Unit = {
    obj match {
      case None =>
        val weather = subject.asInstanceOf[WeatherData]
        this.pressure = weather.pressure
        this.humidity = weather.humidity
    }
    display()
  }

  override protected def func(): Unit = {
    println("pressure: " + pressure)
    println("humidity: " + humidity)
  }
}

class ThirdPartyDisplay extends Observer with DisplayElement {
  override def update(subject: Subject, obj: Any): Unit = {
    obj match {
      case None =>
        val weather = subject.asInstanceOf[WeatherData]
        this.temperature = weather.temperature
        this.humidity = weather.humidity
    }
    display()
  }

  override protected def func(): Unit = {
    println("humidity: " + humidity)
    println("temperature" + temperature)
  }
}

class ForecastDisplay extends Observer with DisplayElement {
  override def update(subject: Subject, obj: Any): Unit = {
    obj match {
      case None =>
        val weather = subject.asInstanceOf[WeatherData]
        this.temperature = weather.temperature
        this.pressure = weather.pressure
        this.humidity = weather.humidity
    }
    display()
  }

  override protected def func(): Unit = {
    println("temperature: " + temperature)
    println("pressure: " + pressure)
    println("humidity: " + humidity)
  }
}