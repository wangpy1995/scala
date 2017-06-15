package test.event.model

import scala.collection.mutable

/**
  * Created by wpy on 17-6-10.
  */
class Writer(val name: String) {

  private[this] val writerEventListeners = new mutable.HashSet[WriterEventListener]()
  WriterManager.writers.put(name, this)
  var novel: String = _

  def addNovel(novel: String): Unit = {
    println(s"${name}发布了新书《$novel》")
    this.novel = novel
    val event = new WriteEvent(this)
    //通知listener
    for (writerListener <- writerEventListeners) writerListener.addEvent(event)
  }

  def registListener(writerEventListener: WriterEventListener): Unit = {
    writerEventListeners.add(writerEventListener)
  }

  def unregistListener(writerEventListener: WriterEventListener): Unit = {
    writerEventListeners.remove(writerEventListener)
  }
}
