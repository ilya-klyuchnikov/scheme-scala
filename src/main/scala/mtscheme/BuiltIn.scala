package mtscheme

import mtscheme.Interpreter._

object BuiltIn {

  def aritFun(op: ((BigDecimal, BigDecimal) => BigDecimal))
             (env: Environment, comb: List[Expression]) = {
    val error = new IllegalArgumentException("arithmetic error")
    comb.map(e => eval(env, e)._2) match {
      case Value(Num(first)) :: t =>
        val res = t.foldLeft(first)((acc, e) =>
          e match { case Value(Num(v)) => op(acc,v); case _ => throw error })
        (env,Value(Num(res)))
      case _                      => throw error
    }
  }

  def combFun(op: ((BigDecimal, BigDecimal) => Boolean))
             (env: Environment, comb: List[Expression]) = {
    val error = new IllegalArgumentException("comparison error")
    comb.map(e => eval(env, e)._2) match {
      case Value(Num(first)) :: t =>
        val res = t.foldLeft((true, first))((acc, e) => e match {
          case Value(Num(v)) => (acc._1 && op(acc._2, v), v)
          case _ => throw error })
        (env,Value(Bool(res._1)))
      case _                      => throw error
    }
  }

  def _not(env: Environment, comb: List[Expression]) = comb match {
    case expr :: Nil  => eval(env, expr) match {
      case (_, Value(Bool(v)))  => (env, Value(Bool(!v)))
      case _                    => throw new IllegalArgumentException("not")
    }
    case _            => throw new IllegalArgumentException("not")
  }

  def _if(env: Environment, comb: List[Expression]) = {
    val error = new IllegalArgumentException("if")
    val (condExpr, posExpr, negExpr) = comb match {
      case condExpr :: posExpr :: negExpr :: Nil  =>
        (condExpr, posExpr, Some(negExpr))
      case condExpr :: posExpr :: Nil             =>
        (condExpr, posExpr, None)
      case _                                      => throw error
    }
    (eval(env, condExpr))._2 match {
        case Value(Bool(c)) =>
          if (c)
            eval(env, posExpr)
          else negExpr match {
            case Some(e)    => eval(env, e)
            case None       => (env, NullExpr())
          }
        case _              => throw error
     }
  }

  val globalEnv = Environment(Env(EnvMap(
                      ("+" -> Procedure(aritFun(_+_) _)),
                      ("-" -> Procedure(aritFun(_-_) _)),
                      ("*" -> Procedure(aritFun(_*_) _)),
                      ("/" -> Procedure(aritFun(_/_) _)),

                      ("=" -> Procedure(combFun(_==_) _)),
                      (">" -> Procedure(combFun(_>_) _)),
                      ("<" -> Procedure(combFun(_<_) _)),
                      (">=" -> Procedure(combFun(_>=_) _)),
                      ("<=" -> Procedure(combFun(_<=_) _)),

                      ("true" -> Value(Bool(true))),
                      ("false" -> Value(Bool(false))),

                      ("not" -> Procedure(_not)),
                      ("if" -> Procedure(_if))
    )))
}