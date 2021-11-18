package datacloud.spark.core.stereoprix.test

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.Assert._
import java.io.PrintWriter
import scala.collection.mutable.ArrayBuffer
import datagenerator.textfile.TextFileGenerator
import datagenerator.DataGenerator
import datacloud.spark.core.stereoprix.Stats._
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileInputStream
import java.util.Scanner

class ChiffreAffaireTest extends StereoprixTest {
      
  @Test
  def test1={
    
    StereoprixTest.listconfig.foreach(c=>{
      val file_in=c._2
      val annee=2019
      val res = chiffreAffaire(file_in.toURI().toASCIIString(), 2019)
      
      var expected=0
      file_in.listFiles().foreach(f => {
          val scanner = new Scanner(new FileInputStream(f))
          while(scanner.hasNextLine()){
              val line = scanner.nextLine();
              val words = line.split(" ")
              val a = words(0).split("_")(2).toInt
              if(a==annee) expected+=words(2).toInt
          }
          scanner.close()
      })
      assertEquals(expected,res)
      
    })   
  }  
}




