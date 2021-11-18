package datacloud.spark.core.stereoprix

import org.apache.spark._

object Stats {
  
  
  def chiffreAffaire(url : String, annee : Int): Int = {
    //JJ_MM_AAAA_hh_mm magasin prix produit categorie
    
    val conf = new SparkConf().setAppName( "chiffreAffaire").setMaster("local[*]");
    val spark = new SparkContext(conf);
    val textFile = spark.textFile(url);
    
    // On sépare chaque ligne en Array de mots
    val words = textFile.map(_.split(" "));
    
    // On ne garde que les lignes dont l'année correspond
    val rdd_annee = words.filter(_(0).split("_")(2).toInt == annee);
    
    // On transforme le prix en entier
    val rdd_prix = rdd_annee.map(_(2).toInt);
    
    // On calcule la somme des prix
    val res = rdd_prix.reduce(_+_);
    
    val chiffreAffaire = res;
    
    spark.stop();
    
    return chiffreAffaire; 
  }
  
  def chiffreAffaireParCategorie(urlEntre : String, urlSortie : String) : Unit = {
    //JJ_MM_AAAA_hh_mm magasin prix produit categorie
    
    val conf = new SparkConf().setAppName( "chiffreAffaire par categorie").setMaster("local[*]");
    val spark = new SparkContext(conf);
    val textFile = spark.textFile(urlEntre);
    
    // On sépare chaque ligne en Array de mots
    val words = textFile.map(_.split(" "));
    
    // Crée un RDD de tuple avec pour clé la catégorie
    val rdd_categorie = words.keyBy(_(4));
    
    // On récupère le prix pour chaque ligne
    val rdd_prix = rdd_categorie.map(tuple => (tuple._1,tuple._2(2).toInt));
    
    // On calcul le prix par catégorie
    val rdd_categorie_prix = rdd_prix.reduceByKey(_+_);
    
    // On le transforme dans la forme que l'on veux
    val res = rdd_categorie_prix.map(tuple => tuple._1 +":"+tuple._2.toString());
    
    res.saveAsTextFile(urlSortie);
    spark.stop();
  }
  
  def produitLePlusVenduParCategorie(urlEntre : String, urlSortie : String) : Unit = {
    //JJ_MM_AAAA_hh_mm magasin prix produit categorie
    
    val conf = new SparkConf().setAppName( "produit le plus vendu par categorie").setMaster("local[*]");
    val spark = new SparkContext(conf);
    val textFile = spark.textFile(urlEntre); 
    
    // On sépare chaque ligne en Array de mots
    val words = textFile.map(_.split(" "));
    
    // Crée un RDD de tuple avec ([produit,categorie], 1)
    val rdd_produit_categorie = words.map(array => ((array(3),array(4)),1));
    
    // On somme le nombre de produit selon la catégorie
    val tmp = rdd_produit_categorie.reduceByKey(_+_);
    
    // On restructure en (categorie,[produit, nb])
    val res = tmp.map(tuple => (tuple._1._2,(tuple._1._1,tuple._2)))
    
    // On récupère le max nb de chaque catégorie
    val res2 = res.reduceByKey((val1, val2) => if (val1._2 > val2._2){
                                                 (val1._1,val1._2)
                                               }else{
                                                 (val2._1,val2._2)
                                               });
    
    // On restructure la donnée 
    val res3 = res2.map(tuple => tuple._1+":"+tuple._2._1);
    
    res3.saveAsTextFile(urlSortie);
    spark.stop();
    
    
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
}