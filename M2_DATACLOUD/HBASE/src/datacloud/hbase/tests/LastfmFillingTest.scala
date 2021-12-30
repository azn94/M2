package datacloud.hbase.tests

import java.io.File
import java.io.PrintWriter

import scala.collection.mutable.HashMap
import scala.io.Source

import org.apache.hadoop.hbase.TableName
import org.junit.Assert._
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import scala.collection.JavaConverters._
import org.junit.Test

import datacloud.hbase.HbaseClient
import datacloud.hbase.tests.HbaseTest._
import datagenerator.DataGenerator
import datacloud.hbase.LastfmFilling
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.util.Bytes

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class LastfmFillingTest extends HbaseTest {
  
  val namespace = "lastfm"
  val table = "stat"
  val cf="compteurs"
  val tn=TableName.valueOf(namespace, table)
  
   @Test
  def testa={// size fichier 200 et nbfichier3
    println("TEST A")
    testFill(generateConfig(sizeFichier=200, nbfichier=3, nbmaxuser=10, nbmaxtrack=6))  
    println("FIN TEST A")
  }
    
  @Test
  def testb={
    println("TEST B")
    testFill(generateConfig(sizeFichier=500, nbfichier=5, nbmaxuser=10, nbmaxtrack=6),false)  
    println("FIN TEST B")
  }
  
  
  def testFill(files:(File,File), sequential:Boolean=true)={
    val client = new HbaseClient(connection)
    client.deleteTable(tn)
    client.createTable(tn,cf)
    new DataGenerator(files._1.toString()).process
    val dir_data = files._2
    
    if(sequential)
       dir_data.listFiles().foreach(file =>  LastfmFilling.fromFile(file,tn,cf))
    else{
      val threads = dir_data.listFiles().map(file => new Thread(new Runnable(){
        override def run = LastfmFilling.fromFile(file,tn,cf)
      }))
      threads.foreach(_.start())
      threads.foreach(_.join())
    }
    
    val expected = getExpected(dir_data)
    val table=connection.getTable(tn)
    val scanner = table.getScanner(new Scan).asScala
    var cpt=0
    scanner.foreach(result => {
      val key = (Bytes.toString(result.getValue(cf.getBytes,"userid".getBytes)),
                 Bytes.toString(result.getValue(cf.getBytes,"trackid".getBytes))
                )
      val val_expected= expected.get(key).get
      assertNotNull(val_expected)
      val val_computed = (Bytes.toLong(result.getValue(cf.getBytes,"locallistening".getBytes)),
                          Bytes.toLong(result.getValue(cf.getBytes,"radiolistening".getBytes)),
                          Bytes.toLong(result.getValue(cf.getBytes,"skip".getBytes))
                         )
      assertEquals("clé="+key,val_expected,val_computed)
      cpt+=1
    })
    assertEquals(expected.size, cpt)
  }
  
  
 
  
  def getExpected(dir_data:File):Map[(String,String),(Long,Long,Long)]={
    assertTrue(dir_data.isDirectory())
    val res = new HashMap[(String,String),(Long,Long,Long)]()   
    dir_data.listFiles().foreach(fdata=>{
      Source.fromFile(fdata).getLines().foreach( line=> {
          val words = line.split(" ")
          val key=(words(0),words(1))
          res.get(key) match {
            case None => res.put(key, (words(2).toLong,words(3).toLong,words(4).toLong))
            case Some((x,y,z)) => res.put(key, (words(2).toLong+x,words(3).toLong+y,words(4).toLong+z))
          }

      })
    })
    return res.toMap
  }
  
  
  def generateConfig(sizeFichier:Int=1,
                     nbfichier:Int=1,
                     nbmaxuser:Int=50,
                     nbmaxtrack:Int=100):(File,File)={
     

    val basename:String="sizeFichier"+sizeFichier+"_nbfichier"+nbfichier+"_nbmaxuser"+nbmaxuser+"_nbmaxtrack"+nbmaxtrack
    val file_config = new File(tmpdirectorypath.toString()+"/"+basename+".config")
    val file_data = new File(tmpdirectorypath.toString()+"/"+basename+".data")
    
    if(file_config.exists()) file_config.delete()
    if(file_data.exists()) file_data.delete()
    
    
    val w = new PrintWriter(file_config)
    w.println("type=textfile")
    w.println("text.sizefichier="+sizeFichier)
    w.println("text.nbfichier="+nbfichier)
    w.println("text.outdir="+file_data.toString())
    
    w.println("text.name=lastfm")
    w.println("text.champs=user,track,locallistening,radiolistening,skip")
    w.println("text.user.type=chaine")
    w.println("text.user.nbmax="+nbmaxuser)
    w.println("text.track.type=chaine")
    w.println("text.track.nbmax="+nbmaxtrack)
    w.println("text.locallistening.type=entier")
    w.println("text.locallistening.valmax=20") 
    w.println("text.radiolistening.type=entier")
    w.println("text.radiolistening.valmax=15")
    w.println("text.skip.type=entier")
    w.println("text.skip.valmax=10")   
    w.close
    return (file_config,file_data)
  }  
}

