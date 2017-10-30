package netty

import netty.client.SimpleClient
import netty.server.SimpleServer
import org.scalatest.FunSuite

import scala.io.StdIn

class SimpleServerTestSuite extends FunSuite {

  test("server") {
    val server = new SimpleServer()
    server.start("localhost",8080)
    StdIn.readLine()
  }

  test("client"){
    val client = new SimpleClient()
     0 until 10 foreach { _ =>
      new Thread(new Runnable {
        override def run(): Unit =
          client.connect("localhost", 8080)
      }).start()
    }
    StdIn.readLine()
  }

}
