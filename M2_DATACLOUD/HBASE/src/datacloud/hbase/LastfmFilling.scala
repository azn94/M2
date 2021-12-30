package datacloud.hbase

import java.io.File
import org.apache.hadoop.hbase._
import scala.io.Source

import org.apache.hadoop.conf._
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util._

object LastfmFilling {
  
  def fromFile(data:File, dest:TableName, col_fam:String):Unit ={
      
    // Config connection
    val conf : Configuration = HBaseConfiguration.create();
    conf.set("hbase.zookeeper.quorum" , "localhost");
    val connection : Connection = ConnectionFactory.createConnection(conf);
    val table : Table =  connection.getTable(dest);
    
    // Variables
    val compteurs : String = col_fam;
    val userid : String = "userid";
    val trackid : String = "trackid";
    val locallistening : String = "locallistening";
    val radiolistening : String = "radiolistening";
    val skip : String = "skip";
        
        
    for(line <- Source.fromFile(data).getLines){
      this.synchronized{
        // user0 track8 16 3 3
        val words : Array[String] = line.split(" ");
        val rowkey : Array[Byte] = Bytes.toBytes(words(0) + words(1));
        val put : Put = new Put(rowkey);
        val get : Get = new Get(rowkey);
        val row : Result = table.get(get);
        
        val LocalListening : Long = words(2).toLong;
        val RadioListening : Long = words(3).toLong;
        val Skip : Long = words(4).toLong;
      
        if(row.isEmpty()){
          put.addColumn(Bytes.toBytes(compteurs), Bytes.toBytes(userid), Bytes.toBytes(words(0)));
          put.addColumn(Bytes.toBytes(compteurs), Bytes.toBytes(trackid), Bytes.toBytes(words(1)));
          put.addColumn(Bytes.toBytes(compteurs), Bytes.toBytes(locallistening), Bytes.toBytes(LocalListening));
          put.addColumn(Bytes.toBytes(compteurs), Bytes.toBytes(radiolistening), Bytes.toBytes(RadioListening));
          put.addColumn(Bytes.toBytes(compteurs), Bytes.toBytes(skip), Bytes.toBytes(Skip));
          table.put(put);
        }else{ // On peut soit le faire avec incrémente, soit le faire à la "main"
          
          val inc : Increment = new Increment(rowkey);
          inc.addColumn(Bytes.toBytes(compteurs),Bytes.toBytes(locallistening), LocalListening);
          inc.addColumn(Bytes.toBytes(compteurs),Bytes.toBytes(radiolistening), RadioListening);
          inc.addColumn(Bytes.toBytes(compteurs),Bytes.toBytes(skip), Skip);
          table.increment(inc);
          
          /*        
          var old_local: Long = 0;
          var old_radio: Long = 0;
          var old_skip: Long = 0;

          old_local = Bytes.toLong(row.getValue(Bytes.toBytes(compteurs), Bytes.toBytes(locallistening)));
          old_radio = Bytes.toLong(row.getValue(Bytes.toBytes(compteurs), Bytes.toBytes(radiolistening)));
          old_skip = Bytes.toLong(row.getValue(Bytes.toBytes(compteurs), Bytes.toBytes(skip)));

          put.addColumn( Bytes.toBytes(compteurs), Bytes.toBytes("locallistening"), Bytes.toBytes(LocalListening + old_local));
          put.addColumn( Bytes.toBytes(compteurs), Bytes.toBytes("radiolistening"), Bytes.toBytes(RadioListening + old_radio));
          put.addColumn( Bytes.toBytes(compteurs), Bytes.toBytes("skip"), Bytes.toBytes(Skip + old_skip));
          table.put(put);
          */
        }
      }
        }
  }
}