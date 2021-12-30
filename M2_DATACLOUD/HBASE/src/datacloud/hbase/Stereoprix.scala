package datacloud.hbase

import org.apache.hadoop.conf._
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util._
import org.apache.hadoop.hbase._
import collection.JavaConverters._
import scala.collection.mutable._
import org.apache.hadoop.hbase.client._

object Stereoprix {
  
  def nbVenteParCategorie:Map[String,Int] = {
    
    // Config connection
    val conf : Configuration = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum" , "localhost");
		val connection : Connection = ConnectionFactory.createConnection(conf);

		// TableName
		val tn_categorie : TableName = TableName.valueOf("stereoprix", "categorie");
		//val tn_client : TableName = TableName.valueOf("client");
		//val tn_magasin : TableName = TableName.valueOf("magasin");
		val tn_produit : TableName = TableName.valueOf("stereoprix", "produit");
		val tn_vente : TableName = TableName.valueOf("stereoprix","vente");
		
		// Table
		val table_categorie : Table =  connection.getTable(tn_categorie);
		//val table_client : Table =  connection.getTable(tn_client);
		//val table_magasin : Table =  connection.getTable(tn_magasin);
		val table_produit : Table =  connection.getTable(tn_produit);
		val table_vente : Table =  connection.getTable(tn_vente);
		
		// Variables
    val map_vente : Map[String,Int] = new HashMap[String,Int];
    val map_prod : Map[String,Int] = new HashMap[String,Int];
    val map_cat : Map[String,Int] = new HashMap[String,Int];
    
    val cf : String = "defaultcf";
    val prod : String = "produit";
    
    val idprod : String = "idprod";
    val cat : String = "categorie";
    
    val idcat : String = "idcat";
    val des : String = "designation";
    
    // Vente
    val scan_vente : Scan = new Scan();
    scan_vente.addColumn(Bytes.toBytes(cf), Bytes.toBytes(prod));
    val result_vente : ResultScanner = table_vente.getScanner(scan_vente);
    for(res <- result_vente.asScala){ // Compte le nb de produit vendu
      val produit : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(prod)));
      if(!map_vente.contains(produit)){
        map_vente.put(produit, 1);
      }else{
        map_vente.put(produit, map_vente.apply(produit) + 1);
      }
    }
    
    // Produit
    val scan_produit : Scan = new Scan();
    scan_produit.addColumn(Bytes.toBytes(cf), Bytes.toBytes(idprod));
    scan_produit.addColumn(Bytes.toBytes(cf), Bytes.toBytes(cat));
    val result_produit : ResultScanner = table_produit.getScanner(scan_produit);
    
    for(res <- result_produit.asScala){ // Compte le produit par catégorie
      val id_produit : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(idprod)));
      val categorie : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(cat)));
      if(!map_prod.contains(categorie)){
        map_prod.put(categorie, map_vente(id_produit));
      }else{
        map_prod.put(categorie, map_prod.apply(categorie) + map_vente(id_produit));
      }
    }
    
    // Categorie
    val scan_categorie : Scan = new Scan();
    scan_categorie.addColumn(Bytes.toBytes(cf), Bytes.toBytes(idcat));
    scan_categorie.addColumn(Bytes.toBytes(cf), Bytes.toBytes(des));
    val result_categorie : ResultScanner = table_categorie.getScanner(scan_categorie);
    
    for(res <- result_categorie.asScala){ // Change la catégorie par sa désignation
      val id_categorie : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(idcat)));
      val designation : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(des)));
      if(!map_cat.contains(id_categorie)){
        map_cat.put(designation, map_prod(id_categorie));
      }
    }  
    
    return map_cat;
  }
  
  /*
   * Ajoute une colonne dans la table categorie qui maintient le nombre de ventes associées
   * */
  
  def denormalise = {
    
    // Config connection
    val conf : Configuration = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum" , "localhost");
		val connection : Connection = ConnectionFactory.createConnection(conf);

		// TableName
		val tn_categorie : TableName = TableName.valueOf("stereoprix", "categorie");
		val tn_produit : TableName = TableName.valueOf("stereoprix", "produit");
		val tn_vente : TableName = TableName.valueOf("stereoprix", "vente");
		
		// Table
		val table_categorie : Table =  connection.getTable(tn_categorie);
		val table_produit : Table =  connection.getTable(tn_produit);
		val table_vente : Table =  connection.getTable(tn_vente);
		
		// Variables
		val map_vente : Map[String, Int] = new HashMap[String, Int];
    val map_prod : Map[String, Int] = new HashMap[String, Int];
    
    val cf : String = "defaultcf";
    val prod : String = "produit";
    
    val idprod : String = "idprod";
    val cat : String = "categorie";
    
    val idcat : String = "idcat";
    
    // Vente
    val scan_vente : Scan = new Scan();
    scan_vente.addColumn(Bytes.toBytes(cf), Bytes.toBytes(prod));
    val result_vente : ResultScanner = table_vente.getScanner(scan_vente);
    for(res <- result_vente.asScala){ // Compte le nb de produit vendu
      val produit : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(prod)));
      if(!map_vente.contains(produit)){
        map_vente.put(produit, 1);
      }else{
        map_vente.put(produit, map_vente.apply(produit) + 1);
      }
    }
    
    // Produit
    val scan_produit : Scan = new Scan();
    scan_produit.addColumn(Bytes.toBytes(cf), Bytes.toBytes(idprod));
    scan_produit.addColumn(Bytes.toBytes(cf), Bytes.toBytes(cat));
    val result_produit : ResultScanner = table_produit.getScanner(scan_produit);
    
    for(res <- result_produit.asScala){ // Compte le produit par catégorie
      val id_produit : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(idprod)));
      val categorie : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(cat)));
      if(!map_prod.contains(categorie)){
        map_prod.put(categorie, map_vente(id_produit));
      }else{
        map_prod.put(categorie, map_prod.apply(categorie) + map_vente(id_produit));
      }
    }
    
    // Categorie
    val scan_categorie : Scan = new Scan();
    scan_categorie.addColumn(Bytes.toBytes(cf), Bytes.toBytes(idcat));
    val result_categorie : ResultScanner = table_categorie.getScanner(scan_categorie);
    
    for(res <- result_categorie.asScala){
      val put : Put = new Put(res.getRow);
      val id_categorie : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(idcat)));
      put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("nb_ventes"), Bytes.toBytes(map_prod(id_categorie)));
      table_categorie.put(put);
    } 
  }
  

  def nbVenteParCategorieDenormalise:Map[String,Int] = {
        
    // Config connection
    val conf : Configuration = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum" , "localhost");
		val connection : Connection = ConnectionFactory.createConnection(conf);

		// TableName
		val tn_categorie : TableName = TableName.valueOf("stereoprix", "categorie");
		
		// Table
		val table_categorie : Table =  connection.getTable(tn_categorie);
		
		// Variables
    val map : Map[String,Int] = new HashMap[String,Int];
    
    val cf : String = "defaultcf";
    val des : String = "designation";
    val nb : String = "nb_ventes";
    
    // Categorie
    val scan_categorie : Scan = new Scan();
    scan_categorie.addColumn(Bytes.toBytes(cf), Bytes.toBytes(des));
    scan_categorie.addColumn(Bytes.toBytes(cf), Bytes.toBytes(nb));
    val result_categorie : ResultScanner = table_categorie.getScanner(scan_categorie);
    
    
    for(res <- result_categorie.asScala){
      val categorie : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(des)));
      val ventes : Int = Bytes.toInt(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(nb)));
      if(!map.contains(categorie)){
        map.put(categorie, ventes);
      }
    }
    return map;
  }
    
  /*
   * Ajoute une colonne dans la table vente qui renseigne la dénomination de la catégorie associée 
   * */
  
  def denormalise_v2 = {
    
    // Config connection
    val conf : Configuration = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum" , "localhost");
		val connection : Connection = ConnectionFactory.createConnection(conf);

		// TableName
		val tn_categorie : TableName = TableName.valueOf("stereoprix", "categorie");
		val tn_produit : TableName = TableName.valueOf("stereoprix", "produit");
		val tn_vente : TableName = TableName.valueOf("stereoprix", "vente");
		
		// Table
		val table_categorie : Table =  connection.getTable(tn_categorie);
		val table_produit : Table =  connection.getTable(tn_produit);
		val table_vente : Table =  connection.getTable(tn_vente);
		
		// Variables
    val map_prod : Map[String, String] = new HashMap[String,String];
    val map_cat : Map[String, String] = new HashMap[String,String];
    
    val cf : String = "defaultcf";
    val prod : String = "produit";
    
    val idprod : String = "idprod";
    val cat : String = "categorie";
    
    val idcat : String = "idcat";
    val des : String = "designation";
    
    // Produit - On commence par récupérer le lien entre idprod et catégorie
    val scan_produit : Scan = new Scan();
    scan_produit.addColumn(Bytes.toBytes(cf), Bytes.toBytes(idprod));
    scan_produit.addColumn(Bytes.toBytes(cf), Bytes.toBytes(cat));
    val result_produit : ResultScanner = table_produit.getScanner(scan_produit);
    
    for(res <- result_produit.asScala){
      val id_produit : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(idprod)));
      val categorie : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(cat)));
      if(!map_prod.contains(id_produit)){
        map_prod.put(id_produit, categorie);
      }
    }
    
    // Categorie - On récupère ensuite le lien entre idcat et désignation
    val scan_categorie : Scan = new Scan();
    scan_categorie.addColumn(Bytes.toBytes(cf), Bytes.toBytes(idcat));
    scan_categorie.addColumn(Bytes.toBytes(cf), Bytes.toBytes(des));
    val result_categorie : ResultScanner = table_categorie.getScanner(scan_categorie);
    
    for(res <- result_categorie.asScala){ 
      val id_categorie : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(idcat)));
      val designation : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(des)));
      if(!map_cat.contains(id_categorie)){
        map_cat.put(id_categorie, designation);
      }
    }
    
    // Ajouter la désignation dans la table Vente
    val scan_vente : Scan = new Scan();
    scan_vente.addColumn(Bytes.toBytes(cf), Bytes.toBytes(prod));
    val result_vente : ResultScanner = table_vente.getScanner(scan_vente);
        
    for(res <- result_vente.asScala){
      val put : Put = new Put(res.getRow);
      val produit : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(prod)));

      put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("cat_designation"), Bytes.toBytes(map_cat(map_prod(produit))));
      table_vente.put(put);
    }
    
  }
  
  
  def nbVenteParCategorieDenormalise_v2:Map[String,Int] = {
        
    // Config connection
    val conf : Configuration = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum" , "localhost");
		val connection : Connection = ConnectionFactory.createConnection(conf);

		// TableName
		val tn_vente : TableName = TableName.valueOf("stereoprix", "vente");
		
		// Table
		val table_vente : Table =  connection.getTable(tn_vente);
		
		// Variables
    val map : Map[String,Int] = new HashMap[String,Int];
    
    val cf : String = "defaultcf";
    val des : String = "cat_designation";
    
    // Vente
    val scan_vente : Scan = new Scan();
    scan_vente.addColumn(Bytes.toBytes(cf), Bytes.toBytes(des));
    val result_vente : ResultScanner = table_vente.getScanner(scan_vente);
    
    
    for(res <- result_vente.asScala){
      val cat : String = Bytes.toString(res.getValue(Bytes.toBytes(cf), Bytes.toBytes(des)));
      if(!map.contains(cat)){
        map.put(cat, 1);
      }else{
        map.put(cat, map.apply(cat) + 1);
      }
    }
    return map;

  }
  
  
  /*
   * addVente pour le cas où on a ajouté une colonne dans la table categorie qui maintient le nombre de ventes associées
   * */
  
  def addVente(c:Connection,idvente:String,idclient:String,idmag:String,idprod:String,date:String):Unit = {
  
    // Variables
		val cf : String = "defaultcf";
		val vente : String = "vente";
		val prod : String = "produit";
		val cat : String = "categorie";
		val des : String = "designation";
		val nb : String = "nb_ventes";
		
    // TableName
		val tn_vente : TableName = TableName.valueOf("stereoprix", vente);
		val tn_produit : TableName = TableName.valueOf("stereoprix", prod);
		val tn_categorie : TableName = TableName.valueOf("stereoprix", cat);
		
		// Table
		val table_vente : Table =  c.getTable(tn_vente);
		val table_produit : Table =  c.getTable(tn_produit);
		val table_categorie : Table =  c.getTable(tn_categorie);
		
    // Ajout de la vente dans la table
    val put : Put = new Put(Bytes.toBytes(idvente));        
    put.addColumn( Bytes.toBytes(cf), Bytes.toBytes("idvente"), Bytes.toBytes(idvente));
    put.addColumn( Bytes.toBytes(cf), Bytes.toBytes("client"), Bytes.toBytes(idclient));
    put.addColumn( Bytes.toBytes(cf), Bytes.toBytes("magasin"), Bytes.toBytes(idmag));
    put.addColumn( Bytes.toBytes(cf), Bytes.toBytes("produit"), Bytes.toBytes(idprod));
    put.addColumn( Bytes.toBytes(cf), Bytes.toBytes("date"), Bytes.toBytes(date));
    table_vente.put(put);
    //println(idprod);
    
    // Jointure entre idprod et categorie
    val get_prod : Get = new Get(Bytes.toBytes(idprod));
    get_prod.addColumn(Bytes.toBytes(cf), Bytes.toBytes(cat));
    val res_prod: Result = table_produit.get(get_prod);
    val categorie : Array[Byte] = res_prod.getValue(Bytes.toBytes(cf), Bytes.toBytes(cat));
    //println(Bytes.toString(categorie));
    
    // Jointure entre categorie et designation    
    val get_cat : Get = new Get(categorie);
    get_cat.addColumn(Bytes.toBytes(cf), Bytes.toBytes(des));
    val res_cat: Result = table_categorie.get(get_cat);
    val designation : Array[Byte] = res_cat.getValue(Bytes.toBytes(cf), Bytes.toBytes(des));
    //println(Bytes.toString(designation));
    
    // MAJ de la colonne dénormalisé
    get_cat.addColumn(Bytes.toBytes(cf), Bytes.toBytes(nb));
    val res_des : Result = table_categorie.get(get_cat);
    val ventes : Array[Byte] = res_des.getValue(Bytes.toBytes(cf), Bytes.toBytes(nb));
    //println(Bytes.toInt(ventes))
    
    val put_denormalise: Put = new Put(categorie);
    put_denormalise.addColumn( Bytes.toBytes(cf), Bytes.toBytes(nb), Bytes.toBytes(Bytes.toInt(ventes) + 1));
    table_categorie.put(put_denormalise);
    
    
  }
}