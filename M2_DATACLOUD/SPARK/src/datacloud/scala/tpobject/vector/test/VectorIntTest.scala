package datacloud.scala.tpobject.vector.test
import org.junit._
import org.junit.Assert._
import datacloud.scala.tpobject.vector.VectorInt
import datacloud.scala.tpobject.vector.VectorInt._

class VectorIntTest {
  @Test
  def test={
    val v1 = new VectorInt(Array(1,4,1))
    val v2 = new VectorInt(Array(2,-1,4))
    val v3 = new VectorInt(Array(2,-1,4,6,10,-3))
    val v4 = new VectorInt(Array(1,4,1))
    

    assertEquals(3,v1.length)
    assertEquals(3,v2.length)
    assertEquals(6,v3.length)
    assertEquals(3,v4.length)
    assertTrue(v4==v1)
    assertTrue(v1==v1)
    assertTrue(v4==v4)
    assertTrue(v3 != v2)
    assertTrue(v2 != v3)
    assertTrue(v1 != v3)
    assertTrue(v1 != v2)
    
    assertEquals(new VectorInt(Array(3,3,5)),v1 + v2)
    assertEquals(new VectorInt(Array(3,3,5)), v2 + v1 )
    assertTrue(v1 + v1 == v4 +v1)
  
    assertTrue(v1 + v1 == v4 +v1)
    assertTrue(v3 * 10 == v3 + (v3*9) )
    assertTrue(v2 * 2 == v2 + v2)
    
    val p = v2.prodD(v1)
    assertEquals(3, p.length)
    assertEquals(new VectorInt(Array(2,8,2) ),  p(0))
    assertEquals(new VectorInt(Array(-1,-4,-1) ),  p(1))
    assertEquals(new VectorInt(Array(4,16,4) ),  p(2))
    
  }
  
  @Test
  def testImplicit={
    val v1 = Array(1,4,1)
    val v2=v1+(Array(1,1,1))
    assertEquals(2, v2.get(0))
    assertEquals(5, v2.get(1))
    assertEquals(2, v2.get(2))
  }
}