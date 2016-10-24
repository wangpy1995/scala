package wpy.scala.hive.jdbc

import java.sql.SQLException

/**
  * Created by w5921 on 2016/10/25.
  */

class JdbcUriParseException(msg: String, cause: Throwable) extends SQLException(msg, cause) {

}