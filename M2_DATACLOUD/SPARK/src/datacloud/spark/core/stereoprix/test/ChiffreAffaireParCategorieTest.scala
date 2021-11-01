package datacloud.spark.core.stereoprix.test

import java.io.FileInputStream
import org.junit.Test
import java.io.File
import java.util.Scanner
import org.junit.Assert._
import datacloud.spark.core.stereoprix.Stats._
import datacloud.spark.core.test.AbstractTest

class ChiffreAffaireParCategorieTest extends StereoprixTest {
  
  @Test
  def test1={
    
    StereoprixTest.listconfig.foreach(c=>{
      val file_in=c._2
      
      val file_out:File=new File(AbstractTest.tmpdirectorypath.toFile(),"out")
      if(file_out.exists) AbstractTest.deleterec(file_out)
      chiffreAffaireParCategorie(file_in.toURI().toASCIIString(),file_out.toURI().toASCIIString())
      val expected = scala.collection.mutable.Map[String,Int]()
      file_in.listFiles().foreach(f => {
          val scanner = new Scanner(new FileInputStream(f))
          while(scanner.hasNextLine()){
              val line = scanner.nextLine();
              val words = line.split(" ")
              val prix = words(2).toInt
              val categorie = words(4)
              expected.put(categorie, expected.get(categorie) match {
                case None => prix
                case Some(v) => prix + v
              })
          }
          scanner.close()
      })
      
      file_out.listFiles((dir,name)=> name.matches("part-\\d{5}")).foreach(f=>{
        val scanner = new Scanner(new FileInputStream(f))
        while(scanner.hasNextLine()){
           val line = scanner.nextLine();
           val tmp = line.split(":")
           val computed_cat=tmp(0)
           val computed_prix=tmp(1).toInt
           assertEquals(expected.get(computed_cat).get, computed_prix)
        }
        scanner.close()
      })
            
      AbstractTest.deleterec(file_out)
      
      
      
    })   
  }  
  
}