package codegen

import java.io.StringReader

import filter.{FilterExec, UserDefinedAnd, UserDefinedEqual}
import org.codehaus.janino._
import org.scalatest.FunSuite

class CodeGenSuite extends FunSuite {

  val testDatas = Seq(Array(1, "xx", 3.0F, 5.9D, 4), Array(2, "xx", 3.0F, 5.9D, 4), Array(3, "xx", 3.0F, 5.9D, 4))
  val equal2 = UserDefinedEqual("1", 0, "java.lang.Integer")
  val equal3 = UserDefinedEqual("\"xx\"", 1, "java.lang.String")
  val equal1 = UserDefinedEqual("3.0", 2, "java.lang.Float")
  val and = UserDefinedAnd(Seq(equal1, equal2, equal3))

  test("code_gen") {
    val id = 0
    val code =
      s"""
         |    @Override
         |    public boolean eval(Object[] objects) {
         |        return ${and.genCode()};
         |    }
      """.stripMargin


    val filter = ClassBodyEvaluator.createFastClassBodyEvaluator(
      new Scanner(null, new StringReader(code)),
      classOf[FilterExec],
      null).asInstanceOf[FilterExec]

    val res = testDatas.filter(filter.eval)
    res.map(_.mkString(", ")).foreach(println)

  }

}
