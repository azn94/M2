package datacloud.zookeeper.barrier;
	
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;


import datacloud.zookeeper.ZkClient;
import datacloud.zookeeper.util.ConfConst;

public class SimpleBarrier {
	
	private ZkClient client;
	private String path;
	
	public SimpleBarrier(ZkClient client, String path) throws KeeperException, InterruptedException {
		// TODO Auto-generated constructor stub
		this.client = client;
		this.path = path;
		
		if(client.zk().exists(path, false) == null) {
            client.zk().create(path, ConfConst.EMPTY_CONTENT,ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        }
		
		
	}

	public void sync(){
		// TODO Auto-generated method stub
		try {
			while(this.client.zk().exists(path, false) != null) {
			}
		} catch (KeeperException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
