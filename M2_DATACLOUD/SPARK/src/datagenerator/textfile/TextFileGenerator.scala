package datagenerator.textfile

import java.io.File
import java.io.PrintWriter
import java.util.Random

import datagenerator.Configuration
import datagenerator.Generator
import datagenerator.textfile.field.Chaine
import datagenerator.textfile.field.Entier
import datagenerator.textfile.field.Field
import datagenerator.textfile.field.MyDate
import datagenerator.textfile.field.List


class TextFileGenerator(config:Configuration) extends Generator {
 
  
  private[this] val seedkey="seed";
  private[this] val seeddefaultvalue="5";
  
  private[this] val sizefichierkey="text.sizefichier";
  
  private[this] val seed = config.getOptionalProperty(seedkey, seeddefaultvalue).toInt
  
  private[this] val sizefichier=config.getMandatoryProperty(sizefichierkey, "définir la taille d'un fichier en kilo octet ").toInt
  
  
  private[this] val nbfichierkey="text.nbfichier";
  private[this] val nbfichier=config.getOptionalProperty(nbfichierkey, "1").toInt
    
  private[this] val name = config.getMandatoryProperty("text.name", "définir 'text.name' pour nommer les fichiers" )
  private[this] val outdir = config.getOptionalProperty("text.outdir", System.getProperty("user.dir")+"/"+name+"_"+nbfichier+"fichier_de_"+sizefichier+"ko")
  
  
  private[this] def getField(typechamp:String,namechamp:String):Field={
    return typechamp  match {
        
        case "entier" => new Entier(namechamp,config.getOptionalProperty("text."+namechamp+".valmin", "1").toInt, config.getOptionalProperty("text."+namechamp+".valmax", "30").toInt)
        case "date" => new MyDate(namechamp, config.getOptionalProperty("text."+namechamp+".period", "4").toInt)
        case "chaine" => new Chaine(namechamp, config.getOptionalProperty("text."+namechamp+".nbmax", "30").toInt)
        case "list" => {
          val sep=config.getOptionalProperty("text."+namechamp+".separateur", ".")
          val typelist=config.getMandatoryProperty("text."+namechamp+".list.type", "definir 'text."+namechamp+".list.type' dont la valeur est {entier,date, chaine}")
          if(typelist.equals(typechamp)) config.fatal("impossible d'avoir une liste de liste", "le type de interne de liste ne peut être que {entier,date, chaine}")
          val f = getField(typelist,""+namechamp+".list")
          new List(namechamp,sep,f, config.getOptionalProperty("text."+namechamp+".taillemin", "1").toInt,config.getOptionalProperty("text."+namechamp+".taillemax", "10").toInt)
        }
        case _ => {config.fatal("definir 'text."+namechamp+".type' dont la valeur est {entier,date, chaine, list}", ""); null}
    }
  }
  
  private[this] val fields:Array[Field] = {
    val champs = config.getMandatoryProperty("text.champs", "définir 'text.champs=nomchamp1,nomchamp2,nomchamp3,...'")
    val nameschamps=champs.split(",")
    val res = new Array[Field](nameschamps.length)
    var i = 0
    for(namechamp <- nameschamps){
      
      var typechamp = config.getMandatoryProperty("text."+namechamp+".type", "definir 'text."+namechamp+".type' dont la valeur est {entier,date, chaine, list}")
      
      var champ:Field = getField(typechamp, namechamp)
      if(config.isPropertyExist("text."+namechamp+".dependance")){
        var namedependance = config.getMandatoryProperty("text."+namechamp+".dependance", "Cette erreur n'aurait pas du se produire")
        val copy_res=res.toSeq.filter(_!=null).filter(_.name.equals(namedependance))
        if(copy_res.size != 1) config.fatal("'text."+namechamp+".dependance' reference un champs inconnu ou declaré après le champs"+namechamp, "déclarer le champs de dépendance avant "+namechamp+" dans 'text.champs'")
        champ.declareOneToAllRelation(copy_res(0))
      }
      
      res(i)=champ
      i=i+1
      
    }
    res
   
  }
  
  
  def generate={
    
    val file = new File(outdir)
    if(file.exists()) config.fatal("fichier "+outdir+" existe déjà", "le répertoire de sortie ne doit pas exister")
    file.mkdirs
    val random = new Random(seed)
    for(i <- 1.to(nbfichier)){
      var nom_fichier=name+"_fichier_"+i
      var file = new File(outdir+"/"+nom_fichier)
      var w = new PrintWriter(file)
      
      while(file.length() < (sizefichier*1024)){
       
        var sb = new StringBuffer
        for(field <- fields){
          sb.append(field.getNewRandomValue(random)+" ")
        }
        w.println(sb.toString())
        w.flush()
      }
      w.close()
    }
    
  } 
}