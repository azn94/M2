package datagenerator.hbase.column

import org.apache.hadoop.hbase.util.Bytes
import scala.util.Random



object Chaine{
  val nbmaxkeysuffix="chaine.nbmax"
  val nbmaxdefault="30"
}


class Chaine(namespace:String, 
             nameTable:String,
             colfam:String,
             name:String,
             private[this] val nbmax:Int) extends Column(namespace,nameTable,colfam,name) {
  
   def getRandomValue(r:Random):Array[Byte] = Bytes.toBytes(name+nameTable+(r.nextInt(nbmax)));
  
}