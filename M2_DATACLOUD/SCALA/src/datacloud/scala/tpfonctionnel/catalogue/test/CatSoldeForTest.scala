package datacloud.scala.tpfonctionnel.catalogue.test


import datacloud.scala.tpfonctionnel.catalogue.CatalogueSoldeWithFor
import org.junit.Test
import org.junit.Assert._

class CatSoldeForTest extends AbstractCatSoldeTest {
  
  @Test
  def f()= super.testCatalogue(new CatalogueSoldeWithFor)
  
}