package datagenerator.textfile.field

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Random

class MyDate(name:String, private[this] val period:Int) extends Field(name){
  
   def getRandomValue(r:Random):String = {
    var max = System.currentTimeMillis()//ms
    var min = max - (period.asInstanceOf[Long] * 365L * 24L * 60L *60L * 1000L);//ms
    max = max / (60L * 1000L);//ms -> minute
    min = min / (  60L * 1000L);//ms -> minute
    
    val interval = max - min
    var res  = r.nextInt(interval.asInstanceOf[Int])+min//minute
    res = res *60L * 1000L;//ms
    return new SimpleDateFormat("dd_MM_yyyy_HH_mm").format(new Date(res))
  }
  
}
