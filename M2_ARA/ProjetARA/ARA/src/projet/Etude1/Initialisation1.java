package projet.Etude1;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class Initialisation1 implements Control{

	public  Initialisation1(String prefix) {}
	
	@Override
	public boolean execute() {
		
		int applicative_pid=Configuration.lookupPid("leader_protocol");
		for(int i=0; i<Network.size(); i++) { //parcours du tableau de Node
			Node src = Network.get(i);
			System.err.println("Le CLIENT : "+src.getID()+" envoie une requête findLeader au PROPOSER : " + src.getIndex());
			Leader_Protocol node = (Leader_Protocol)src.getProtocol(applicative_pid);
			node.findLeader(src);
			
		}
		
		return false;
	}


}