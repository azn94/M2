package datacloud.scala.tpfonctionnel.test

import org.junit.Test
import org.junit.Assert._
import datacloud.scala.tpfonctionnel.Statistics._

class StatisticsTest {

  @Test
  def f=assertEquals(10.0, 
                    average(List( (10.0,1) , (10.0, 1), (20.0,2), (0.0,2), (8.0, 3), (12.0,3) )),
                    0.0)
}