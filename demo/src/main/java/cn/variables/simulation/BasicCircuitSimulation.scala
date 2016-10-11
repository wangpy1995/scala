package cn.variables.simulation

/**
  * Created by Wpy on 2016/10/10.
  */
abstract class BasicCircuitSimulation extends Simulation {

  def InverterDelay: Int

  def AndGateDelay: Int

  def OrGateDelay: Int

  class Wire {
    private var sigVal = false
    private var actions: List[Action] = List()

    def getSignal = sigVal

    /**
      * sigVal和actions組合成線路狀態
      *
      * @param s
      */
    def setSignal(s: Boolean) =
      if (s != sigVal) {
        //線路信號改變時，新的信號首先被保存在sigVal中，再執行線路中所有線路附加的動作
        sigVal = s
        //_ ()可以帶函數作參數
        actions foreach (_ ())
      }

    def addAction(a: Action) = {
      actions = a :: actions
      a()
    }
  }

  def inverter(input: Wire, output: Wire) = {
    def inverterAction() {
      val inputSig = input.getSignal
      afterDelay(InverterDelay) {
        output setSignal !inputSig
      }
    }
    input addAction inverterAction
  }

  def andGate(a1: Wire, a2: Wire, output: Wire) = {
    def andAction() {
      val a1Sig = a1.getSignal
      val a2Sig = a2.getSignal
      afterDelay(AndGateDelay) {
        output setSignal (a1Sig & a2Sig)
      }
    }
    a1 addAction andAction
    a2 addAction andAction
  }

  def orGate(o1: Wire, o2: Wire, output: Wire) = {
    def orAction() = {
      val o1Sig = o1.getSignal
      val o2Sig = o2.getSignal
      afterDelay(OrGateDelay) {
        output setSignal (o1Sig | o2Sig)
      }
    }
    o1 addAction orAction
    o2 addAction orAction
  }

  def probe(name: String, wire: Wire): Unit = {
    def probeAction() = {
      println(name + " " + currentTime + " new value " + wire.getSignal)
    }
    wire addAction probeAction
  }
}
