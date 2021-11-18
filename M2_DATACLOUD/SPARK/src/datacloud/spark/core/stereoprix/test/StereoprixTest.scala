package datacloud.spark.core.stereoprix.test

import org.junit.BeforeClass
import java.io.PrintWriter
import org.junit.AfterClass
import scala.collection.mutable.ArrayBuffer
import java.nio.file.Files
import java.io.File
import datagenerator.DataGenerator
import datacloud.spark.core.test.AbstractTest
import datacloud.spark.core.test.AbstractTest._




abstract class StereoprixTest extends AbstractTest{
  
  
}

object StereoprixTest{
    
  
   
   def generateConfig(sizeFichier:Int=1,
                     nbfichier:Int=1,
                     prixmax:Int=500,
                     nbproduitmax:Int=100,
                     nbmaxcategorie:Int=10):(File,File)={
     

    val basename:String="sizeFichier"+sizeFichier+"_nbfichier"+nbfichier+"_prixmax"+prixmax+"_nbproduitmax"+nbproduitmax+"_nbmaxcategorie"+nbmaxcategorie
    val file_config = new File(tmpdirectorypath.toString()+"/"+basename+".config")
    val file_data = new File(tmpdirectorypath.toString()+"/"+basename+".data")
    
    if(file_config.exists()) file_config.delete()
    if(file_data.exists()) file_data.delete()
    
    
    val w = new PrintWriter(file_config)
    w.println("type=textfile")
    w.println("text.sizefichier="+sizeFichier)
    w.println("text.nbfichier="+nbfichier)
    w.println("text.outdir="+file_data.toString())
    
    w.println("text.name=stereoprix")
    w.println("text.champs=date,magasin,prix,produit,categorie")
    w.println("text.date.type=date")
    w.println("text.magasin.type=chaine")
    w.println("text.prix.type=entier")
    w.println("text.prix.valmax="+prixmax)
    w.println("text.produit.type=chaine")
    w.println("text.produit.nbmax="+nbproduitmax)
    w.println("text.categorie.type=chaine")
    w.println("text.categorie.nbmax="+nbmaxcategorie)
    w.println("text.categorie.dependance=produit")
    
    w.close
    return (file_config,file_data)
  }
  
  val listconfig=new ArrayBuffer[(File,File)]
   
  @BeforeClass
  def beforeclass:Unit={
   println("Temp working path = "+tmpdirectorypath.toString())
   listconfig+=generateConfig(nbproduitmax=2,nbmaxcategorie=5)
   listconfig+=generateConfig(sizeFichier=300,nbfichier=3, nbproduitmax=50,nbmaxcategorie=10)   
   listconfig.foreach(c=> new DataGenerator(c._1.toString()).process)
   
  }
  
  
  

}