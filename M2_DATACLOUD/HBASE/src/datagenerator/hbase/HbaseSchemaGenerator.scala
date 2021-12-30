package datagenerator.hbase

import java.util.Properties

import scala.util.Random

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.ConnectionFactory
import datagenerator.hbase.column.Column
import datagenerator.Configuration
import datagenerator.Generator
import datagenerator.hbase.column.ForeignKey
import datagenerator.hbase.column.Id
import datagenerator.hbase.column.Chaine
import datagenerator.hbase.column.Entier
import datagenerator.hbase.column.MyDate




case class TableSpec(name:String,id:Column,columns:Array[Column],maxsize:Int,minsize:Int){
  def getColFams= columns.map(_.colfam).+:(id.colfam).distinct

  override def toString:String="("+name+" "+id+" "+columns.map(_.toString()).toSeq+" "+maxsize+" "+minsize
 
  
}

class HbaseSchemaGenerator(config:Configuration) extends Generator {

  
  
  private[this] val seedkey="seed";
  private[this] val seeddefaultvalue="10";
  
  private[this] val CF_DEFAULT="defaultcf"
  
  private[this] val zookeeperquorumkey="zookeeperquorum";
  private[this] val zookeeperquorumdefaultvalue="localhost";
  
  
  private[this] val namespacekey="namespace";
  private[this] val namespacedefaultvalue="default";
  
  
  private[this] val seed:Int = config.getOptionalProperty(seedkey, seeddefaultvalue).toInt
    
  private[this] val quorum_zookeeper:String= config.getOptionalProperty(zookeeperquorumkey, zookeeperquorumdefaultvalue)
    
    
  private[this] val conf = HBaseConfiguration.create();
  conf.set("hbase.zookeeper.quorum", quorum_zookeeper)
  private[this]  val c = ConnectionFactory.createConnection(conf)
  
  private[this] val namespace:String= config.getOptionalProperty(namespacekey, namespacedefaultvalue)

  
  private[this] val tableskey="tables"
  
  
  private[this] val les_types_de_col="{entier, chaine,id,fk,date}"
  
  private[this] val tables:Array[TableSpec] = {
    val tables_value=config.getMandatoryProperty(tableskey, "liste des nom de tables de la base séparés par une virgule : '"+tableskey+" =nametable0,nametable1,....'")
    val lestables= tables_value.split(",")
    val res = new Array[TableSpec](lestables.length)
    var i =0
    //tables= nametable1,nametable2,nametable3
    for(table <- lestables){
       //table.<nametable>.columns =  namecol1,namecol2,namecol3
      var lescolumnsvalue =  config.getMandatoryProperty("table."+table+".columns", "définir 'table."+table+".columns=namecol0,namecol1,....' pour connaitre la liste des noms de colonne")
        
      var lescolumns = lescolumnsvalue.split(",")
      var columns = new Array[Column](lescolumns.length)
      var j= 0
      for(column <- lescolumns){
        var colfam = config.getOptionalProperty("table."+table+"."+column+".colfam", CF_DEFAULT)
        
        
        
        var typecol = config.getMandatoryProperty("table."+table+"."+column+".type", "définir 'table."+table+"."+column+".type="+les_types_de_col+"'")
                 
        var col:Column = typecol match{
          case "entier" => new Entier(namespace,table,colfam,column, config.getOptionalProperty("table."+table+"."+column+"."+Entier.nbmaxkeysuffix, Entier.nbmaxdefault).toInt)
          case "chaine" => new Chaine(namespace,table,colfam,column,config.getOptionalProperty("table."+table+"."+column+"."+Chaine.nbmaxkeysuffix, Chaine.nbmaxdefault).toInt)
          case "id" => new Id(namespace,table,colfam,column)
          case "fk" => {
            
            var name_foreign_table = config.getMandatoryProperty("table."+table+"."+column+".nametable", "définir 'table."+table+"."+column+".nametable = <nom de la table reférencée>'")
                       
            if(! lestables.contains(name_foreign_table))config.fatal("valeur de la propriété 'table."+table+"."+column+".nametable' incohérente", "la valeur doit être le nom d'une table déclarée")
            new ForeignKey(namespace, table,colfam, column,name_foreign_table,c)
          }
                                             
          case "date" =>  new MyDate(namespace,table,colfam,column, config.getOptionalProperty("table."+table+"."+column+"."+MyDate.periodkey, MyDate.perioddefault).toInt)
          
          case _ => {config.fatal("valeur de la propriété 'table."+table+"."+column+".type' incohérente","valeurs possibles {entier, chaine,id,fk,date}'"); null}

        
        }
        columns(j)=col
        j=j+1
        
      }
      //table.<nametable>.maxsize = ...
      var maxsize = config.getMandatoryProperty("table."+table+".maxsize", "définir 'table."+table+".maxsize = <taille max de la table>'").toInt
      //table.<nametable>.minsize = ... 
      var minsize = config.getOptionalProperty("table."+table+".minsize", "1").toInt
      
      var colidname = config.getMandatoryProperty("table."+table+".colidname", "définir 'table."+table+".colidname' = <nom de la colonne id>")
      
      
      var type_colid= config.getMandatoryProperty("table."+table+"."+colidname+".type", "")
      if(!type_colid.equals("id")) config.fatal("la propriété table."+table+"."+colidname+".type n'a pas la valeur id", "'table."+table+".colidname' doit referencer une colonne de type id")            
      var colid:Column = null
      var trouve=false;
      var k = 0
      while(!trouve && k<columns.length ){
        if(columns(k).name.equals(colidname)){
          if(columns(k).isInstanceOf[Id]){
            colid=columns(k)
            trouve=true
          }else config.fatal("la propriété table."+table+"."+colidname+".type n'a pas la valeur id", "'table."+table+".colidname' doit referencer une colonne de type id")
        }
        k=k+1
      }
      if(!trouve) config.fatal("la propriété table."+table+"."+colidname+".type ne reference aucune colonne existante dans la table "+table, "'table."+table+".colidname' doit referencer une colonne de type id")
      res(i)=TableSpec(table,colid,columns, maxsize, minsize)
      i=i+1
    }
    //res.foreach(println)
    res
  }
                      
  
  def generate:Unit = {
    val random = new Random(seed);
    
    
	  val tablecreator = new TableCreator(c)
	  config.info("Verification de l'existance du namespace "+namespace)
	  val create =  tablecreator.createNamespaceIfNotexists(namespace)
	  if (create) config.info("le namespace "+namespace+" a été créé")
	  for(ts <- tables){
	    var size = random.nextInt(ts.maxsize-ts.minsize)+ts.minsize;
	    config.info("création de la table "+ts.name)
	    val replace = tablecreator.createTableOrOverwrite(namespace, ts.name, ts.getColFams.toSeq)
	    if(replace) config.info("la table "+ts.name+" existait déjà et à été écrasée")
	    tablecreator.fillTable(namespace, ts.name, size, ts.id, ts.columns,random)
	    config.info("Création de "+ size+" lignes dans la table "+ts.name)
	  }
	  c.close()	  
  }
  
}