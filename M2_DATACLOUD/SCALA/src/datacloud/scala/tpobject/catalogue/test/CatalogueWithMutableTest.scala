package datacloud.scala.tpobject.catalogue.test

import datacloud.scala.tpobject.catalogue.CatalogueWithMutable;
import org.junit.Test
import org.junit.Assert._

class CatalogueWithMutableTest extends AbstractTest {
  
  @Test
  def test=testCatalogue(new CatalogueWithMutable)
  
}
