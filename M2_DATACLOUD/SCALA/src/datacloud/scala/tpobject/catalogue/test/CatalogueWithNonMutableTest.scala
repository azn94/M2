package datacloud.scala.tpobject.catalogue.test

import org.junit.Test;

import datacloud.scala.tpobject.catalogue.CatalogueWithNonMutable;

class CatalogueWithNonMutableTest extends AbstractTest {
  
  @Test
  def test=testCatalogue(new CatalogueWithNonMutable)
}