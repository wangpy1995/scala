package cases

import scala.util.Random

/**
  * Created by w5921 on 2016/11/6.
  */
case class SubmitTask(id: String, name: String)

case class HeartBeat(time: Long)

case object CheckTimeoutTask

object CaseDemo extends App {
  val arr = Array(CheckTimeoutTask, HeartBeat(12333), SubmitTask("0001", "task-0001"))
  val a = CheckTimeoutTask
  val b = CheckTimeoutTask
  arr(Random.nextInt(arr.length)) match {
    case SubmitTask(id, name) => println(s"$id,$name")
    case HeartBeat(time) => println(time)
    case CheckTimeoutTask => println("check")
  }
}