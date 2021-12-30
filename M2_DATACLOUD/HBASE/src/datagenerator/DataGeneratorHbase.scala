package datagenerator


class DataGeneratorHbase(override val propfilename:String) extends DataGenerator(propfilename){
  
  
 
  typesclass.put("HbaseSchema" ,classOf[datagenerator.hbase.HbaseSchemaGenerator].getCanonicalName)
  
}


object MainHbaseGenerator{
  def main(args:Array[String]):Unit={
    if(args.length !=1 ){
      System.err.println("usage : <fichier conf>")
      System.exit(1)
    }  
    new DataGeneratorHbase(args(0)).process 
    System.exit(0)
    
    
  }
}