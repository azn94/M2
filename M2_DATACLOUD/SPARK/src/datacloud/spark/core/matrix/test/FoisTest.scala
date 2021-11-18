package datacloud.spark.core.matrix.test

import java.io.FileWriter
import java.io.PrintWriter
import java.nio.file.Files

import org.junit.Assert._
import org.junit.Test

import datacloud.spark.core.matrix.MatrixIntAsRDD
import datacloud.spark.core.matrix.MatrixIntAsRDD._

class FoisTest extends AbstractTest {
  
  @Test
      def testmaking:Unit={
          val sizes = List(4)
      		for(nb <- sizes){
      				val nb_partitions= Math.sqrt(nb).ceil.toInt
      				val file1 = Files.createTempFile("matrice", ".dat")
      				val marray1 =  Util.createRandomMatrixInt(nb, nb, file1 )
      				val matrdd1 = makeFromFile(file1.toUri().toASCIIString(), nb_partitions, sc)
       				val file2 = Files.createTempFile("matrice", ".dat")
      				val marray2 =  Util.createRandomMatrixInt(nb, nb, file2 )
      				val matrdd2 = makeFromFile(file2.toUri().toASCIIString(), nb_partitions, sc)
      				
      				val matfois = matrdd1*matrdd2
      				val file_expected = Files.createTempFile("matrice", ".dat")
      				val w= new PrintWriter(new FileWriter(file_expected.toFile()))
              for(i<-0 until nb){
                for(j <- 0 until nb){
                  if(j>0) w.print(" ")
                  w.print(prod(i,j,marray1,marray2))
                }
                if(i < nb-1)w.println("")
              }   
              w.close
      				val mat_expected = makeFromFile(file_expected.toUri().toASCIIString(), nb_partitions, sc)
      				
      				assertEquals(mat_expected,matfois)
      				file1.toFile().delete()
      				file2.toFile().delete()
      				file_expected.toFile().delete()
      		}
      }
  
      def prod(line:Int,col:Int,mat1:Array[Array[Int]], mat2:Array[Array[Int]]):Int={
        var res=0
        for(i<- 0 until mat1.length){
          res = res + mat1(line)(i)*mat2(i)(col)
        }
        return res
      }
  
}