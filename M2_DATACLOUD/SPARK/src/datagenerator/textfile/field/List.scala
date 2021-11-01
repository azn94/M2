package datagenerator.textfile.field

import java.util.Random

class List(name:String,separator:String, f:Field, sizemin:Int, sizemax:Int) extends Field(name) {
  
  def getRandomValue(r:Random):String={
  
    var size = sizemax 
    if(sizemin<sizemax)  
      size=r.nextInt(sizemax-sizemin)+sizemin
    var res=new StringBuilder
    if(size >= 1) res.append(f.getNewRandomValue(r))
    for(i <- 2 to size){
      res.append(separator+f.getNewRandomValue(r))
    }
    return res.toString()
    
  }
}