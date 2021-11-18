package datagenerator.filemultiplicator

import datagenerator.Generator
import datagenerator.Configuration
import java.io.File
import java.io.PrintStream
import java.io.FileInputStream
import java.io.FileOutputStream

class FileMultiplicator(config:Configuration) extends Generator{
  
  val source:File={
    val res= new File(config.getMandatoryProperty("source", "chemin absolu du fichier source"))
    if(!res.exists()) config.fatal("le fichier "+res.getAbsolutePath+" n'existe pas", "donner le chemin absolu d'un fichier source")
    res
  }
  
  val size_dest=config.getMandatoryProperty("sizedest", "taille du fichier à produire en Mo").toInt
  
  
  val dest:File={
    val defaultname=System.getProperty("user.dir")+"/"+source.getName+"-"+size_dest
    val res = new File(config.getOptionalProperty("dest",defaultname))
    if(res.exists()) res.delete()
    res
  }
  
  
  def generate={
    
    val content_source:Array[Byte]=new Array[Byte](source.length().toInt)
    
    val in = new FileInputStream(source)
    in.read(content_source)
    in.close
    
    val out = new FileOutputStream(dest)
    while(dest.length() < (size_dest*1024*1024)){
       out.write(content_source) 
       out.flush();
    }
    out.close
    
    
  }
}