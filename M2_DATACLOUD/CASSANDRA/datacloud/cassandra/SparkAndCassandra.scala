package datacloud.cassandra

import com.datastax.spark.connector._
import org.apache.spark._
import org.apache.spark.sql._
import com.datastax.spark.connector.cql._
import org.apache.spark.rdd.RDD

object SparkAndCassandra extends App {
  
  org.apache.log4j.Logger.getLogger("org.apache.spark").setLevel(org.apache.log4j.Level.OFF);
  
  val conf = new SparkConf()
             .setAppName("Spark on Cassandra").setMaster("local[*]")
             .set("spark.cassandra.connection.host", "localhost")
    
  val sc = new SparkContext(conf);
  
  case class Mecanicien(idmecano: Int, nom: String, prenom: String, status:String);

  /*	Méthode 1*/
  val rdd_mecanicien : RDD[Mecanicien] = sc.cassandraTable[Mecanicien]("garage","mecanicien");
  rdd_mecanicien.saveAsCassandraTable("garage","mecanicien_cpy");
  
  /*	Méthode 2*/
  /*
  val rdd_mecanicien : RDD[Mecanicien] = sc.cassandraTable[Mecanicien]("garage","mecanicien");
  val tabledef = TableDef.fromType[Mecanicien]("garage", "mecanicien_cpy");
  table_mecanicien.saveAsCassandraTableEx(tabledef,AllColumns);
  */
  
  /*	Méthode 3*/ 
  /*
  val rdd_mecanicien : RDD[Mecanicien] = sc.cassandraTable[Mecanicien]("garage","mecanicien");
  val iterable = rdd_mecanicien.collect().toSeq;
  val rdd_mecanicien_cpy = sc.parallelize(iterable);
  val table = TableDef.fromType[Mecanicien]("garage", "mecanicien_cpy");
  rdd_mecanicien_cpy.saveAsCassandraTableEx(table,AllColumns);
  */

  sc.stop();
}