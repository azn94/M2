package datacloud.scala.tpfonctionnel.catalogue.test

import datacloud.scala.tpfonctionnel.catalogue.CatalogueSoldeWithAnoFunction
import org.junit.Test

class CatSoldeAnoFTest extends AbstractCatSoldeTest {
  
  @Test
  def f()= super.testCatalogue(new CatalogueSoldeWithAnoFunction)
}