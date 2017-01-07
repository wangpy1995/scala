//import java.io.{FileWriter, PrintWriter}
//
//import scala.actors.{Actor, Future}
//import scala.collection.mutable.{ArrayBuffer, ListBuffer}
//import scala.io.Source
//import scala.util.Random
//
///**
//  * Created by w5921 on 2016/11/19.
//  */
//class ReservoirSample(val file: String, val k: Int) extends Actor {
//  def reservoir(): Array[String] = {
//    val lines = Source.fromFile(file).getLines()
//    val arr = new Array[String](k)
//    var i = 0
//
//    while (i < k) {
//      if (lines.hasNext) {
//        arr(i) = lines.next()
//        i += 1
//      }
//    }
//
//    while (lines.hasNext) {
//      val line = lines.next()
//      val p = new Random(System.nanoTime()).nextInt(i + 1)
//      if (p < k)
//        arr(p) = line
//      i += 1
//    }
//    arr
//  }
//
//  override def act(): Unit = {
//    react {
//      case "start" => {
//        sender ! reservoir()
//        act
//      }
//      case "stop" => exit()
//    }
//  }
//}
//
//object TestRandom {
//  def main(args: Array[String]): Unit = {
//    val replyList = new ListBuffer[Future[Any]]()
//    val resultList = new ListBuffer[Array[String]]()
//    val writer = new PrintWriter(new FileWriter("./demo/words.log", true))
//    for (i <- 1 to 10) {
//      val reservoir = new ReservoirSample("./demo/words.txt", 1)
//      val reply = reservoir.start() !! "start"
//      replyList += reply
//      reservoir !! "stop"
//    }
//
//    while (replyList != null) {
//      val toString = replyList.filter(_.isSet)
//      for (s <- toString) {
//        resultList += s().asInstanceOf[Array[String]]
//        replyList -= s
//      }
//    }
//    resultList.foreach(s => writer.write(s + "\n"))
//    writer.close()
//    /*for (i <- 1 to 100) {
//      val reservoir = new ReservoirSample("./demo/words.txt", 1)
//      reservoir.reservoir().foreach(r => println(s"$i $r"))
//    }*/
//  }
//}