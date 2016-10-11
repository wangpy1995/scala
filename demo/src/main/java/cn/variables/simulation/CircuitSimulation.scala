package cn.variables.simulation

/**
  * Created by Wpy on 2016/10/10.
  */
abstract class CircuitSimulation extends BasicCircuitSimulation {

  /**
    * 半加法器
    *
    * @param a 輸入1
    * @param b 輸入2
    * @param s 累加和
    * @param c 進位
    */
  def halfAdder(a: Wire, b: Wire, s: Wire, c: Wire) {
    val d, e = new Wire
    orGate(a, b, d)
    andGate(a, b, c)
    inverter(c, e)
    andGate(d, e, s)
  }

  /**
    * 全加法器
    * @param a    輸入1
    * @param b    輸入2
    * @param cin  進位
    * @param sum  累加輸出
    * @param cout 進位輸出
    */
  def fullAdder(a: Wire, b: Wire, cin: Wire, sum: Wire, cout: Wire) {
    val s, c1, c2 = new Wire
    halfAdder(a, cin, s, c1)
    halfAdder(b, s, sum, c2)
    orGate(c1, c2, cout)
  }
}

object MySimulation extends CircuitSimulation {
  def InverterDelay = 1

  def AndGateDelay = 3

  def OrGateDelay = 5

  val input1, input2, sum, carry = new Wire

  def main(args: Array[String]) {
    probe("sum", sum)
    probe("carry", carry)
    halfAdder(input1, input2, sum, carry)
    input1 setSignal true
    run()
    input2 setSignal true
    run()
  }
}
