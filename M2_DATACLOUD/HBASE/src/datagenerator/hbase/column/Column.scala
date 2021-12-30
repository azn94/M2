package datagenerator.hbase.column

import scala.util.Random

abstract class Column(val namespace:String, 
                      val nameTable:String,
                      val colfam:String,
                      val name:String) { 
  def getRandomValue(r:Random):Array[Byte];
  
  override def toString:String= {
    return this.getClass.getSimpleName+"("+namespace+" "+nameTable+" "+colfam+" "+name+")"
  }
}