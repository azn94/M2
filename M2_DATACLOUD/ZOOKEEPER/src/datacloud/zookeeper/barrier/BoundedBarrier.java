package datacloud.zookeeper.barrier;

import java.math.BigInteger;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;

import datacloud.zookeeper.ZkClient;
import datacloud.zookeeper.util.ConfConst;


public class BoundedBarrier implements Watcher{

	private ZkClient client;
	private String path;
	private int N; 	
	
	private  Object mutex = new Object();
	
	public BoundedBarrier(ZkClient client, String path, int N) throws KeeperException, InterruptedException {
		// TODO Auto-generated constructor stub
		this.client = client;
		this.path = path;
		
		if(client.zk().exists(path, true) == null) {
            client.zk().create(path, BigInteger.valueOf(N).toByteArray(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            this.N = N;
        }else {
        	this.N = new BigInteger(client.zk().getData(path, false, null)).intValue();
        }
	}

	public void sync() {
		// TODO Auto-generated method stub
		try {
			
			// Création des noeuds enfants
			String path_child = client.zk().create(path + "/" + client.id(), ConfConst.EMPTY_CONTENT, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		
			while (true) {
                synchronized (mutex) {
                    if (client.zk().getChildren(path, true).size() < N) {
                        mutex.wait();
                    }
                    break;
                    
                }
            }
			if(client.zk().exists(path_child, true) != null)
				client.zk().delete(path_child, 0);
			
			while(true) {
				synchronized(mutex) {
					if (client.zk().getChildren(path, true).size() > 0) {
                        return;
                    } else {
                    	if(client.zk().exists(path, true) != null)
                    		client.zk().delete(path, 0);
                    	return;
                    }
				}
			}
			
		} catch (KeeperException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				client.zk().delete(path + "/" + client.id(), 0);
				while(true) {
					synchronized(mutex) {
						if (client.zk().getChildren(path, true).size() > 0) {
	                        return;
	                    } else {
	                    	if(client.zk().exists(path, true) != null)
	                    		client.zk().delete(path, 0);
	                    	return;
	                    }
					}
				}
			} catch (InterruptedException | KeeperException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}
	
	
	public int sizeBarrier() {
		// TODO Auto-generated method stub
		return N;
	}

	@Override
	public void process(WatchedEvent e) {
		// TODO Auto-generated method stub
		synchronized(mutex) {
			System.out.println("on event: " + e.getPath() + " - " + e.getType() + " - " + e.getState());
			mutex.notify();
		}
			
	}
}
