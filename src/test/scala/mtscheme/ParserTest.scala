package mtscheme

import org.scalatest.FunSuite

class ParserTest extends FunSuite {

  test("Parse OpenClose") {
    val res = List(Comb(List()))
    assertResult(res) { Parser.parse("()") }
  }

  test("Parse Multiple") {
    val res = List(Value(Num(1.0)), Value(Num(2.0)))
    assertResult(res) { Parser.parse("1 2") }
  }

  test("Parse Expr") {
    val res = List(Comb(List(Symbol("+"), Value(Num(1.0)), Value(Num(2.0)))))
    assertResult(res) { Parser.parse("(+ 1 2)") }
  }
}
