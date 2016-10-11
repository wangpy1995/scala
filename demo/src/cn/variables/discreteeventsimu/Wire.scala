package cn.variables.discreteeventsimu

/**
  * Created by Wpy on 2016/10/9.
  * 線路，包括：反轉器(inverter)，與門(and-Gate)，或門(or-Gate)
  */
/*
class Wire {
  var a, b, c = new Wire

  def inverter(input: Wire, output: Wire)

  def andGate(a1: Wire, a2: Wire, output: Wire)

  def orGate(o1: Wire, o2: Wire, output: Wire)

  //半加法器
  def halfAdder(a: Wire, b: Wire, s: Wire, c: Wire) {
    val d, e = new Wire
    orGate(a, b, d)
    andGate(a, b, c)
    inverter(c, e)
    andGate(d, e, s)
  }

  //全加法器
  def fullAdder(a: Wire, b: Wire, cin: Wire, sum: Wire, cout: Wire) {
    val s, c1, c2 = new Wire
    halfAdder(a, cin, s, c1)
    halfAdder(b, s, sum, c2)
    orGate(c1, c2, cout)
  }
}
*/
