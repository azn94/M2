package datacloud.zookeeper;

import static datacloud.zookeeper.util.ConfConst.*;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public abstract class ZkClient implements Watcher {

	private final String zid;
	private final String id;
	private final ZooKeeper zk;
		
	public ZkClient(String name, String servers) throws IOException, KeeperException, InterruptedException {
		zk = new ZooKeeper(servers, TIMEOUT_CLIENT, this);
		if(zk.exists(ZNODEIDS, false) == null) {
			zk.create(ZNODEIDS, EMPTY_CONTENT, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		zid=zk.create(ZNODEIDS+"/"+name, EMPTY_CONTENT, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		String[] tmp  = zid.split("/");
		id=tmp[tmp.length-1];
	}
	
	public String id() {
		return id;
	 }
	public String zid() {return  zid;}
	
	public ZooKeeper zk() {return  zk;}
	

}
