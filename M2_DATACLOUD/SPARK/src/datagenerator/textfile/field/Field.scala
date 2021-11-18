package datagenerator.textfile.field

import java.util.Random

import scala.collection.mutable.{Map=>Mmap}

abstract class Field(val name:String) {
  
  
   private[this] var lastvalue:String=""
   
   def getLastGeneratedValue=lastvalue
   
   private[this] var other:Field=null
   
   private[this] val valueof = Mmap[String,String]()
   
   def declareOneToAllRelation(other:Field){
     this.other=other
   }
   
   def getNewRandomValue(r:Random):String={
     if(other!=null){
         val lastgeneratedvalue = other.getLastGeneratedValue
         
         
         lastvalue =   valueof.getOrElse(lastgeneratedvalue, getRandomValue(r))
         valueof.put(lastgeneratedvalue, lastvalue)
     }else{
       lastvalue=getRandomValue(r)  
     }
     
     return lastvalue
   }
   protected[Field] def getRandomValue(r:Random):String;
}