package datacloud.zookeeper.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.junit.Test;

import datacloud.zookeeper.ZkClient;

public class ZkClientTest extends ZookeeperTest{

	
	
	
	@Test
	public void test() throws Exception{
		ZkClient c1 = new DummyClient("a",servers);
		assertEquals("a0000000000",c1.id());
		assertEquals("/ids/"+c1.id(),c1.zid());	
		ZkClient c2 = new DummyClient("a",servers);
		assertEquals("a0000000001",c2.id());
		assertEquals("/ids/"+c2.id(),c2.zid());
		ZkClient c3 = new DummyClient("bonjour",servers);
		assertEquals("bonjour0000000002",c3.id());
		assertEquals("/ids/"+c3.id(),c3.zid());
		
		c1.zk().close(1000);
		assertNull(c2.zk().exists(c1.zid(), false));
		
		
	}

	
	public static class DummyClient extends ZkClient{
		
		public DummyClient(String name, String servers) throws IOException, KeeperException, InterruptedException {
			super(name, servers);
		}

		@Override
		public void process(WatchedEvent event) {}
		
	}
}
