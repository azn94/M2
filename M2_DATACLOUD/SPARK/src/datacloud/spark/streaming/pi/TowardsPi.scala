package datacloud.spark.streaming.pi

import org.apache.log4j._
import org.apache.spark._ 
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds

object TowardsPi extends App{
  Logger.getLogger("org.apache.spark").setLevel(Level.OFF)
  val conf = new SparkConf().setAppName("PiAtTime").setMaster("local[*]")
  val ssc = new StreamingContext(new SparkContext(conf), Seconds(5))
  val ds0 = ssc.socketTextStream("localhost", 4242)
  val ds1 = ds0.map(x => x.split(" "))
  val ds2 = ds1.map(x => (x(0).toDouble,x(1).toDouble))
  // DStream[(Compteur de NbIn,Compteur de NbTotal)]
  val ds3 = ds2.map(x => if(x._1*x._1 + x._2*x._2 < 1) (1.0,1.0) else (0.0,1.0))
  val ds4 = ds3.reduce((x1,x2) => (x1._1+x2._1,x1._2+x2._2))
  
  // Besoin d'un flux (k,v) donc on crée une clé de valeur n'importe 0.0
  val ds5 = ds4.map(x => (0.0,4*x._1/x._2))
  
  val ds6 = ds5.updateStateByKey((vals, state : Option[Double]) => state match{
                    case None => if(vals.length == 0) Some(0.0) else Some(vals.reduce(_+_))
                    case Some(n) => if(vals.length == 0) Some(n) else Some(((n+vals.reduce(_+_))/2))  
                  }
                )
  
  
  
  
  
  ds6.print()  
  ssc.checkpoint("tmp")
  ssc.start()
  ssc.awaitTermination()
}