package datacloud.zookeeper.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import datacloud.zookeeper.membership.ClientMembership;

public class ZkClientMembershipTest extends ZookeeperTest{

	@Test
	public void test() throws Exception{
		List<ClientMembership> clients = new ArrayList<>();
		for(int i=0; i<10;i++) {
			clients.add(new ClientMembership("client",servers));
			List<String> expected = clients.stream().map(c->c.id()).collect(Collectors.toList());
			Collections.sort(expected);
			Thread.sleep(500);
			for(ClientMembership c : clients) {
				List<String> members = c.getMembers();
				Collections.sort(members);
				assertEquals(expected, members);
			}
		}
		
		for(int i=0; i<9;i++) {
			ClientMembership leaving = clients.remove(0);
			leaving.zk().close(500);
			List<String> expected = clients.stream().map(c->c.id()).collect(Collectors.toList());
			Collections.sort(expected);
			for(ClientMembership c : clients) {
				List<String> members = c.getMembers();
				Collections.sort(members);
				assertEquals(c.zid()+"",expected, members);
			}
		}
	}

}
