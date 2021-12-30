package datacloud.zookeeper.pubsub;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import datacloud.zookeeper.ZkClient;
import datacloud.zookeeper.util.ConfConst;

public class Publisher extends ZkClient{

	public Publisher(String name, String servers) throws IOException, KeeperException, InterruptedException {
		super(name, servers);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void process(WatchedEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void publish(String topic, String message) {
		// TODO Auto-generated method stub
		
		String root = "/topics";
		String path = root + "/" + topic;
		try {
			if(zk().exists(root, false) == null) {
				zk().create(root, ConfConst.EMPTY_CONTENT, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				
			}
			
			Stat state = zk().exists(path, false);
			
			if( state == null) {
				zk().create(path, message.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}else {
				zk().setData(path, message.getBytes(), state.getVersion());
			}
			
			
			} catch (KeeperException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
