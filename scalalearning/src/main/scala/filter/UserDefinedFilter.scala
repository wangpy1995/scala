package filter

import code.CodeGenInterface

abstract class UserDefinedFilter extends CodeGenInterface {
  protected val childs: Seq[UserDefinedFilter] = null

  def filter(objects: Array[Any]): Boolean
}
