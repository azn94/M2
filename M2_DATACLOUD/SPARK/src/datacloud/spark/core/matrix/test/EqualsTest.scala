package datacloud.spark.core.matrix.test

import java.io.FileOutputStream
import java.nio.file.Files

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

import datacloud.spark.core.matrix.MatrixIntAsRDD.makeFromFile

class EqualsTest extends AbstractTest{
  @Test
  def test():Unit={
    val sizes = List(5,15,50)
    for(nbl <- sizes){
      for(nbc<- sizes){
				val nb_partitions= Math.sqrt(nbl).ceil.toInt
				
				val file = Files.createTempFile("matrice", ".dat")
				val marray =  Util.createRandomMatrixInt(nbl, nbc, file)
				val matrdd = makeFromFile(file.toUri().toASCIIString(), nb_partitions, sc)
				
				val filecopy = Files.createTempFile("matrice", ".dat")
				val fos = new FileOutputStream(filecopy.toFile())
				Files.copy(file, fos )
				fos.close()
				val matrddcopy = makeFromFile(filecopy.toUri().toASCIIString(), nb_partitions, sc)
				
				assertNotEquals("Bonjour",matrddcopy)
				assertNotEquals(3,matrdd)
				assertEquals(matrdd,matrddcopy)
				
				filecopy.toFile().delete()
				file.toFile().delete()
			}
		}
    
  }
}