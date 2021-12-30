package datacloud.zookeeper.pubsub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;

import datacloud.zookeeper.ZkClient;

public class Subscriber extends ZkClient {
	
	private Map<String, List<String>> myMap = new HashMap<>();
	
	public Subscriber(String name, String servers) throws IOException, KeeperException, InterruptedException {
		super(name, servers);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(WatchedEvent arg0) {
		// TODO Auto-generated method stub
		EventType type = arg0.getType();
		if((type.equals(Event.EventType.NodeDataChanged)) ||(type.equals(Event.EventType.NodeCreated))) {
			String[] tab = arg0.getPath().split("/");
			String topic = tab[tab.length-1];
			List<String> msg = myMap.get(topic);
			try {
				byte[] data = zk().getData(arg0.getPath(), true, new Stat());
				String message = new String(data);
				
				if (msg.contains(message) == false) {
					msg.add(message);
					myMap.put(topic, msg);
				}
			} catch (KeeperException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void subscribe(String topic) {
		// TODO Auto-generated method stub
		String root = "/topics";
		String path = root + "/" + topic;
		try {
			zk().exists(path, true);
			myMap.put(topic, new ArrayList<String>());
		} catch (KeeperException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> received(String topic) {
		// TODO Auto-generated method stub
		if(myMap.containsKey(topic))
			return myMap.get(topic);
		return Collections.emptyList();
	}

}
