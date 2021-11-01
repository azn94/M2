package datacloud.spark.core.lastfm.test

import java.io.File
import java.io.FileInputStream
import java.io.PrintWriter
import java.util.Scanner

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.{ Map=>MMap }
import scala.collection.mutable.{ Set=>MSet }

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.junit.Assert._
import org.junit.BeforeClass
import org.junit.Test

import datacloud.spark.core.lastfm.HitParade.TrackId
import datacloud.spark.core.lastfm.HitParade.UserId
import datacloud.spark.core.lastfm.HitParade.hitparade
import datacloud.spark.core.lastfm.HitParade.loadAndMergeDuplicates
import datacloud.spark.core.test.AbstractTest
import datacloud.spark.core.test.AbstractTest.tmpdirectorypath
import datagenerator.DataGenerator
import scala.collection.mutable.HashSet

class HitParadeTest extends AbstractTest {
  
  
  @Test
  def test={
    HitParadeTest.listconfig.foreach(c=>{
      val file_in=c._2     
      val conf = new SparkConf().setAppName("LastFM").setMaster("local[*]")
      val sc= new SparkContext(conf)
      
      val rdd0:RDD[((UserId,TrackId),(Int,Int,Int))] = loadAndMergeDuplicates(sc, file_in.toURI().toASCIIString())
      

      val hit:RDD[TrackId] = hitparade(rdd0)
      
      val expectedListenerByTrack = MMap[TrackId,MSet[UserId]]()
      val expectedNbListeningSkipByTrack = MMap[TrackId,Int]()
      
      file_in.listFiles().foreach(f => {
          val scanner = new Scanner(new FileInputStream(f))
          while(scanner.hasNextLine()){
              val line = scanner.nextLine();
              val words=line.split(" ")
              val user=UserId(words(0))
              val track=TrackId(words(1))
              val local=words(2).toInt
              val radio=words(3).toInt
              val skip=words(4).toInt
              expectedNbListeningSkipByTrack.get(track) match{
                case None => expectedNbListeningSkipByTrack.put(track, local+radio-skip)
                case Some(n) => expectedNbListeningSkipByTrack.put(track, n+local+radio-skip)
              }
              if(local+radio > 0){
                expectedListenerByTrack.get(track)match{
                  case None => expectedListenerByTrack.put(track, MSet[UserId](user))
                  case Some(set)=> set.add(user)
                }
              }
          }
          scanner.close()
      })
           
      val expected = expectedListenerByTrack.keySet.toSeq.sortWith((tid1,tid2)=>{
          val nb_listener1 = expectedListenerByTrack.get(tid1).get.size
          val nb_listener2 = expectedListenerByTrack.get(tid2).get.size
          
          val nblistskip1=expectedNbListeningSkipByTrack.get(tid1).get
          val nblistskip2=expectedNbListeningSkipByTrack.get(tid2).get
          
          if( nb_listener1 != nb_listener2 )
            nb_listener1.compareTo(nb_listener2) > 0
          else if(nblistskip1!=nblistskip2)
            nblistskip1.compareTo(nblistskip2) > 0
          else
            tid1.id.compareTo(tid2.id) < 0
      })
      val  res = hit.collect()      
      assertEquals(expected.size,res.size)
      for(i<- 0.to(res.size-1)){
        assertEquals(expected(i),res(i))
      }
      sc.stop()      
    })   
  }
  
  
  
}

object HitParadeTest{
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
  
  val listconfig=new ArrayBuffer[(File,File)]
   
  @BeforeClass
  def beforeclass:Unit={
   println("Temp working path = "+tmpdirectorypath.toString())
   listconfig+=generateConfig()
   listconfig+=generateConfig(sizeFichier=500,nbfichier=3)  
   listconfig.foreach(c=> new DataGenerator(c._1.toString()).process)
   
  }
}