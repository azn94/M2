package datacloud.scala.tpfonctionnel.test

import org.junit.Test
import org.junit.Assert._
import datacloud.scala.tpfonctionnel.MySorting._
import scala.math.Ordering._

class MySortingTest {
  
  def test[T](sorted:Array[T],
      nonsorted:Array[T])(implicit o:Ordering[T])={
    assertTrue(isSorted(sorted,ascending[T]))
    assertFalse(isSorted(sorted,descending[T]))
    assertFalse(isSorted(sorted.reverse, ascending[T]))
    assertTrue(isSorted(sorted.reverse, descending[T]))
  
    assertFalse(isSorted(nonsorted, ascending[T]))
    assertFalse(isSorted(nonsorted, descending[T]))
    assertFalse(isSorted(nonsorted.reverse, ascending[T]))
    assertFalse(isSorted(nonsorted.reverse, descending[T]))
  }
  
  
  @Test
  def testInt={
    test(Array(3,4,5,6,10), Array(13,4,6,7,49))   
  }
  
  @Test
  def testString={
    test(Array("ab","cd","ehuiius","ghit","zoopoi"), 
         Array("zert","sdfg","dgdf","qsae","aezs"))   
  }
  
  
  @Test
  def testDouble={
    test(Array(-25.3,0.0,854,2023.054,8954.10254), 
         Array(564.2,-0.321,56.2,78.2,45.3564))   
  }
}