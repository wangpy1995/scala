package actor

import scala.actors.Actor

/**
  * Created by w5921 on 2016/11/6.
  */
object MyActor1 extends Actor {
  override def act(): Unit = {
    for (i <- 1 to 10) {
      println(s"actor-1 $i")
      Thread.sleep(2000)
    }
  }
}

object MyActor2 extends Actor {
  override def act(): Unit = {
    for (i <- 1 to 10) {
      println(s"actor-2 $i")
      Thread.sleep(2000)
    }
  }
}

object TestActor extends App {
  MyActor1.start()
  MyActor2.start()
  println("main")
}