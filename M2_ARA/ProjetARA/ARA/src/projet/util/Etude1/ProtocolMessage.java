package projet.util.Etude1;

import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import projet.util.Etude1.Message1.*;


public class ProtocolMessage {
	
	private Transport transport; private final Node node;
	private int protocol_id;
	private int nb_mess = 0;
	private long time ;


	public ProtocolMessage(Node node , Transport tr , int protocol_id) {
		this.transport = tr;
		this.node = node;
		this.protocol_id = protocol_id;
	}
	
	
	public void envoiPrepareMessage(Node dest , int roundId) {
		PrepareMessage prepare = new PrepareMessage(node.getID(), dest.getID(),roundId);
		transport.send(node, dest, prepare, protocol_id);
		nb_mess++;
	}

	public void envoiRejectMessage(Node dest , int idNode) {
		RejectMessage reject = new RejectMessage(node.getID(),dest.getID(), idNode);
		transport.send(node, dest, reject, protocol_id);
		nb_mess++;
	}
	
	public void envoiRenvoiMessage(int temps) {
		EDSimulator.add(temps, new  RenvoiMessage(), node, protocol_id);
	}
	
	
	public void envoiPromiseMessage(Node dest , int value , int roundId) {
		PromiseMessage promise = new PromiseMessage(node.getID(), dest.getID(), value, roundId);
		transport.send(node, dest, promise, protocol_id);
		nb_mess++;
	}

	public void envoiAcceptMessage(Node dest , long value, int roundId) {
		AcceptMessage accept = new AcceptMessage(node.getID(), dest.getID(), value, roundId);
		transport.send(node, dest, accept, protocol_id);
		nb_mess++;
	}

	public void envoiAcceptedMessage(Node dest , long value) {
		AcceptedMessage accepted = new AcceptedMessage(node.getID(), dest.getID(), value);
		transport.send(node, dest, accepted, protocol_id);
		nb_mess++;
	}


	public void broadcastLeader(int leader, int roundId) {
		for (int i = 0; i < Network.size(); i++) {
			LeaderFoundMessage lead = new LeaderFoundMessage(node.getID(), Network.get(i).getID(), leader, roundId);
			transport.send(node, Network.get(i), lead, protocol_id);
			nb_mess++;
		}
	}
	
	
	public void sendPingMessage(Node destinataire) {
		transport.send(node, destinataire, new PingMessage(node.getID(), destinataire.getID()), protocol_id);
	}

	public void sendPongMessage(Node destinataire) {
		transport.send(node, destinataire, new PongMessage(node.getID(), destinataire.getID()), protocol_id);
	}


	
	public long getTime() { return time; }
	public void setTime(long time) { this.time = time;}
	
	public int getNbMsg() {return nb_mess;}
	
}