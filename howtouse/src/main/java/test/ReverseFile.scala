package test

import java.io.PrintWriter

import scala.io.Source

/**
  * Created by Wpy on 2016/9/12.
  */
object ReverseFile {
  /**
    * 1. 文件中的行倒轉順序并保存到另一文件中
    *
    * @param file
    * @param reversedFile
    * @return
    */
  def reverseFile(file: String, reversedFile: String): String = {
    val src = Source.fromFile(file).getLines().toArray
    val dst = src.reverse
    val reverse = new PrintWriter(reversedFile)
    dst.foreach {
      line => reverse.write(line + "\n")
    }
    reverse.close()
    reversedFile
  }

  /**
    * 2. 製錶符轉空格
    *
    * @param file
    * @param spacedFile
    * @return
    */
  def tab2Space(file: String, spacedFile: String): String = {
    val src = Source.fromFile(file).getLines().toArray
    val writer = new PrintWriter(spacedFile)
    src.foreach {
      tab => src.toString.replaceAll("\t", " ")
        writer.write(tab + "\n")
    }
    writer.close()
    spacedFile
  }

  def main(args: Array[String]) {
    //Source.fromFile(reverseFile("E:\\projects\\scala\\howtouse\\src\\File.txt", "E:\\projects\\scala\\howtouse\\src\\ReverseFile")).getLines().foreach(println)
    Source.fromFile(tab2Space("D:/IdeaProjects/scala/howtouse/src/main/java/File.txt", "D:/IdeaProjects/scala/howtouse/src/main/java/ReverseFile")).getLines().foreach(println)

  }
}