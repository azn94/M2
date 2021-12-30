package datagenerator.hbase.column

import java.text.SimpleDateFormat
import org.apache.hadoop.hbase.util.Bytes
import java.util.Date
import scala.util.Random


object MyDate{
  val periodkey="period"
  val perioddefault="3"
}


class MyDate(namespace:String, 
             nameTable:String,
             colfam:String,
             name:String,
             private[this] val period:Int) extends Column(namespace,nameTable,colfam,name) {
 
  def getRandomValue(r:Random):Array[Byte] = {
    var max = System.currentTimeMillis()//ms
    var min = max - (period.asInstanceOf[Long] * 365L * 24L * 60L *60L * 1000L);//ms
    max = max / (60L * 1000L);//ms -> minute
    min = min / (  60L * 1000L);//ms -> minute
    
    val interval = max - min
    var res  = r.nextInt(interval.asInstanceOf[Int])+min//minute
    res = res *60L * 1000L;//ms
    return Bytes.toBytes(new SimpleDateFormat("dd_MM_yy_HH_mm").format(new Date(res)))
  }
}