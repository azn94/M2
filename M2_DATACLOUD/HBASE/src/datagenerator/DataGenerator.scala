package datagenerator

import scala.collection.mutable.{Map=>Mmap}

class DataGenerator(val propfilename:String){
  
  
  val config=new Configuration(propfilename)
  
  val typegeneratorkey = "type"
  
  
  
  protected[this] val typesclass = Mmap(
      "twit" -> classOf[datagenerator.twit.TwitGenerator].getCanonicalName,
      "textfile" -> classOf[datagenerator.textfile.TextFileGenerator].getCanonicalName,
      "filemultiplicator" -> classOf[datagenerator.filemultiplicator.FileMultiplicator].getCanonicalName
   )
  
    
  
  def process={
       
    val sb = new StringBuilder
    sb.append("types possible pour la propriété '"+typegeneratorkey+"' ")
    typesclass.keySet.foreach(n=>sb.append(n+" "))
    val typegeneratorvalue = config.getMandatoryProperty(typegeneratorkey, sb.toString)
        
    val nameclass = typesclass.getOrElse(typegeneratorvalue, "")
        
    if(nameclass.equals("")) config.fatal("la valeur de la clé '"+typegeneratorkey+"' n'est pas une valeur valide", sb.toString)
    
    val c:Class[_ <: Generator] =  Class.forName(nameclass).asSubclass(classOf[Generator])
    c.getConstructor(classOf[Configuration]).newInstance(config).generate
  }
  
  
}


object Main {  
  def main(args:Array[String]):Unit={
    if(args.length !=1 ){
      System.err.println("usage : <fichier conf>")
      System.exit(1)
    }  
    new DataGenerator(args(0)).process 
    System.exit(0)
    
    
  }
}