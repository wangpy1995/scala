package filter

class UserDefinedLessThan(v: String, idx: Int, typeName: String) extends UserDefinedFilter {
  override def filter(objects: Array[Any]): Boolean = false

  override def genCode(): String = {
    s"(($typeName)objects[$idx])< $v"
  }
}
