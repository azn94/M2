package datacloud.hbase.tests

import org.apache.hadoop.hbase.TableName
import org.junit.Test
import org.junit.Assert._
import datacloud.hbase.HbaseClient
import datacloud.hbase.tests.HbaseTest._
import org.apache.hadoop.hbase.util.Bytes
import scala.util.Random
import scala.collection.mutable.HashMap
import org.apache.hadoop.hbase.client.Get
import scala.collection.JavaConverters._
import datacloud.hbase.Etudiant
import org.apache.hadoop.hbase.client.Scan



class HbaseClientReadWriteTest extends HbaseTest {

	implicit val etuToHbaseRow = Etudiant.toHbaseObject(_)
  implicit val hbaseRowToEtu = Etudiant.HbaseObjectToEtudiant(_)
	@Test
	def test={
			val random = new Random(20)
			val client:HbaseClient = new HbaseClient(connection)

			val tn = TableName.valueOf("test", "etudiants")
			client.deleteTable(tn)
			client.createTable(tn, "info","note")
			assertTrue(connection.getAdmin.listTableNames.contains(tn))
			val expected=new HashMap[Int,Etudiant]

			val max=20;
			var cpt:Int=0
			for(i<- 0 to max){
				val id = max-i
				val rowkey = Bytes.toBytes(id)
				val notes = (0 to (random.nextInt(5)+1)).map(idtopic => ("topic"+idtopic, random.nextInt(20))).toMap
				val e = Etudiant("nom"+id,"prenom"+id,18+random.nextInt(5),notes)
				expected.put(id, e)
				client.writeObject(tn, rowkey, e) 
			}
			
			
			
			val table = connection.getTable(tn)
			
			val scanner = table.getScanner(new Scan)
			assertEquals(max+1,scanner.asScala.size)
			
			table.exists((0 to max).map(i=> new Get(Bytes.toBytes(i))).asJava).foreach(assertTrue)

			val id_to_test = random.nextInt(max)
			val result = table.get(new Get(Bytes.toBytes(id_to_test)))
			val etudiant_to_test = expected.get(id_to_test).get
			assertEquals(etudiant_to_test.nom,Bytes.toString(result.getValue("info".getBytes, "nom".getBytes)))
			assertEquals(etudiant_to_test.prenom,Bytes.toString(result.getValue("info".getBytes, "prenom".getBytes)))
			assertEquals(etudiant_to_test.age,Bytes.toInt(result.getValue("info".getBytes, "age".getBytes)))
			result.getFamilyMap("notes".getBytes).asScala.foreach(c=>{
			  val topic = Bytes.toString(c._1)
			  val col = Bytes.toInt(c._2)
			  assertEquals(etudiant_to_test.notes.get(topic).get,col)
			})
						
			for(id<- 0 to max){
			  val etudiant = client.readObject[Etudiant](tn, Bytes.toBytes(id))
			  assertEquals(expected.get(id),etudiant)
			}

			client.deleteTable(tn)
			assertFalse(connection.getAdmin.listTableNames.contains(tn))

	}

}