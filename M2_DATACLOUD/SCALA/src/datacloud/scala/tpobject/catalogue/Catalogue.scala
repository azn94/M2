package datacloud.scala.tpobject.catalogue

trait Catalogue {
    def getPrice (nomProduit: String): Double
    def removeProduct (nomProduit: String): Unit //Void de Scala - Any = Object en Scala
    def selectProducts(prixMin:Double, prixMax: Double): Iterable[String]
    def storeProduct(nomProduit: String, prix : Double) : Unit
}