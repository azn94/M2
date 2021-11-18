package datacloud.spark.core.test

import java.io.File
import java.nio.file.Files

import org.junit.AfterClass

abstract class AbstractTest {
  
}

object AbstractTest{
   val tmpdirectorypath = Files.createTempDirectory("datacloud_spark_test_dir")
   
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
}