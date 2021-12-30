package datacloud.hbase.tests

import datacloud.hbase.Stereoprix
import org.junit.Test
import org.junit.Assert._
import datacloud.hbase.tests.StereoPrixTest._

class NbVenteParCategorieTest extends StereoPrixTest {
  
  @Test
  def test_a={
    val expected = categories_map.mapValues(
                        cat => (
                            cat.designation,ventes_map.values.filter(v=> v.produit.categorie.idcat == cat.idcat).size                           
                            )
                        ).values.filter(_._2>0).toMap
    val computed = Stereoprix.nbVenteParCategorie
    assertEquals(ventes_map.size,computed.map(_._2).reduce(_+_))
    assertEquals(expected,computed)
       
  }
}