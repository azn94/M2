package datacloud.hbase

import scala.collection.immutable.Map
import org.apache.hadoop.hbase.util._

case class Etudiant(val nom : String, val prenom : String, val age : Int, val notes : Map[String,Int])

object Etudiant{
  def toHbaseObject(e: Etudiant) : Map[(String,String),Array[Byte]] = {
    /*
     * - info qui possédera 3 colonnes (le nom, le prénom et l’âge) et qui stockera la valeur des
		 * attributs dans les cases correspondantes.
		 * 
		 * - notes qui possédera une colonne par matière présente dans la map des notes de l’étudiant et
		 * qui stockera la note dans la cellule correspondante
		 */
    
    var map : Map[(String,String),Array[Byte]] = Map[(String,String),Array[Byte]]();
    
    val info : String = "info";
    val note : String = "note";
    val nom : String = "nom";
    val prenom : String = "prenom";
    val age : String = "age";
    
    /* INFO */
    map += ((info,nom) -> Bytes.toBytes(e.nom));
    map += ((info,prenom) -> Bytes.toBytes(e.prenom));
    map += ((info,age) -> Bytes.toBytes(e.age));
    
    /* NOTES */
    for(n <- e.notes){
      map += ((note,n._1) -> Bytes.toBytes(n._2));
    }
    
    return map;
  }
  
  def HbaseObjectToEtudiant(d:Map[(String,String),Array[Byte]]) : Etudiant={
    
    val info : String = "info";
    val note : String = "note";
    val nom : String = "nom";
    val prenom : String = "prenom";
    val age : String = "age";
    
    var nomEtu : String = "";
    var prenomEtu : String = "";
    var ageEtu : Int = -1;
    
    var notes : Map[String,Int] = Map[String,Int]();
    
    for(value <- d){
      if(value._1._1.equals(info)){
        if(value._1._2.equals(nom)){
          nomEtu = Bytes.toString(value._2);
        }
        
        if(value._1._2.equals(prenom)){
          prenomEtu = Bytes.toString(value._2);
        }
        
        if(value._1._2.equals(age)){
          ageEtu = Bytes.toInt(value._2);
        }
      }
      
      if(value._1._1.equals(note)){
        notes = notes + (value._1._2 -> Bytes.toInt(value._2));
      }
    }
    
    return Etudiant(nomEtu,prenomEtu,ageEtu,notes);
  }
}