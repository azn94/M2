package datacloud.hbase.tests

import org.junit.BeforeClass
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer
import org.junit.AfterClass
import org.apache.hadoop.hbase.rest.client.Client
import org.apache.hadoop.hbase.rest.client.Cluster
import java.io.IOException
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Connection
import org.apache.hadoop.hbase.client.ConnectionFactory
import java.nio.file.Files
import java.io.File


abstract class HbaseTest {
  
}
object HbaseTest{
  
  val tmpdirectorypath = Files.createTempDirectory("datacloud_hbase_test_dir")
   
   def deleterec(f:File):Unit={
   f match {
     case file if !file.isDirectory() => {}
     case directory =>  directory.listFiles().foreach(deleterec(_))
    
   }
    println("deleting "+f.toString())
    f.delete()
   } 
    
   
   
  @AfterClass
  def afterclass:Unit={ 
   deleterec(new File(tmpdirectorypath.toString()))
  }
  
  
  
  var connection:Connection=null;
  
  @BeforeClass
  def deploy():Unit={
    Runtime.getRuntime.addShutdownHook( new Thread(){
      override def run={
         cleanup
      }
    })
    
    val conf = HBaseConfiguration.create() 
    conf.set("hbase.zookeeper.quorum", "localhost")
    connection = ConnectionFactory.createConnection(conf)
  }
  
  @AfterClass
  def cleanup={
    if(!connection.isClosed())
      connection.close()
  }
  
   
}