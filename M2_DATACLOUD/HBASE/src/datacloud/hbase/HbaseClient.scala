package datacloud.hbase

import collection.JavaConverters._
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.NamespaceDescriptor
import scala.util.control.Breaks
import org.apache.hadoop.hbase.client._
import scala.collection.immutable.Map
import org.apache.hadoop.hbase.util._

class HbaseClient(val connection : Connection) {
  
  def createTable(tn:TableName, colfams:String*): Unit = {
    
    val admin : Admin = connection.getAdmin();
    
    // Si le namespace associé à cette table n’existe pas sur "connection", ce dernier est créé au préalable.
    if(!connection.getAdmin.listNamespaces().contains(tn.getNamespaceAsString)){
      admin.createNamespace(NamespaceDescriptor.create(tn.getNamespaceAsString).build());
    }
    
    // Si la table n'existe pas sur "connection", on la crée
    if(!admin.tableExists(tn)){
      var tdb : TableDescriptorBuilder = TableDescriptorBuilder.newBuilder(tn);
      for (cf <- colfams){
        tdb.setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(cf.getBytes).build());
      }
      val td : TableDescriptor = tdb.build();
      admin.createTable(td);
    }
  }
 
  def deleteTable(tn:TableName):Unit = {

    val admin : Admin = connection.getAdmin();
    val tn_namespace : String = tn.getNamespaceAsString;
    var deleted : Boolean = true;
    val wall : Breaks = new Breaks();
    
    // Si la table donnée existe, on la supprime
    if(admin.tableExists(tn)){
      admin.disableTable(tn);
      admin.deleteTable(tn);
    }
    
    // Si le namespace associé existe
    if(admin.listNamespaces().contains(tn_namespace)){
      
      wall.breakable{
        
        // Parcours la liste des tableNames
        for(tn_i <- admin.listTableNames()){
          
          // Si un autre tableName est sur le même namespace alors on ne peut pas supprimer le namespace
          if(tn_i.getNamespaceAsString.equals(tn_namespace)){
            deleted = false;
            wall.break();
          }
        }
      }
      if(deleted){
        admin.deleteNamespace(tn_namespace);
      }
    }
  }
  
  def writeObject[E](tn:TableName, rowkey: Array[Byte], obj : E)
                    (implicit conv : E => Map[(String,String),Array[Byte]]) : Unit ={
    
    val table : Table = connection.getTable(tn);
    val put : Put = new Put(rowkey);
    val mapEtu : Map[(String,String),Array[Byte]] = conv(obj); // On convertit l'étudiant sous la forme d'une Map
    
    // On parcours la map et on écrit dans la base toutes les informations contenu dans la map
    for(etu <- mapEtu) {
      put.addColumn(etu._1._1.getBytes, etu._1._2.getBytes, etu._2);
    }
    table.put(put);
  }
  
  def readObject[E](tn:TableName, rowkey: Array[Byte])
                   (implicit conv : Map[(String,String),Array[Byte]] => E) : Option[E] ={
    
    val table : Table = connection.getTable(tn);
    val get : Get = new Get(rowkey);
    val result : Result = table.get(get);
    
    // On convertit la NavigableMap en Map scala :  Map[colfam, Map[colqua, value]]]
    val map = result.getNoVersionMap.asScala.map(x => (x._1,x._2.asScala));
    var mapEtu : Map[(String,String),Array[Byte]] = Map[(String,String),Array[Byte]]();
    
    // On parcours la Map que l'on a récupéré de la Hbase
    for(res <- map){
      val colfam : String = Bytes.toString(res._1);
      for(res2 <- res._2){
        val colqua : String = Bytes.toString(res2._1);
        mapEtu = mapEtu + ((colfam,colqua) -> res2._2);
      }
    }
    return Some(conv(mapEtu));
  }
}

