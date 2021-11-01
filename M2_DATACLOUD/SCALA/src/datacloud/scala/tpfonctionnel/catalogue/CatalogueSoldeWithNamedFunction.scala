package datacloud.scala.tpfonctionnel.catalogue

import datacloud.scala.tpobject.catalogue.CatalogueWithNonMutable;

class CatalogueSoldeWithNamedFunction extends CatalogueWithNonMutable with CatalogueSolde{
  
  override def solde(pourcentage : Int) : Unit ={
    var var_nomme = diminution( _:Double, pourcentage);
    map = map.mapValues(var_nomme);
  }
  
  def diminution(a : Double, percent: Int) : Double = a * ((100.0 - percent)/100.0)
}