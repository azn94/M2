package datacloud.spark.core.matrix.test

import scala.util.Random
import java.io.PrintWriter
import java.io.FileWriter
import java.nio.file.Path

object Util {
  val rand=new Random(20)
  
  def pullInt(min:Int=(-99),max:Int=99):Int=rand.nextInt(max-min) +min
  
  def createRandomMatrixInt(nbl:Int,nbc:Int,fileout:Path):Array[Array[Int]]={
    
    val res = Array.ofDim[Int](nbl, nbc)
    val w= new PrintWriter(new FileWriter(fileout.toFile()))
    for(i<-0 until nbl){
      for(j <- 0 until nbc){
        res(i)(j)= pullInt()
        if(j>0) w.print(" ")
        w.print(res(i)(j))
      }
      if(i < nbl-1)w.println("")
    }   
    w.close
    return res
  }
  
}