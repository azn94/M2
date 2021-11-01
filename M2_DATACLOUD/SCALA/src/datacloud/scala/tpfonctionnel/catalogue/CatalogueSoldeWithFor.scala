package datacloud.scala.tpfonctionnel.catalogue

import datacloud.scala.tpobject.catalogue.CatalogueWithNonMutable

class CatalogueSoldeWithFor extends CatalogueWithNonMutable with CatalogueSolde{

    def solde(pourcentage : Int) : Unit = {
      for( (k,v) <- map ){
        //removeProduct(k);
        storeProduct(k, v * pourcentage / 100.0);
      }
    }
}