package datacloud.spark.core.matrix.test

import java.nio.file.Files

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

import datacloud.scala.tpobject.vector.VectorInt
import datacloud.spark.core.matrix.MatrixIntAsRDD
import datacloud.spark.core.matrix.MatrixIntAsRDD._

class MakingTest extends AbstractTest {
    
      @Test
      def testConversion:Unit={
      
      		val list = List(new VectorInt(Array(0,1,2)),
      				new VectorInt(Array(3,4,5)),
      				new VectorInt(Array(6,7,8)))
      				val matrix:MatrixIntAsRDD = sc.parallelize(list, 2)
      				matrix.toString() 
      				val vectors = matrix.lines.collect()
      				for(i <- 0 until 9)
      					assertEquals(i,vectors(i/3).get(i%3)) 
      }
      
      @Test
      def testmaking:Unit={
          val sizes = List(4,10,50,500,1500)
      		for(nbl <- sizes){
      			for(nbc<- sizes){
      				val nb_partitions= Math.sqrt(nbl).ceil.toInt
      				val file1 = Files.createTempFile("matrice", ".dat")
      				val marray1 =  Util.createRandomMatrixInt(nbl, nbc, file1 )
      				val matrdd1 = makeFromFile(file1.toUri().toASCIIString(), nb_partitions, sc)
       				val file2 = Files.createTempFile("matrice", ".dat")
      				val marray2 =  Util.createRandomMatrixInt(nbl, nbc, file2 )
      				val matrdd2 = makeFromFile(file2.toUri().toASCIIString(), nb_partitions, sc)
      				assertEquals(nb_partitions, matrdd1.lines.partitions.length)
      				assertEquals(nb_partitions, matrdd2.lines.partitions.length)
      
      				val array = matrdd1.lines.zip(matrdd2.lines).collect()
      
      				for(i <- 0 until array.length ){
      					assertArrayEquals(marray1(i), array(i)._1.elements)
      					assertArrayEquals(marray2(i), array(i)._2.elements)
      				}
      				
      				file1.toFile().delete()
      				file2.toFile().delete()
      			}
      		}
      }
}