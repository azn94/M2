package datacloud.scala.tpfonctionnel

object FunctionParty {
  
  def curryfie[A, B, C](f : (A, B) => C): A => B => C = {
    return ((a : A) => ((b : B) => f(a,b)));
  }
  
  def decurryfie[A, B, C](f : A => B => C):(A, B) => C ={
    return ((a: A, b : B) => f(a)(b));
  }
  
  def compose[A, B, C](f : B => C, g : A => B): A => C = {
    return (a : A) => f(g(a));
  }
  
  def axplusb(a : Int, b : Int): Int => Int = {
    def sum = (a : Int, b : Int) => a + b;
    def multiply = (a : Int, b: Int) => a * b;
    
    return compose(curryfie(sum)(b),curryfie(multiply)(a));
  }
}