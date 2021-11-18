package datagenerator.textfile.field

import java.util.Random



class Chaine(name:String, private[this] val nbmax:Int) extends Field(name) {
  
   def getRandomValue(r:Random):String = name+(r.nextInt(nbmax));
  
}