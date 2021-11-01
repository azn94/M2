package datacloud.scala.tpobject.catalogue

import scala.collection.immutable.Map
import scala.collection.immutable.HashMap

class CatalogueWithNonMutable extends Catalogue {
  var map : Map[String, Double] = Map[String, Double]()
  
   override def getPrice (nomProduit: String): Double = 
     if (map.contains(nomProduit)) map(nomProduit) else -1.0
   
    
    def removeProduct (nomProduit: String): Unit = {
      map -=(nomProduit) 
    }
    
    def selectProducts(prixMin:Double, prixMax: Double): Iterable[String] = {
       var res = List[String]()
       for ((nomProduit,prix) <- map) {
         if (prix >= prixMin && prix <= prixMax) {
           res = res :+ nomProduit
         }
       }
       return res
    }
   
    def storeProduct(nomProduit: String, prix : Double) : Unit = {
      map = map + (nomProduit -> prix)
    }
    
}