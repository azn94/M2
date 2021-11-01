package datacloud.scala.tpfonctionnel

object Statistics {
  def average(liste : List[(Double,Int)]): Double = {
     
    var res = liste.map(t => (t._1*t._2,t._2)).reduce((a,b) => (a._1 + b._1, a._2 + b._2 ));
    return res._1/res._2;
  }
}