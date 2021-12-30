package datagenerator.hbase.column

import scala.util.Random
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.Connection
import java.util.ArrayList
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.TableName
import scala.collection.JavaConverters._




class ForeignKey(namespace:String, 
             nameTable:String,
             colfam:String,
             name:String,
             private[this] val nameforeigntable:String,
             private[this] val connection : Connection) extends Column(namespace,nameTable,colfam,name) {
  
   def getRandomValue(r:Random):Array[Byte] = {
     val sc = new Scan
     val t = connection.getTable(TableName.valueOf(namespace+":"+nameforeigntable))
     val results = t.getScanner(sc)
     val ids = new ArrayList[String]()
     for(res <- results.asScala){
       ids.add(Bytes.toString(res.getRow))
     }
     val tmp = r.nextInt(ids.size)
     return Bytes.toBytes(ids.get(tmp))
   }
}