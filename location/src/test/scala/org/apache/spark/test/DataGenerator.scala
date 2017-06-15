package org.apache.spark.test

import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousFileChannel, CompletionHandler}
import java.nio.file.StandardOpenOption
import java.util.concurrent.CountDownLatch

import sun.nio.ch.DirectBuffer

import scala.util.Random

/**
  * Created by wpy on 2017/6/13.
  */
object DataGenerator {

  def main(args: Array[String]): Unit = {
    val countDownLatch = new CountDownLatch(10)
    for (i <- 1 to 10) {
      val file = new File(s"/home/wpy/tmp/test/t$i")
      //      if (!file.exists() || file.isDirectory) file.createNewFile()
      val writer = AsynchronousFileChannel.open(file.toPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
      val kvs = (for (_ <- 0 until 100) yield {
        Math.abs(Random.nextInt(10000000)) + " " +
          Random.nextString(5) + " " + Random.nextString(10) +
          " " + Random.nextString(10) + " " + Random.nextString(15) +
          " " + Random.nextString(5)
      }).mkString("\n").getBytes()
      val buf = ByteBuffer.allocateDirect(kvs.length + 1)
      val handler = new CompletionHandler[Integer, Array[Byte]] {
        override def failed(exc: Throwable, attachment: Array[Byte]): Unit = exc.printStackTrace()

        override def completed(result: Integer, attachment: Array[Byte]): Unit = {
          if (result > 0) {
            println(s"complete, result=$result")
            buf.clear()
            buf.asInstanceOf[DirectBuffer].cleaner().clean()
            countDownLatch.countDown()
          } else writer.close()
        }
      }
      buf.put(kvs)
      buf.flip()
      writer.write(buf, 0, kvs, handler)
    }
    countDownLatch.await()
  }
}
