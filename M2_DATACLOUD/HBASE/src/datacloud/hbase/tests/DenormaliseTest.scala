package datacloud.hbase.tests

import java.util.Random

import org.junit.Assert._
import org.junit.Test

import datacloud.hbase.Stereoprix
import datacloud.hbase.tests.StereoPrixTest._
import datacloud.hbase.tests.HbaseTest._
import java.text.SimpleDateFormat
import java.util.Date
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DenormaliseTest extends StereoPrixTest{
  
  @Test
  def test_a={
    val expected = categories_map.mapValues(
                        cat => (
                            cat.designation,ventes_map.values.filter(v=> v.produit.categorie.idcat == cat.idcat).size                           
                            )
                        ).values.filter(_._2>0).toMap
   
    var start = System.currentTimeMillis()                    
    val computed_avant = Stereoprix.nbVenteParCategorie
    val time_avant = System.currentTimeMillis() - start
    assertEquals(ventes_map.size,computed_avant.map(_._2).reduce(_+_))
    assertEquals(expected,computed_avant)
    Stereoprix.denormalise
    start=System.currentTimeMillis()
    val computed_apres = Stereoprix.nbVenteParCategorieDenormalise
    val time_apres = System.currentTimeMillis() - start
   
    assertEquals(ventes_map.size,computed_apres.map(_._2).reduce(_+_))
    assertEquals(expected,computed_apres)
    
    println("temps sans denormalisation = "+time_avant)
    println("temps avec denormalisation = "+time_apres)
    assertTrue(time_avant>time_apres)
        
  }
    
  @Test
  def test_b={
    
    Stereoprix.denormalise
    
    val random = new Random(10)
    val idclientlist = clients_map.keySet.toList
    val idmaglist = magasins_map.keySet.toList
    val idprodlist = produits_map.keySet.toList
    
    (1.until(1000)).map(i => { 
      val idvente = "idvente"+ventes_map.size + i
      val idclient=idclientlist(random.nextInt(idclientlist.size))
      val idmag=idmaglist(random.nextInt(idmaglist.size))
      val idprod=idprodlist(random.nextInt(idprodlist.size))
      val date = new SimpleDateFormat("dd_MM_yy_HH_mm").format(new Date(System.currentTimeMillis()))
      
      val v = Vente(idvente,
          clients_map.get(idclient).get,
          magasins_map.get(idmag).get,
          produits_map.get(idprod).get,
          date
          )
      ventes_map.put(v.idvente, v)
      Stereoprix.addVente(connection,idvente,idclient,idmag,idprod,date)
    } )
    
    val expected = categories_map.mapValues(
                        cat => (
                            cat.designation,ventes_map.values.filter(v=> v.produit.categorie.idcat == cat.idcat).size                           
                            )
                        ).values.filter(_._2>0).toMap
   
       
    val computed_sans_denormal = Stereoprix.nbVenteParCategorie
    assertEquals(ventes_map.size,computed_sans_denormal.map(_._2).reduce(_+_))
    assertEquals(expected,computed_sans_denormal)
    
    val computed_avec_denormal = Stereoprix.nbVenteParCategorieDenormalise
    assertEquals(ventes_map.size,computed_avec_denormal.map(_._2).reduce(_+_))
    assertEquals(expected,computed_avec_denormal)
    
    
  }
  
  
}