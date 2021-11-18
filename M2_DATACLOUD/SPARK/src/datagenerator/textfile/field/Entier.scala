package datagenerator.textfile.field

import java.util.Random

class Entier(name:String, val valmin:Int, val valmax:Int) extends Field(name) {
  
  
  def getRandomValue(r:Random):String = (r.nextInt(valmax-valmin)+valmin).toString()
}