package datacloud.scala.tpfonctionnel

object Premiers {
  def premiers(n : Int) : List[Int] = {
    
     var liste : List[Int] = List.range(2, n);
     for(cpt <- 2 to n){
       liste = liste.filter((a: Int) => a% cpt != 0 ||(a == cpt));
     }
     
     return liste;
  }
  
  def premiersWithRec(n : Int) : List[Int] = {
    
    var liste : List[Int] = List.range(2, n);
    
    def f(liste : List[Int]) : List[Int] = {
      val tete : Int = liste(0);
      if(tete*tete > liste(liste.size-1)){
        return liste;
      }else{
        return List(liste.head) ++ f(liste.filter((elem : Int) => elem % tete != 0))
      }
    }
    
    return f(liste);
  }
}