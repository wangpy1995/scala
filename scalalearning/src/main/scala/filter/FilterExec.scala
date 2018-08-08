package filter

trait FilterExec {

  def eval(objects: Array[Any]): Boolean

}
