package datacloud.scala.tpfonctionnel.catalogue.test

import datacloud.scala.tpobject.catalogue.test.AbstractTest
import datacloud.scala.tpfonctionnel.catalogue.CatalogueSolde
import org.junit.Assert._

abstract class AbstractCatSoldeTest extends AbstractTest {
  
  def testCatalogue(cat:CatalogueSolde)={
    super.testCatalogue(cat)
    
    val tmp = {
      for(prod <- cat.selectProducts(0.0, Double.MaxValue))
        yield (prod,cat.getPrice(prod))
    }
    val save=tmp.toMap
    cat.solde(50)
    cat.selectProducts(0.0, Double.MaxValue)
        .foreach(p=>{
          save.get(p) match{
            case None => assertFalse(true)
            case Some(price) => assertEquals(price/2,cat.getPrice(p),0.0)
          }
        })
  }
}