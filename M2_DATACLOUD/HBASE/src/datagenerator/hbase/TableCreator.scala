package datagenerator.hbase

import scala.util.Random
import org.apache.hadoop.hbase.HTableDescriptor
import org.apache.hadoop.hbase.NamespaceDescriptor
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Connection
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
import java.util.ArrayList
import datagenerator.hbase.column.Column
import datagenerator.hbase.column.Id

class TableCreator(private[this] val connection:Connection) {
  
  private[this] val admin = connection.getAdmin
  

  
  def fillTable(namespace:String,nametable:String, size:Int, id:Column, columns:Seq[Column], random:Random):Unit={
    val puts = new ArrayList[Put]()
    for(i<- 1.to(size).by(1)){
      var val_id = id.getRandomValue(random)
      var put = new Put(val_id)
      //put.addColumn(Bytes.toBytes(id.colfam), Bytes.toBytes(id.name), val_id)
      for(col <- columns){
        val value = col match{
          case idtmp:Id if idtmp.name.equals(id.name) => val_id
          case _ =>col.getRandomValue(random)
        }         
//         System.err.println("add put key = "+val_id+"  "+col.colfam+":"+col.name+" = "+value)
        put.addColumn(Bytes.toBytes(col.colfam), Bytes.toBytes(col.name), value)
      }
      puts.add(put)
    }
    
    //puts.forEach(println)
    connection.getTable(TableName.valueOf(namespace+":"+nametable)).put(puts)
  }
   
  
  def createNamespaceIfNotexists(namespace:String):Boolean={
    var present= false
    for(desc <- admin.listNamespaceDescriptors()){
      if(desc.getName.equals(namespace)){
        return false
      }
    }
    admin.createNamespace(NamespaceDescriptor.create(namespace).build())
    
    return true
  }
  
  def createTableOrOverwrite(namespace :String, nametable : String, columnfams : Seq[String]):Boolean={
    var res = false
    val tabledesc = new HTableDescriptor(TableName.valueOf(namespace+":"+nametable))
    if(admin.tableExists(tabledesc.getTableName)){
      admin.disableTable(tabledesc.getTableName)
      admin.deleteTable(tabledesc.getTableName)
      res=true
    }
    
    for(col_fam <- columnfams){
      tabledesc.addFamily(new HColumnDescriptor(col_fam))
    }
    //System.err.println(tabledesc)
    admin.createTable(tabledesc)
    return res
  }  
}