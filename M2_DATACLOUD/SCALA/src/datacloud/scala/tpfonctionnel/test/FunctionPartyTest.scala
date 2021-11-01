package datacloud.scala.tpfonctionnel.test

import org.junit.Test
import org.junit.Assert._
import datacloud.scala.tpfonctionnel.FunctionParty._
class FunctionPartyTest {
 
  def testcurrying[A,B,C](f :(A, B)=>C, a:A,b:B )={
    val f_curr = curryfie(f)
    val f_decurr = decurryfie(f_curr)
    assertTrue(f_curr(a)(b) == f_decurr(a,b))
  }
  
  @Test
  def test={
    testcurrying((x:Int, y:Int) => x+ y, 3,4)
    testcurrying((x:Int, y:Int) => x* y, 3,4)
    testcurrying((x:String, y:Double) => x+y, "bonjour",4.5)
  }
  
  @Test
  def testCompose={
    val f1=axplusb(3, 4)
    val f2=axplusb(2, 1)
    
    assertEquals(13,f1(3))
    assertEquals(7,f2(3))
  }
  
}