package datacloud.cassandra

import com.datastax.spark.connector._
import org.apache.spark._
import org.apache.spark.sql._
import com.datastax.spark.connector.cql._
  
object Merge extends App {
  org.apache.log4j.Logger.getLogger("org.apache.spark").setLevel(org.apache.log4j.Level.OFF);
  
  val conf = new SparkConf()
             .setAppName("Spark on Cassandra").setMaster("local[*]")
             .set("spark.cassandra.connection.host", "localhost")
    
  val sc = new SparkContext(conf);

  case class Reparation(
    idvehicule: Int,
    marque: String,
    modele: String,
    kilometrage: Int,
    idmecano: Int,
    nom: String,
    prenom: String,
    status: String
  )
 
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

  val table_reparation = TableDef.fromType[Reparation]("garage", "reparation")

  val rdd_mecanicien = sc.cassandraTable[Mecanicien]("garage", "mecanicien")
  val rdd_vehicule = sc.cassandraTable[Vehicule]("garage", "vehicule")

  val rdd_mecanicien_key = rdd_mecanicien.map(m => (m.idmecano, m))
  val rdd_vehicule_key = rdd_vehicule.map(v => (v.mecano, v))

  val rdd_jointure = rdd_mecanicien_key.join(rdd_vehicule_key).map(_._2)

  val rdd_reparation = rdd_jointure.map(tuple =>
                                            new Reparation(
                                              tuple._2.idvehicule,
                                              tuple._2.marque,
                                              tuple._2.modele,
                                              tuple._2.kilometrage,
                                              tuple._2.mecano,
                                              tuple._1.nom,
                                              tuple._1.prenom,
                                              tuple._1.status
                                            )
  )

  rdd_reparation.saveAsCassandraTableEx(table_reparation, AllColumns)
  sc.stop()
}