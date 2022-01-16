package datacloud.cassandra

import com.datastax.spark.connector._
import org.apache.spark._
import org.apache.spark.sql._
import com.datastax.spark.connector.cql._

import scala.collection.mutable.ListBuffer

object Modify extends App {
  
  org.apache.log4j.Logger.getLogger("org.apache.spark").setLevel(org.apache.log4j.Level.OFF);
  
  val conf = new SparkConf()
             .setAppName("Spark on Cassandra").setMaster("local[*]")
             .set("spark.cassandra.connection.host", "localhost")
    
  val sc = new SparkContext(conf);
  
  case class Mecanicien(
    idmecano: Int,
    nom: String,
    prenom: String,
    status: String
  )

  case class Vehicule(
    idvehicule: Int,
    marque: String,
    modele: String,
    kilometrage: Int,
    mecano: Int
  )
  
  case class MecanicienV(idmecano: Int, nom: String, prenom: String, status:String, vehicules: Seq[Int]);
  
    val rdd_table_meca3 = sc.cassandraTable[Mecanicien]("garage", "mecanicien")
                            .map(m => ((m.idmecano), (m.nom, m.prenom, m.status)))
    val rdd_table_vehicule2 = sc.cassandraTable[Vehicule]("garage", "vehicule")
                            .map(l => (l.mecano, l))

    rdd_table_meca3.join(rdd_table_vehicule2)
                    .map(l => (l._1, (l._2._1, l._2._2)))
                    .groupByKey()
                    .map(l => {
                        var vl :ListBuffer[Int] = ListBuffer[Int]()
                        for(elem <- l._2) 
                            vl += elem._2.idvehicule;
                        MecanicienV(l._1, l._2.head._1._1, l._2.head._1._2, l._2.head._1._3, Seq(vl: _*));
                    }).saveAsCassandraTable("garage", "mecanicien_vehicule")
  
  sc.stop();
}