package actor


import scala.actors.{Actor, Future}
import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by w5921 on 2016/11/18.
  */
class Task extends Actor {
  override def act(): Unit = {
    loop {
      react {
        case SubmitTask(fileName) => {
          sender ! ResultTask(Source.fromFile(fileName).getLines()
            .flatMap(_.split(" ")).map((_, 1)).toList.groupBy(_._1).mapValues(_.size))
        }

        case StopTask => {
          exit()
        }
      }
    }
  }
}

case class SubmitTask(fileName: String)

case class ResultTask(result: Map[String, Int])

case object StopTask

object ActorWordCount {
  def main(args: Array[String]): Unit = {
    val replySet = new scala.collection.mutable.HashSet[Future[Any]]()
    val resultList = new ListBuffer[ResultTask]()
    val files = Array[String]("e://words.txt", "e://words.log")
    for (f <- 0 until Runtime.getRuntime.availableProcessors()) {
      if (f < files.length) {
        val actor = new Task
        val reply = actor.start() !! SubmitTask(files(f))
        replySet += reply
        actor !! StopTask
      }
    }

    while (replySet.nonEmpty) {
      val toCompute = replySet.filter(_.isSet)
      for (f <- toCompute) {
        resultList += f().asInstanceOf[ResultTask]
        replySet -= f
      }
      Thread.sleep(100)
    }
    println(resultList.flatMap(_.result).groupBy(_._1).mapValues(_.foldLeft(0)(_ + _._2)))
  }
}