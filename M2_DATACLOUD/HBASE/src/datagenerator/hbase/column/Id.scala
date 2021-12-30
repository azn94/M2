package datagenerator.hbase.column

import org.apache.hadoop.hbase.util.Bytes
import scala.util.Random



class Id(namespace:String, 
             nameTable:String,
             colfam:String,
             name:String) extends Column(namespace,nameTable,colfam,name) {
  private[this] var cpt=0
  def getRandomValue(r:Random):Array[Byte] = {
    cpt=cpt+1
    return Bytes.toBytes(name+nameTable+cpt);
  }
}