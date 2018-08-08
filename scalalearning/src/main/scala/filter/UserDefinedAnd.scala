package filter

case class UserDefinedAnd(filters: Seq[UserDefinedFilter]) extends UserDefinedFilter {
  override def filter(objects: Array[Any]) = false

  override def genCode(): String = {
    filters.map(_.genCode()).mkString("&& ")
  }

  override protected val childs = filters
}
