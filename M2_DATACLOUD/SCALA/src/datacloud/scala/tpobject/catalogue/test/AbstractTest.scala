package datacloud.scala.tpobject.catalogue.test

import org.junit.Test
import org.junit.Assert._
import datacloud.scala.tpobject.catalogue.Catalogue

abstract class AbstractTest {

  
  def testCatalogue(cat:Catalogue)={
     cat.storeProduct("peluche", 10.5)
     cat.storeProduct("tondeuse", 250.6)
     cat.storeProduct("table", 90)
     cat.storeProduct("saladier", 20)
     cat.storeProduct("casserole", 35)
     cat.storeProduct("fourchette", 2)

     assertEquals(-1.0, cat.getPrice("baguette"),0.0)
     
     assertEquals(10.5, cat.getPrice("peluche"),0.0)
     assertEquals(250.6,cat.getPrice("tondeuse"),0.0)
     assertEquals(90,cat.getPrice("table"),0.0)
    
     assertEquals(20,cat.getPrice("saladier"),0.0)
     assertEquals(35,cat.getPrice("casserole"),0.0)
     assertEquals(2,cat.getPrice("fourchette"),0.0)
     
     val it=cat.selectProducts(5.0, 100.0)
     assertEquals(4,it.size)
     val l = it.toList
     assertTrue(l.contains("peluche"))
     assertTrue(l.contains("table"))
     assertTrue(l.contains("saladier"))
     assertTrue(l.contains("casserole"))
     
     
     cat.removeProduct("peluche")
     assertEquals(-1.0,cat.getPrice("peluche"),0.0)
  }
  
    
  
}