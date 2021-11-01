package datacloud.scala.tpfonctionnel.test

import org.junit.Test
import org.junit.Assert._
import datacloud.scala.tpfonctionnel.Premiers._


class PremiersTest {
  
  def f(p:Int=>List[Int]){
    val l = p(100)
    assertEquals(25,l.size)
    assertTrue(l.contains(2))
    assertTrue(l.contains(3))
    assertTrue(l.contains(5))
    assertTrue(l.contains(7))
    assertTrue(l.contains(11))
    assertTrue(l.contains(13))
    assertTrue(l.contains(17))
    assertTrue(l.contains(19))
    assertTrue(l.contains(23))
    assertTrue(l.contains(29))
    assertTrue(l.contains(31))
    assertTrue(l.contains(37))
    assertTrue(l.contains(41))
    assertTrue(l.contains(43))
    assertTrue(l.contains(47))
    assertTrue(l.contains(53))
    assertTrue(l.contains(59))
    assertTrue(l.contains(61))
    assertTrue(l.contains(67))
    assertTrue(l.contains(71))
    assertTrue(l.contains(73))
    assertTrue(l.contains(79))
    assertTrue(l.contains(83))
    assertTrue(l.contains(89))
    assertTrue(l.contains(97))
  }
  
  
  @Test
  def test1()={
    f(premiers)
  }
  
  @Test
  def test2()={
    f(premiersWithRec)
  }
}