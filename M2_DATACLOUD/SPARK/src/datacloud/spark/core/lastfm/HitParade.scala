package datacloud.spark.core.lastfm

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

object HitParade {
  
  case class TrackId(id : String) {}
  
  case class UserId(id : String){}
  
  def loadAndMergeDuplicates[T](spark : SparkContext, urlEntre : String): RDD[((UserId,TrackId),(Int,Int,Int))] = {
    // UserID TrackID LocalListening RadioListening Skip
    
    val textFile = spark.textFile(urlEntre);
    
    // On sépare chaque ligne en Array de mots
    val rdd1 = textFile.map(_.split(" "));
    
    val rdd2 = rdd1.map(array => ((UserId(array(0)),TrackId(array(1))), (array(2).toInt, array(3).toInt, array(4).toInt)));
    
    val rdd3 = rdd2.reduceByKey( (val1,val2) => (val1._1 + val2._1, val1._2 + val2._2, val1._3 + val2._3));
    
    return rdd3;   
  }
  
  
  def hitparade(rdd0 : RDD[((UserId,TrackId),(Int,Int,Int))]): RDD[TrackId] ={
    
    // RDD[(TrackId,(1, Local, Radio, Skip))]
    val rdd1 = rdd0.map(tuple => (tuple._1._2, (1,tuple._2._1,tuple._2._2,tuple._2._3)));
    
    // RDD[(TrackId,(#nb_listener, Local, Radio, Skip))]
    val rdd2 = rdd1.reduceByKey((tuple1, tuple2) => (tuple1._1+tuple2._1, tuple1._2+tuple2._2, tuple1._3+tuple2._3, tuple1._4+tuple2._4));

    // RDD[(TrackId,(#nb_listener, Local + Radio - Skip))]
    val rdd3 = rdd2.map(tuple => (tuple._1,(tuple._2._1, tuple._2._2 + tuple._2._3 - tuple._2._4)));
    
    val rdd4 = rdd3.sortBy((tuple =>(-tuple._2._1, -tuple._2._2,tuple._1.id)), true);

    return rdd4.map(tuple => tuple._1);
  }
}