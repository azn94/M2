package datacloud.scala.tpfonctionnel.catalogue.test

import datacloud.scala.tpfonctionnel.catalogue.CatalogueSoldeWithNamedFunction
import org.junit.Test

class CatSoldeNamedFTest extends AbstractCatSoldeTest {
  @Test
  def f()= super.testCatalogue(new CatalogueSoldeWithNamedFunction)
}