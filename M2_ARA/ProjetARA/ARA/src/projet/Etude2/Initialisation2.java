package projet.Etude2;


import peersim.config.Configuration;
import peersim.core.* ;
import projet.util.Etude2.ProtocolRequest.Requests;

public class Initialisation2 implements Control{

	public static final int NB_REQUETES = 10 ;
	
	public  Initialisation2(String prefix) {}
	
	@Override
	public boolean execute() {
		
		int applicative_pid = Configuration.lookupPid("multipaxos_protocol");
		Requests.set(NB_REQUETES); 
		
		for(int i=0; i<Network.size(); i++) {
			Node src = Network.get(i);
			MultiPaxos_Protocol node = (MultiPaxos_Protocol)src.getProtocol(applicative_pid);
			node.findLeader(src);	
		}
		
		return false;
	}

	

}