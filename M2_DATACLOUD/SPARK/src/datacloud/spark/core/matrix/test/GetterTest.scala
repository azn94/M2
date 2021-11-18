package datacloud.spark.core.matrix.test

import java.nio.file.Files

import org.junit.Assert.assertEquals
import org.junit.Test

import datacloud.spark.core.matrix.MatrixIntAsRDD.makeFromFile

class GetterTest extends AbstractTest{
  
  @Test
  def test():Unit={
    val sizes = List(8)
    for(nbl <- sizes){
      for(nbc<- sizes){
				val nb_partitions= Math.sqrt(nbl).ceil.toInt
				val file = Files.createTempFile("matrice", ".dat")
				val marray =  Util.createRandomMatrixInt(nbl, nbc, file)
				val matrdd = makeFromFile(file.toUri().toASCIIString(), nb_partitions, sc)
				
				assertEquals(nbc,matrdd.nbColumns)
				assertEquals(nbl,matrdd.nbLines)
				for(i<-0 until nbl){
				  for(j<-0 until nbc){
				    assertEquals(marray(i)(j),matrdd.get(i, j))
				  }
				}				
				file.toFile().delete()
			}
		}
    
  }
  
}