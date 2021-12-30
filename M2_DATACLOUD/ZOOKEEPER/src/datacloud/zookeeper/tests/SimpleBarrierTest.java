package datacloud.zookeeper.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.junit.Test;

import datacloud.zookeeper.ZkClient;
import datacloud.zookeeper.barrier.SimpleBarrier;

public class SimpleBarrierTest extends ZookeeperTest{

	public static class ClientRunnable extends ZkClient implements Runnable{
		
		public boolean ok=false;
		private final SimpleBarrier barrier;
		
		public ClientRunnable(String name, String servers, String namebarrier) throws IOException, KeeperException, InterruptedException {
			super(name, servers);
			this.barrier=new SimpleBarrier(this, namebarrier);
		}
	

		@Override
		public void process(WatchedEvent event) {
		}

		@Override
		public void run() {
			barrier.sync();
			ok=true;
		}
		
		
		
	}
	
	
	@Test
	public void test() throws Exception {
		
		int nb_clients=10;
		List<ClientRunnable> clients = new ArrayList<>();
		List<Thread> clients_thread = new ArrayList<>();
		String name_barrier1="/barrier1";		
		for(int i = 0; i< nb_clients; i++) {
			clients.add(i, new ClientRunnable("client", servers, name_barrier1));
			clients_thread.add(new Thread(clients.get(i)));
		}
		for(Thread t : clients_thread) {
			t.setDaemon(true);
			t.start();
		}
		for(ClientRunnable c : clients) {
			assertFalse(c.ok);
		}
		
		clients.get(0).zk().delete(name_barrier1, 0);
		Thread.sleep(500);
		for(ClientRunnable c : clients) {
			assertTrue(c.ok);
		}
		
		
		
	}

}
