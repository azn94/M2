package datacloud.spark.core.matrix.test

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.junit.After
import org.junit.Before

abstract class AbstractTest {
    var sc:SparkContext= null
		@Before
		def before:Unit={
				sc= new SparkContext(new SparkConf().setAppName("Matrix").setMaster("local[*]"))
    }

    @After
    def after:Unit={
    		sc.stop()
    }
}