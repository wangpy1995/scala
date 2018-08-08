package filter

import code.CodeGenInterface

case class UserDefinedEqual(v: String, idx: Int, typeName: String) extends UserDefinedFilter with CodeGenInterface {

  override def filter(objects: Array[Any]) = false

  override def genCode(): String = {
    s"(($typeName)objects[$idx]) == $v"
  }
}
