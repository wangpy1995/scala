package actor

import akka.actor.{Actor, Props}
import akka.event.Logging


/**
  * Created by wpy on 17-6-17.
  */
class MyActor extends Actor {

  private[this] val log = Logging(context.system, this)

  val props = Props[MyActor]
  props.deploy

  override def receive: Receive = {
    case "test" => log.info("test")
    case _ => log.info("default")
  }
}
