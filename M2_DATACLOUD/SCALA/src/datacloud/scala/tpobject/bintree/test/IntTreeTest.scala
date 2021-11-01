package datacloud.scala.tpobject.bintree.test

import org.junit.Test
import org.junit.Assert._
import datacloud.scala.tpobject.bintree.BinTrees._
import datacloud.scala.tpobject.bintree.EmptyIntTree
import datacloud.scala.tpobject.bintree.IntTree
import datacloud.scala.tpobject.bintree.NodeInt

class IntTreeTest {
  
  @Test
  def test={
    var tree:IntTree = EmptyIntTree
    for(v <- 1 to 10){
      tree=insert(tree,v)
    }
    assertEquals(10,size(tree))    
    for(v <- 1 to 10){
      assertTrue(contains(tree,v))
    }
  
    for(v <- 11 to 20){
      assertFalse(contains(tree,v))
    }
    
    tree match {
      case EmptyIntTree => assertTrue(false)
      case NodeInt(e,l,r) => { 
        assertEquals(4, size(l))
        assertEquals(5, size(r))
      }
    }
  }
}