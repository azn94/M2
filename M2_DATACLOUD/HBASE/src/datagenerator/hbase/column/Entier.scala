package datagenerator.hbase.column

import org.apache.hadoop.hbase.util.Bytes
import scala.util.Random



object Entier{
  val nbmaxkeysuffix="nbmax"
  val nbmaxdefault="30"
}

class Entier(namespace:String, 
             nameTable:String,
             colfam:String,
             name:String,
             private[this] val nbmax:Int) extends Column(namespace,nameTable,colfam,name) {
  
  
  
  def getRandomValue(r:Random):Array[Byte] = Bytes.toBytes(r.nextInt(nbmax)+1)
  
}