package datacloud.spark.streaming.twit

import org.apache.log4j._
import org.apache.spark._ 
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds

object TopTwitAtTime2 extends App {
  Logger.getLogger("org.apache.spark").setLevel(Level.OFF)
  val conf = new SparkConf().setAppName("Twit2").setMaster("local[*]")
  val ssc = new StreamingContext(new SparkContext(conf), Seconds(1))
  val ds0 = ssc.socketTextStream("localhost", 4242)
  
  // lieu ou on va stocker les données
  ssc.checkpoint("/tmp/datacloud/spark/streaming/twitq2")
  
  // Affichage toute les x secondes
  val x = 1
  
  // Prendre en compte les y dernières secondes
  val y = 10
  
  // 0 : #twit9
  // On le coupe selon l'espace
  val ds1 = ds0.flatMap(x => x.split(" "))
  
  // On garde que les twits avec les #
  val ds2 = ds1.filter(_.contains("#"))
  
  // On ajoute un compteur
  val ds3 = ds2.map((_,1))
  
  // On somme les compteurs toutes les x secondes sur un intervalle de y secondes
  val ds4 = ds3.reduceByKeyAndWindow(_+_, _-_, Seconds(y), Seconds(x))
  
  // On classe les twits selon la valeur en descending  
  val ds5 = ds4.transform( rdd => rdd.sortBy(_._2, ascending=false))
  
  ds5.print()
  ssc.start()
  ssc.awaitTermination()
}