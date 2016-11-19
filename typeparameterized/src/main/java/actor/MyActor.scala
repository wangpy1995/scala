package actor

import scala.actors.Actor

/**
  * Created by w5921 on 2016/11/18.
  */
class MyActor extends Actor {
  override def act(): Unit = {
    while (true) {
      receive {
        case "start" => {
          println("Starting....")
          Thread.sleep(2000)
          println("Started....")
        }
        case "stop" => {
          println("Stopping....")
          Thread.sleep(2000)
          println("Stopped....")
        }
        case "exit"=>{
          println("exiting....")
          exit()
        }
      }
    }
  }
}

object MyActor {
  def main(args: Array[String]): Unit = {
    val actor = new MyActor
    actor.start()
    actor ! "start"
    actor ! "stop"
    actor ! "exit"
    println("finished")
  }
}
