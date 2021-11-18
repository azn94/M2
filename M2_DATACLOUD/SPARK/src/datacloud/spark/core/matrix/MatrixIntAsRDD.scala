package datacloud.spark.core.matrix

import datacloud.scala.tpobject.vector.VectorInt
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext

object MatrixIntAsRDD{
    implicit def conversion(rdd : RDD[VectorInt]): MatrixIntAsRDD = {
    return new MatrixIntAsRDD(rdd);
  }
  
  def makeFromFile(url : String, nb_partitions : Int, spark : SparkContext): MatrixIntAsRDD = {
    
    // 1er rdd (RDD[String]) : création à partir du fichier (textfile)
    val rdd1 = spark.textFile(url);
    
    //2ème rdd (RDD[Array[Int]]) : on transforme chaque ligne du 1er rdd en array de Int (map)
    val rdd2 = rdd1.map(line => line.split(" ").map(elem => elem.toInt))
    
    //3ème rdd (RDD[VectorInt]) : on transforme chaque élément du 2ème rdd en VectorInt (map)
    val rdd3 = rdd2.map(array => new VectorInt(array))
    
    // 4ème rdd (RDD[(VectorInt,Long)]) : lier chaque élément du 3eme rdd avec son index (zipWithIndex)
    val rdd4 = rdd3.zipWithIndex();
    
    // 5ème rdd (RDD[(VectorInt,Long)]) : trier le 4ème rdd en fonction de l’index croissant (sortBy)
    val rdd5 = rdd4.sortBy(tuple => tuple._2, ascending = true, nb_partitions);
    
    // 6ème rdd (RDD[VectorInt]) : enlever la partie droite de chaque élément pour ne garder que le vecteur (map)   
    val rdd6 = rdd5.map(tuple => tuple._1);
    
    //enfin construire et retourner une MatrixIntAsRDD à partir du 6ème rdd.
    return conversion(rdd6);
  }
}

class MatrixIntAsRDD(val lines : RDD[VectorInt]){
 
   override def toString={  
     val sb = new StringBuilder();
     lines.collect().foreach(line => sb.append(line+"\n"));
     sb.toString()
   }
   
   def nbLines(): Int ={
     return lines.count().toInt;
   }
   
   def nbColumns(): Int = {
     return lines.first().length;
   }
   
   def get(i : Int, j : Int) : Int = {
     val nb_lignes = nbLines();
     val nb_cols = nbColumns();
     
     if( i < 0 ||(i > nb_lignes) ||(j < 0 )||(j > nb_cols)){
       throw new Exception("i doit être entre 0 et "+nb_lignes+" et j doit être en 0 et"+nb_cols);
     }else{
       // On ajoute un index à line pour avoir le nombre de lignes
       val rdd1 = lines.zipWithIndex();
       
       // On récupère la i-ème ligne de la matrice
       val rdd2 = rdd1.filter(_._2 == i);
       
       // On récupère l'élément du rdd : (VectorInt,i)
       val tuple = rdd2.first;
       
       // On retourne le j-ème élément du vecteur de Int
       return tuple._1.get(j);
     }
   }
   
   override def equals(a : Any):Boolean ={
     a match {
     case other: MatrixIntAsRDD => 
         {
           if(other.nbLines() == this.nbLines() && other.nbColumns == this.nbColumns()){
             var mat = other.lines.zip(this.lines).filter(tuple => tuple._1!=tuple._2);
             if(mat.count() == 0){
               return true;
             }
           }
           return false;
         }
     case _ => false;
     }
   }
   
   
   def +(other: MatrixIntAsRDD): MatrixIntAsRDD = {
     return other.lines.zip(this.lines).map(tuple => tuple._1+tuple._2);
   }
   
   def transpose():MatrixIntAsRDD = {

     // RDD(value, Index_col) : Ajout d'un index colonne
     var rdd1 = this.lines.flatMap(tuple => (tuple.elements.zipWithIndex))

     // RDD(Index_col, value) : Inverse les places de l'index et de la valeur
     var rdd2 = rdd1.map(a=> (a._2, a._1))
     
     // (Index_col, interable de value) : Regrouper selon les index de colonne
     var rdd3 = rdd2.groupByKey()
     
     // sort selon la clé = index_col
     var rdd4 = rdd3.sortBy(_._1, ascending=true)
     
     // RDD[VectorInt] : transforme l'itérable en tableau puis crée un vecteur de int avec
     var rdd5 = rdd4.map(tuple => new VectorInt(tuple._2.toArray))
     
     return new MatrixIntAsRDD(rdd5);

    }
   
    def *(other: MatrixIntAsRDD): MatrixIntAsRDD = {

      // RDD[VectorInt] : Transpose la matrice
      val rdd1 = this.transpose().lines;
      
      // RDD[(VectorInt, VectorInt)] : zip la colonne i de this avec la ligne i de other
      val rdd2 = rdd1.zip(other.lines);
      
      // RDD[Array[VectorInt]]: Applique le produit dyadique sur chaque couple
      val rdd3 = rdd2.map(tuple => tuple._1.prodD(tuple._2));
      
      // RDD[(VectorInt, Indice_ligne)] : Ajoute l'indice des lignes
      val rdd4 = rdd3.flatMap(vector => vector.zipWithIndex);
      
      // RDD[(Indice ligne, VectorInt)] : Mets l'indice en clé
      val rdd5 = rdd4.map(tuple => (tuple._2,tuple._1));
      
      // RDD[(Indice ligne, VectorInt)] : Somme selon l'indice
      val rdd6 = rdd5.reduceByKey(_+_);
      
      // RDD[(Indice ligne, VectorInt)] : Range selon l'indice
      val rdd7 = rdd6.sortBy(_._1, ascending = true);
      
      // RDD[VectorInt] : Retire l'indice
      val rdd8 = rdd7.map(_._2);
      
      return new MatrixIntAsRDD(rdd8);
      
    }
   
   
   
   
   
   
   
   
   
   
   
   
   
   
}