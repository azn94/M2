package datacloud.hbase.tests

import java.io.File
import datacloud.hbase.tests.HbaseTest._
import java.io.PrintWriter
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import datagenerator.DataGenerator
import org.junit.Test
import datagenerator.DataGeneratorHbase
import org.junit.BeforeClass
import org.junit.Assert._
import scala.collection.mutable.HashMap
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Scan
import scala.collection.JavaConverters._
import org.apache.hadoop.hbase.util.Bytes
import datacloud.hbase.tests.StereoPrixTest._
import datacloud.hbase.Stereoprix


abstract class StereoPrixTest extends HbaseTest {
  
}
object StereoPrixTest {
  val cf="defaultcf"
  val namespace="stereoprix"
  case class Client(idclient:String,nom:String,prenom:String)
  case class Categorie(idcat:String,designation:String)
  case class Magasin(idmag:String,adresse:String)
  case class Produit(idprod:String,designation:String,prix:Int,categorie:Categorie)
  case class Vente(idvente:String,client:Client,magasin:Magasin,produit:Produit,date:String)
  
  val categories_map = new HashMap[String,Categorie] 
  val clients_map = new HashMap[String,Client]
  val magasins_map = new HashMap[String,Magasin]
  val produits_map = new HashMap[String,Produit]
  val ventes_map = new HashMap[String,Vente]
  
  @BeforeClass
  def setup={
            
    new DataGeneratorHbase(generateConfig().toString()).process
    
    categories_map.clear()
    val categories_table = connection.getTable(TableName.valueOf(namespace,"categorie"))
    for(r<-categories_table.getScanner(new Scan).asScala){
       val idcat = Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("idcat")))
       val designation = Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("designation")))
       categories_map.put(idcat, Categorie(idcat,designation))
    }
   
    clients_map.clear()
    val clients_table = connection.getTable(TableName.valueOf(namespace,"client"))
     for(r<-clients_table.getScanner(new Scan).asScala){
       val idclient = Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("idclient")))
       val nom = Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("nom")))
       val prenom = Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("prenom")))
       clients_map.put(idclient, Client(idclient,nom,prenom))
    }
    
    
    val magasins_table = connection.getTable(TableName.valueOf(namespace,"magasin"))
     for(r<-magasins_table.getScanner(new Scan).asScala){
       val idmag = Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("idmag")))
       val adresse = Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("adresse")))
       magasins_map.put(idmag, Magasin(idmag,adresse))
    }
    
   
   
    val produits_table = connection.getTable(TableName.valueOf(namespace,"produit"))
     for(r<-produits_table.getScanner(new Scan).asScala){
       val idprod = Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("idprod")))
       val designation = Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("designation")))
       val prix = Bytes.toInt(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("prix")))
       val cat = categories_map.get(Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("categorie")))).get
       produits_map.put(idprod, Produit(idprod,designation,prix,cat))
    }
    
    
    val ventes_table = connection.getTable(TableName.valueOf(namespace,"vente"))
     for(r<-ventes_table.getScanner(new Scan).asScala){
       val idvente = Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("idvente")))
       val client = clients_map.get(Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("client")))).get
       val magasin = magasins_map.get(Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("magasin")))).get
       val produit = produits_map.get(Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("produit")))).get
       val date = Bytes.toString(r.getValue(Bytes.toBytes(cf), Bytes.toBytes("date")))
       ventes_map.put(idvente, Vente(idvente,client,magasin,produit,date))
    }
    
       
  }
  
  def generateConfig(nbvente:Int=3000):File={
    val file_config = new File(tmpdirectorypath.toString()+"/stereoprix_schema.config")
    if(file_config.exists()) file_config.delete()
    val w = new PrintWriter(file_config)
    w.println("type=HbaseSchema")
    w.println("namespace=stereoprix")
    w.println("tables=client,categorie,magasin,produit,vente")
    
    //table client
    w.println("table.client.columns=idclient,nom,prenom")
    w.println("table.client.nom.type=chaine")
    w.println("table.client.prenom.type=chaine")
    w.println("table.client.idclient.type=id")
    w.println("table.client.colidname=idclient")
    w.println("table.client.maxsize=100")
    
    //table categorie
    w.println("table.categorie.columns=idcat,designation")

    w.println("table.categorie.designation.type=id")
    w.println("table.categorie.idcat.type=id")
    w.println("table.categorie.maxsize=30")
    w.println("table.categorie.colidname=idcat")

    //table magasin
    w.println("table.magasin.columns=idmag,adresse")

    w.println("table.magasin.idmag.type=id")
    w.println("table.magasin.adresse.type=chaine")
    w.println("table.magasin.maxsize=10")
    w.println("table.magasin.minsize=9")
    w.println("table.magasin.colidname=idmag")


    //table produit
    w.println("table.produit.columns=idprod,designation,prix,categorie")

    w.println("table.produit.idprod.type=id")
    w.println("table.produit.designation.type=chaine")
    w.println("table.produit.prix.type=entier")

    w.println("table.produit.colidname=idprod")
    w.println("table.produit.categorie.type=fk")
    w.println("table.produit.categorie.nametable=categorie")
    w.println("table.produit.maxsize=90")
    w.println("table.produit.minsize=89")

    //table vente
    w.println("table.vente.columns=idvente,client,magasin,produit,date")
    w.println("table.vente.idvente.type=id")
    w.println("table.vente.client.type=fk")
    w.println("table.vente.client.nametable=client")
    w.println("table.vente.magasin.type=fk")
    w.println("table.vente.magasin.nametable=magasin")
    w.println("table.vente.produit.type=fk")
    w.println("table.vente.produit.nametable=produit")
    w.println("table.vente.date.type=date")

    w.println("table.vente.maxsize="+(nbvente+1))
    w.println("table.vente.minsize="+nbvente)
    w.println("table.vente.colidname=idvente")
    
    w.close
    return file_config
  }
}
