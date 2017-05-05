package typeparam

/**
  * Created by wpy on 2017/5/4.
  */

class Biology(val name: String) {
  override def toString: String = s"Biological name: $name ----${hashCode()}"
}

class Animal(name: String, val gender: String, val age: Int) extends Biology(name) {
  override def toString: String = s"Animal name: $name, gender: $gender, age: $age ----$hashCode()"
}

class BiologicalType {
  val animals = Seq(new Animal("ani", "male", 11), new Animal("cat", "female", 11))
  val bios = Seq(new Biology("bio"), new Biology("b"))

  def printBiology(f: Animal => Biology): Unit = {
    for (animal <- animals)
      println(f(animal))
  }

  def print[P >: Animal, R <: Biology](action: Bio2Ani[P, R]): Unit = {
    animals.foreach { bio =>
      println(action(bio.asInstanceOf[P]))
    }
  }

}

trait Bio2Ani[P >: Animal, R <: Biology] {
  def apply(x: P): R
}

object Test {
  def main(args: Array[String]): Unit = {
    val bt = new BiologicalType()
    bt.printBiology(animal => new Biology(animal.name))
    bt.print(new Bio2Ani[Biology, Animal] {
      override def apply(x: Biology): Animal = new Animal(x.name, "female", 22)
    })
  }
}