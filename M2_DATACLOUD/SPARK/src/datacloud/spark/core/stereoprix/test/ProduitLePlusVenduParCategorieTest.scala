package datacloud.spark.core.stereoprix.test

import java.io.FileInputStream
import org.junit.Test
import java.io.File
import java.util.Scanner
import org.junit.Assert._
import datacloud.spark.core.stereoprix.Stats._
import scala.collection.mutable.{Map=> MMap}
import datacloud.spark.core.test.AbstractTest

class ProduitLePlusVenduParCategorieTest extends StereoprixTest {
  
  type Categorie = String
  type Produit = String
  
  @Test
  def test1={
    
    StereoprixTest.listconfig.foreach(c=>{
      val file_in=c._2
      val file_out:File=new File(AbstractTest.tmpdirectorypath.toFile(),"out")
      if(file_out.exists) AbstractTest.deleterec(file_out)
      val name_file_out=file_out.getAbsolutePath
      produitLePlusVenduParCategorie(file_in.toURI().toASCIIString(),file_out.toURI().toASCIIString())
      val counters = MMap[Categorie,MMap[Produit,Int]]()     
      file_in.listFiles().foreach(f => {
          val scanner = new Scanner(new FileInputStream(f))
          while(scanner.hasNextLine()){
              val line = scanner.nextLine();
              val words = line.split(" ")
              val categorie:Categorie = words(4)
              val produit:Produit=words(3)
              if(!counters.contains(categorie)) counters.put(categorie, MMap[Produit,Int]())
              val prod_count = counters.get(categorie).get
              if(!prod_count.contains(produit))  prod_count.put(produit, 0)
              prod_count.put(produit, prod_count.get(produit).get+1)
              
          }
          scanner.close()
      })
      val expected = MMap[Categorie,Produit]()
      for((cat,map)<-counters){
        var prod_max=""
        for((prod,count)<- map){
          if (prod_max=="") prod_max=prod
          if(map.get(prod_max).get<count) prod_max=prod
        }
        expected.put(cat, prod_max)
      }
           
      file_out.listFiles((dir,name)=> name.matches("part-\\d{5}")).foreach(f=>{
        val scanner = new Scanner(new FileInputStream(f))
        while(scanner.hasNextLine()){
           val line = scanner.nextLine();
           val tmp = line.split(":")
           val computed_cat=tmp(0)
           val computed_prod=tmp(1)
           assertEquals(expected.get(computed_cat).get, computed_prod)
        }
        scanner.close()
      })
            
      AbstractTest.deleterec(file_out)
      
      
      
    })   
  }  
}