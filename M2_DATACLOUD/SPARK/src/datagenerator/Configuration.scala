package datagenerator

import java.io.FileInputStream
import java.util.Properties

class Configuration(propfilename:String) {
  
  private[this] val props = new Properties

  props.load(new FileInputStream(propfilename))
  
  def getMandatoryProperty(key:String, messageifnotfound:String) : String={
    val res = props.getProperty(key, "")
    if(res.equals("")) fatal("propriété '"+key+"' manquante",messageifnotfound)
    return res
  }
  
  
  def getOptionalProperty(key:String, defaultvalue:String):String={
    val res = props.getProperty(key, defaultvalue)
    if(res.equals(defaultvalue)) info("Utilisation de la valeur par défaut de la propriété '"+key+"' = "+defaultvalue)
    return res
  }
  
  def isPropertyExist(key:String):Boolean= props.getProperty(key)!=null
  
  
  
  def fatal(problem:String, solution:String)={
    val message ="ERREUR CONFIG : "+ problem+" \n \t "+solution
    System.err.println(message)
    System.exit(1)
  }
  
  def info(message:String)={
    System.err.println("INFO : "+message)
  }
  
  def warning(message:String)={
    System.err.println("ATTENTION : "+message)
  }
}