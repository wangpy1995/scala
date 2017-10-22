package actor

import akka.actor.Actor


/**
  * Created by wpy on 2017/5/6.
  */
class SillyActor extends Actor {
  override def receive: Receive = {
    case i: Int => println(s"Int: $i ")
    case other => println(s"other: $other")
  }
}
