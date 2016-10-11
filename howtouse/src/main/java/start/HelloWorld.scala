import scala.io.Source

/**
  * Created by Wpy on 2016/8/19.
  */

//import scala.collection.mutable.Set

object HelloWorld {

  def main(args: Array[String]): Unit = {
    //maxInt(1, 2)
    //parameterizeType()
    //testFor()
    //testLists()
    //testTuple()
    //testSetAndMap()
    readLine("http://12.33.41.36:3000/projects/b20160412000/boards/444")
  }

  //讀寫文件
  def readLine(path: String): Unit = {
    if (path != null)
      for (line <- Source.fromURL(path).getLines())
        println(line.length + " : " + line)
    else
      Console.err.print("please enter a valid URL")
  }

  //編程風格
  def printArgs(args: Array[String]): Unit = {
    //指令式編程示例
    var i = 0
    while (i < args.length) {
      println(args(i))
      i += 1
    }
    //函數式編程
    args.foreach(println)
    for (arg <- args) {
      println(arg)
    }
  }


  //Set和Map(Immutable)
  def testSetAndMap(): Unit = {
    var set = Set("Beijing", "China")
    set += "America"
    set -= "Beijing"
    set.foreach(println)
    val map = Map(1 -> "Beijing", 2 -> "Shanghai", 3 -> "Guangzhou", 4 -> "Shengzhen", 5 -> "Hangzhou")
    /*for (m <- map.toList)
      println(m._2)*/
    map.values.foreach(println)
  }

  //使用元組
  def testTuple(): Unit = {
    val pair = (45, "Luftballons")
    println(pair._1)
    println(pair._2)
  }

  //使用Lists
  def testLists(): Unit = {
    val oneTwo = List(1, 2)
    val threeFour = List(3, 4)
    val oneToFour = oneTwo ::: threeFour
    println(oneTwo + " and " + threeFour + "~~~~~~~~~")
    print(oneToFour)
    //    val oneToThree = 1 :: 2 :: 3 :: Nil
    val oneToThree = oneTwo :: 3 :: Nil
    print("oneTwo: " + oneToThree)
  }

  def testFor(): Unit = {
    val str = Array("I", "Like", "Scala")
    //str.foreach(println)
    //str.foreach(str => println(str))
    for (s <- str) {
      println(s)
    }
  }

  //使用類型參數化數組
  def parameterizeType(): Unit = {
    val greetingStrings = new Array[String](3)
    greetingStrings(0) = "Hi"
    greetingStrings(1) = "Hello"
    greetingStrings(2) = "Good morning"
    //greetingStrings.foreach(println)
    for ( /*i <- 0 to 2 */ i <- 0.to(2))
      println(greetingStrings(i))
  }

  def maxInt(x: Int, y: Int): Int = {
    if (x > y)
      x
    else
      y
  }
}
