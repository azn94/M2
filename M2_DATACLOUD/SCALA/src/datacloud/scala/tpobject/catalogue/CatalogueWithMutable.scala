package datacloud.scala.tpobject.catalogue

import scala.collection.mutable.Map

class CatalogueWithMutable extends Catalogue {
   var mutableMap: Map[String, Double] = Map[String, Double]() // var = mutable variable
   
    def getPrice (nomProduit: String): Double = {
      mutableMap.getOrElse(nomProduit, -1.0)
   }
    
    def removeProduct (nomProduit: String): Unit = {
      mutableMap -= nomProduit /* <=> mutableMap.remove(nomProduit) */
    }
    
    def selectProducts(prixMin:Double, prixMax: Double): Iterable[String] = {
       var res = List[String]()
       for ((nomProduit,prix) <- mutableMap) {
         if (prix >= prixMin && prix <= prixMax) {
           res = res :+ nomProduit
         }
       }
       return res
    }
   
    def storeProduct(nomProduit: String, prix : Double) : Unit = {
      mutableMap +=(nomProduit -> prix)
    }
    
}