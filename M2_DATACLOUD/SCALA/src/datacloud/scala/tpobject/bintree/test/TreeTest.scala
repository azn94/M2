package datacloud.scala.tpobject.bintree.test
import org.junit.Test
import org.junit.Assert._
import datacloud.scala.tpobject.bintree.BinTrees._
import datacloud.scala.tpobject.bintree.EmptyTree
import datacloud.scala.tpobject.bintree.Node
import datacloud.scala.tpobject.bintree.Tree

class TreeTest {
    
  def test[A](f:Int=>A)={
    var tree:Tree[A] = EmptyTree
    for(v <- 1 to 10){
      tree=insert(tree,f(v))
    }
    assertEquals(10,size(tree))    
    for(v <- 1 to 10){
      assertTrue(contains(tree,f(v)))
    }
  
    for(v <- 11 to 20){
      assertFalse(contains(tree,f(v)))
    }
    
    tree match {
      case EmptyTree => assertTrue(false)
      case Node(e,l,r) => { 
        assertEquals(4, size(l))
        assertEquals(5, size(r))
      }
    }
  }
  
  
  @Test
  def testString={
    test(_.toString())
  }
  
  
  @Test
  def testDouble={
    test(_.toDouble)
  }
  
  @Test
  def testList={
    test(i=>List(i,i+1,i+2))
  }
  
}