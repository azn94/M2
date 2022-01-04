package datacloud.zookeeper.barrier;

import java.math.BigInteger;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;

import datacloud.zookeeper.ZkClient;
import datacloud.zookeeper.util.ConfConst;

public class BoundedBarrier {

	private static ZkClient client;
	private String path;
	private int N;
	
	public BoundedBarrier(ZkClient client, String path, int N) throws KeeperException, InterruptedException {
		// TODO Auto-generated constructor stub
		BoundedBarrier.client = client;
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
		String path_child;
		try {
			synchronized(client) {
				path_child = client.zk().create(path + "/", ConfConst.EMPTY_CONTENT,ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
				if(client.zk().getChildren(path, false).size() < N) {
					try {
						client.wait();
					}catch (InterruptedException e1) { // pour cr2
						client.zk().delete(path_child, -1);
						if (client.zk().getChildren(path, false).size() == 0) {
							client.zk().delete(path, -1);
						}
						return; 
					}
				}else {
					client.notifyAll();
				}
				client.zk().delete(path_child, -1);
				if(client.zk().getChildren(path, false).size() == 0) {
					client.zk().delete(path, -1);
				}
			}
			
		} catch (KeeperException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int sizeBarrier() {
		// TODO Auto-generated method stub
		return N;
	}

}

/*

Tentative sans static ZkClient non abouti

*/


/*
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
                synchronized (this) {
                    if (client.zk().getChildren(path, true).size() < N) {
                        this.wait();
                    }
                    break;
                    
                }
            }
			if(client.zk().exists(path_child, true) != null)
				client.zk().delete(path_child, 0);
			
			while(true) {
				synchronized(this) {
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
					synchronized(this) {
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
		synchronized(this) {
			System.out.println("on event: " + e.getPath() + " - " + e.getType() + " - " + e.getState());
			this.notify();
		}
			
	}
}*/
