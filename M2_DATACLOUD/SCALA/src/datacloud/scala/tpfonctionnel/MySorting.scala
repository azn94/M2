package datacloud.scala.tpfonctionnel

import scala.Array

object MySorting{
  def isSorted[A](tab : Array[A], f : (A,A)=> Boolean) : Boolean = {
    tab.length match{
      case 0 | 1 => return true;
      case _ => if(f(tab(0),tab(1))){
                  return isSorted(tab.drop(1),f);
                }else{
                  return false;
                }
    }
  }
  
  def ascending[T](a : T, b : T)(implicit order : Ordering[T]): Boolean = {
    return order.lteq(a, b);
  }
  
  def descending[T](a : T, b : T)(implicit order : Ordering[T]): Boolean = {
    return order.gteq(a, b);
  }
}