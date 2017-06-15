package test.event.model

/**
  * Created by wpy on 17-6-10.
  */
class Reader(name: String) extends WriterEventListener {

  def subscribe(writerName: String): Unit = {
    WriterManager.writers(writerName).registListener(this)
  }

  def unsubscribe(writerName: String): Unit = {
    WriterManager.writers(writerName).unregistListener(this)
  }

  override def addEvent(event: WriteEvent): Unit = {
    val writer = event.writer
    println(name + "知道" + writer.name + "发布了新书《" + writer.novel + "》，非要去看！")
  }
}
