package datacloud.scala.tpfonctionnel

object Counters {
  def nbLetters( liste : List[String]): Int={
    return liste.flatMap((phrase : String)=> phrase.split(" ")).map((mot : String) => mot.size).reduce(_+_)
  }
}