package datacloud.hbase.tests

import org.junit.Test
import datacloud.hbase.HbaseClient
import org.junit.Test
import datacloud.hbase.tests.HbaseTest._
import datacloud.hbase.HbaseClient
import org.junit.Assert._
import org.apache.hadoop.hbase.TableName

class HbaseClientCreateDeleteTest extends HbaseTest{
   
 
  def getNamesSpaces=connection.getAdmin.listNamespaces
  def getTables=connection.getAdmin.listTableNames
  def getCfNameOf(t:TableName)=connection.getAdmin.getDescriptor(t).getColumnFamilies.map(cfd=> cfd.getNameAsString)
  
  
  @Test
  def test():Unit={
    val client = new HbaseClient(connection)
    
    val ns1="ns1"
    val t1_1=TableName.valueOf(ns1,"t1")
    val t1_2=TableName.valueOf(ns1,"t2")
 
    val ns2="ns2"
    val t2_1=TableName.valueOf(ns2,"t1")
               
    client.deleteTable(t1_1)
    client.deleteTable(t1_2)
    client.deleteTable(t2_1)
    
    var namespaces = getNamesSpaces
    var tables = getTables
    
    assertFalse(namespaces.contains(ns1))
    assertFalse(namespaces.contains(ns2))
    assertFalse(tables.contains(t1_1))
    assertFalse(tables.contains(t1_2))
    assertFalse(tables.contains(t2_1))
    
    client.createTable(t1_1, "cf1","cf2")
    namespaces = getNamesSpaces
    tables = getTables
    assertTrue(namespaces.contains(ns1))
    assertFalse(namespaces.contains(ns2))
    assertTrue(tables.contains(t1_1))
    assertFalse(tables.contains(t1_2))
    assertFalse(tables.contains(t2_1))
    var cfnames = getCfNameOf(t1_1)
    assertEquals(2,cfnames.size)
    assertTrue(cfnames.contains("cf1"))
    assertTrue(cfnames.contains("cf2"))
    
    
    client.createTable(t1_2, "cf1","cf2","cf3")
    namespaces = getNamesSpaces
    tables = getTables
    assertTrue(namespaces.contains(ns1))
    assertFalse(namespaces.contains(ns2))
    assertTrue(tables.contains(t1_1))
    assertTrue(tables.contains(t1_2))
    assertFalse(tables.contains(t2_1))
    cfnames = getCfNameOf(t1_2)
    assertEquals(3,cfnames.size)
    assertTrue(cfnames.contains("cf1"))
    assertTrue(cfnames.contains("cf2"))
    assertTrue(cfnames.contains("cf3"))
    
    
    client.createTable(t2_1, "cf1")
    namespaces = getNamesSpaces
    tables = getTables
    assertTrue(namespaces.contains(ns1))
    assertTrue(namespaces.contains(ns2))
    assertTrue(tables.contains(t1_1))
    assertTrue(tables.contains(t1_2))
    assertTrue(tables.contains(t2_1))
    cfnames = getCfNameOf(t2_1)
    assertEquals(1,cfnames.size)
    assertTrue(cfnames.contains("cf1"))
        
    client.deleteTable(t1_2)
    namespaces = getNamesSpaces
    tables = getTables
    assertTrue(namespaces.contains(ns1))
    assertTrue(namespaces.contains(ns2))
    assertTrue(tables.contains(t1_1))
    assertFalse(tables.contains(t1_2))
    assertTrue(tables.contains(t2_1))
    
    client.deleteTable(t1_1)
    namespaces = getNamesSpaces
    tables = getTables
    assertFalse(namespaces.contains(ns1))
    assertTrue(namespaces.contains(ns2))
    assertFalse(tables.contains(t1_1))
    assertFalse(tables.contains(t1_2))
    assertTrue(tables.contains(t2_1))
    
    
    client.deleteTable(t2_1)
    namespaces = getNamesSpaces
    tables = getTables
    assertFalse(namespaces.contains(ns1))
    assertFalse(namespaces.contains(ns2))
    assertFalse(tables.contains(t1_1))
    assertFalse(tables.contains(t1_2))
    assertFalse(tables.contains(t2_1))
    
    
  }

  
  
}