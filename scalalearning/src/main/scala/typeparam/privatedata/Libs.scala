package typeparam



//书籍类
class Book(title: String) extends Publication(title)

//图书库类
object Library {
  val books: Set[Book] = Set(new Book("scala"), new Book("java"))

  def printBookList(p: Book => AnyRef): Unit = {
    for (book <- books) println(book)
  }

  def printBookListByTrait[P >: Book, R <: AnyRef](
                                                    action: GetInfoAction[P, R]) {
    //打印
    for (book <- books)
      println(action(book))
  }
}

//取得图书内容特征，P类型参数的类型下界是Book，R类型参数的类型上界是AnyRef
trait GetInfoAction[P >: Book, R <: AnyRef] {
  //取得图书内容的文本描述，对应（）操作符
  def apply(book: P): R
}

//单例对象，文件的主程序
object Customer {
  //定义取得出版物标题的函数
  def getTitle(p: Publication): String = p.title

  def main(args: Array[String]): Unit = {

    //使用函数来打印
    Library.printBookList(getTitle)

    //使用特征GetInfoAction的实例来打印
    Library.printBookListByTrait(new GetInfoAction[Publication, String] {
      def apply(p: Publication): String = p.title
    })
  }
}