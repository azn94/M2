package datacloud.zookeeper.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import datacloud.zookeeper.pubsub.Publisher;
import datacloud.zookeeper.pubsub.Subscriber;

public class PubsubTest extends ZookeeperTest {

	@Test
	public void test() throws Exception {
		
		
		Publisher p1 = new Publisher("P1", servers);
		Publisher p2 = new Publisher("P2", servers);
				
		Subscriber s1 = new Subscriber("S1", servers);
		Subscriber s2 = new Subscriber("S2", servers);
			
		p1.publish("topic1", "1");
		p1.publish("topic2", "2");
		
		assertEquals(s1.received("topic1"),Collections.emptyList());
		assertEquals(s1.received("topic2"),Collections.emptyList());
		assertEquals(s1.received("topic3"),Collections.emptyList());
		assertEquals(s2.received("topic1"),Collections.emptyList());
		assertEquals(s2.received("topic2"),Collections.emptyList());
		assertEquals(s2.received("topic3"),Collections.emptyList());
						
		s1.subscribe("topic1");
		s2.subscribe("topic3");
		
		p1.publish("topic1", "3");
		p2.publish("topic3", "4");
		Thread.sleep(100);
		assertEquals(s1.received("topic1"),Arrays.asList("3"));
		assertEquals(s1.received("topic2"),Collections.emptyList());
		assertEquals(s1.received("topic3"),Collections.emptyList());
		assertEquals(s2.received("topic1"),Collections.emptyList());
		assertEquals(s2.received("topic2"),Collections.emptyList());
		assertEquals(s2.received("topic3"),Arrays.asList("4"));
		
		
		Subscriber s3 = new Subscriber("S3", servers);
		Subscriber s4 = new Subscriber("S4", servers);
		Subscriber s5 = new Subscriber("S5", servers);
		Subscriber s6 = new Subscriber("S6", servers);
		
		s1.subscribe("topic2");
		s2.subscribe("topic2");
		s3.subscribe("topic2");
		s4.subscribe("topic2");	
		s5.subscribe("topic2");
		s6.subscribe("topic2");	
		
		p1.publish("topic2", "5");
		p2.publish("topic2", "6");
		p1.publish("topic2", "7");
		p2.publish("topic2", "8");
		p1.publish("topic2", "9");
		p2.publish("topic2", "10");
		p1.publish("topic2", "11");
		p2.publish("topic2", "12");
			
		Thread.sleep(1000);
		
		for(Subscriber s : Arrays.asList(s1,s2,s3,s4,s5,s6)) {
			assertTrue(s.received("topic2").containsAll(Arrays.asList("5","6","7","8","9","10","11","12")));
			assertEquals(8,s.received("topic2").size());
		}
		
		Subscriber s7 = new Subscriber("S7", servers);
		s7.subscribe("topic2");
		assertEquals(s7.received("topic2"),Collections.emptyList());
		
	}

}
