package org.apache.spark.test

import java.util.concurrent.atomic.AtomicInteger

import scala.util.Random

/**
  *
  * Created by wpy on 2017/6/8.
  */
object TestSemaphore {
  val THREAD_COUNT = 20
  val CALL_COUNT_NOW = new AtomicInteger(0)
  val QUERY_MAX_LENGTH = 5
  val LOCK_OBJ = new Object

  def tryLock(): Unit = {
    var tryTimes = 0
    var flag = true
    var nowVal = CALL_COUNT_NOW.get()
    while (flag) {
      if (nowVal < QUERY_MAX_LENGTH
        && CALL_COUNT_NOW.compareAndSet(nowVal, nowVal + 1)) flag = false
      if (tryTimes % 3 == 0) waitForObjectNotify()
      nowVal = CALL_COUNT_NOW.get()
      tryTimes += 1
    }
  }

  def waitForObjectNotify(): Unit = {
    LOCK_OBJ.synchronized(LOCK_OBJ.wait(500))
  }

  def tryUnLock(): Unit = {
    CALL_COUNT_NOW.getAndDecrement()
    LOCK_OBJ.synchronized(LOCK_OBJ.notify())
  }

  def main(args: Array[String]): Unit = {
    for (i <- 0 until THREAD_COUNT) {
      new Thread(i.formatted("%02d")) {
        override def run(): Unit = {
          tryLock()
          println(s"${this.getName} ====== Operation Start")
          Thread.sleep(100 + Random.nextInt() & 3000)
          println(s"${this.getName} ====== Operation Ended")
          tryUnLock()
        }
      }.start()
    }
  }
}