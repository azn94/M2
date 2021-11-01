package datacloud.scala.tpfonctionnel.catalogue

import datacloud.scala.tpobject.catalogue.CatalogueWithNonMutable;

class CatalogueSoldeWithAnoFunction extends CatalogueWithNonMutable with CatalogueSolde{
  override def solde(pourcentage : Int) : Unit ={
    var diminution = (value : Double) => value *((100.0 - pourcentage) / 100.0);
    map = map.mapValues(diminution);
  }
}