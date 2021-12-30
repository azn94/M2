package datacloud.zookeeper.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.junit.Test;

import datacloud.zookeeper.ZkClient;
import datacloud.zookeeper.barrier.BoundedBarrier;

public class BoundedBarrierTest extends ZookeeperTest {

	public static class ClientRunnable extends ZkClient implements Runnable{

		public boolean ok=false;
		private final BoundedBarrier barrier;

		public ClientRunnable(String name, String servers, String namebarrier, int size) throws IOException, KeeperException, InterruptedException {
			super(name, servers);
			this.barrier=new BoundedBarrier(this, namebarrier, size);
		}


		@Override
		public void process(WatchedEvent event) {
		}

		public BoundedBarrier getBarrier() {
			return barrier;
		}

		@Override
		public void run() {
			barrier.sync();
			ok=true;
		}
	}


	@Test
	public void test() throws Exception {

		int nb_clients=8;
		List<ClientRunnable> clients = new ArrayList<>();
		List<Thread> clients_thread = new ArrayList<>();
		String name_barrier1="/barrierbound";		
		for(int i = 0; i< nb_clients; i++) {
			ClientRunnable client = new ClientRunnable("client", servers, name_barrier1, nb_clients+i);
			clients.add(i, client);
			assertEquals(nb_clients, client.getBarrier().sizeBarrier());
			clients_thread.add(new Thread(clients.get(i)));
		}



		for(Thread t : clients_thread.subList(1, clients_thread.size())) {
			t.setDaemon(true);
			t.start();
		}
		for(ClientRunnable c : clients) {
			assertFalse(c.ok);
		}

		clients_thread.get(0).start();

		for(Thread t : clients_thread) {
			t.join(500);
		}
		
		for(ClientRunnable c : clients) {
			assertTrue(c.id(),c.ok);
			
		}

		assertNull(clients.get(0).zk().exists(name_barrier1, false));
		System.out.println("fin test 1");

	}


	@Test
	public void test2() throws Exception {

		String name_barrier="/barrierbound2";		
		ClientRunnable cr1 = new ClientRunnable("client", servers, name_barrier, 3);
		ClientRunnable cr2 = new ClientRunnable("client", servers, name_barrier, 3);
		ClientRunnable cr3 = new ClientRunnable("client", servers, name_barrier, 3);
		ClientRunnable cr4 = new ClientRunnable("client", servers, name_barrier, 3);

		Thread t1 = new Thread(cr1); t1.setDaemon(true);
		Thread t2 = new Thread(cr2); t2.setDaemon(true);
		Thread t3 = new Thread(cr3); t3.setDaemon(true);
		Thread t4 = new Thread(cr4); t4.setDaemon(true);

		t1.start();
		Thread.sleep(200);
		assertFalse(cr1.ok);
		assertFalse(cr2.ok);
		assertFalse(cr3.ok);
		assertFalse(cr4.ok);

		t2.start();
		Thread.sleep(200);
		assertFalse(cr1.ok);
		assertFalse(cr2.ok);
		assertFalse(cr3.ok);
		assertFalse(cr4.ok);

		t2.interrupt();
		cr2.zk().close();

		t3.start();
		Thread.sleep(500);
		assertFalse(cr1.ok);
		assertTrue(cr2.ok);
		assertFalse(cr3.ok);
		assertFalse(cr4.ok);


		t4.start();
		Thread.sleep(1500);
		assertTrue(cr1.ok);
		assertTrue(cr2.ok);
		assertTrue(cr3.ok);
		assertTrue(cr4.ok);


		assertNull(cr1.zk().exists(name_barrier, false));

	}


}
