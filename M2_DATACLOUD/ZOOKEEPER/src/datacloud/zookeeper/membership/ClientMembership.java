package datacloud.zookeeper.membership;

import datacloud.zookeeper.ZkClient;
import datacloud.zookeeper.util.ConfConst;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientMembership extends ZkClient {

    private List<String> members;
    
    public ClientMembership(String name, String servers) throws IOException, KeeperException, InterruptedException {
        super(name, servers);
        members = new ArrayList<>();
        members = zk().getChildren(ConfConst.ZNODEIDS, true);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
    	try {
            members = zk().getChildren(ConfConst.ZNODEIDS, true);
        } catch (KeeperException | InterruptedException e) {}
    }

    public List<String> getMembers() {
    	
        return members;
    }
}