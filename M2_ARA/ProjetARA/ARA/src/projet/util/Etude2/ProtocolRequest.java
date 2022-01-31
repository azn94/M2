package projet.util.Etude2;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import projet.util.Etude2.Message2.*;


public class ProtocolRequest {          /** ENVOI DE REQUETES */
	
	private Transport transport; private final Node node;
	private int protocol_id;
	private int nbRequetes = 0;
	private long avgtime = 0 ;
	private long time = 0 ; 


	public ProtocolRequest(Node node , Transport transport , int protocol_id) {
		this.transport = transport;
		this.node = node;
		this.protocol_id = protocol_id;
	}
	
	

	public void envoiPrepareReq(Node destinataire , int roundId,Request seq) {
		transport.send(node, destinataire, new PrepareRequest(node.getID(),destinataire.getID(),roundId,seq), protocol_id);
		nbRequetes++;
	}
	
	public void envoiRejectReq(Node destinataire,Request seq) {
		transport.send(node, destinataire, new RejectRequest(node.getID(),destinataire.getID(),seq), protocol_id);
		nbRequetes++;
	}
	
	public void renvoiReq(int nbCycle, Request request) {
		EDSimulator.add(nbCycle,new RunRequestAgain(node.getID(),node.getID(),request) , node, protocol_id);
		nbRequetes++;
	}
	

	public void envoiPromiseReq(Node destinataire, int roundId,Request seq) {
		transport.send(node, destinataire, new PromiseRequest(node.getID(),destinataire.getID(),roundId,seq), protocol_id);
		nbRequetes++;
	}

	public void envoiAcceptReq(Node destinataire, int roundId,Request seq) {
		transport.send(node, destinataire,  new AcceptRequest(node.getID(),destinataire.getID(),roundId,seq), protocol_id);
		nbRequetes++;
	}

	public void envoiAcceptedReq(Node destinataire,Request seq) {
		transport.send(node, destinataire,  new AcceptedRequest(node.getID(),destinataire.getID(),seq), protocol_id);
		nbRequetes++;
	}
	
	public void broadcastReq(int roundId, Request r) {
		for (int i = 0; i < Network.size(); i++) {
			transport.send(node, Network.get(i), new FoundRequest(node.getID(), Network.get(i).getID(),roundId,r), protocol_id);
			nbRequetes++;

		}
	}
	
	
	
	public void envoiBeginReq(int delay) {
		EDSimulator.add(delay, new BeginRequest() , node, protocol_id);
	}
	public void envoiLaterReq(long delay , Request currentReq) {
		EDSimulator.add(delay,new RequestLater(currentReq) , node, protocol_id);
	}
	public void envoiReset(Node dest) {
		transport.send(node, dest, new ResetRequest(node.getID(),dest.getID()), protocol_id);
	}
	public void envoiReady(Node dest) {
		transport.send(node, dest, new ReadyRequest(node.getID(),dest.getID()), protocol_id);
	}
	

	
	public long getTime() { return time; }
	public void setTime(long time) { this.time = time; }
	public long getavgtime() { return avgtime; }
	public void setavgtime(long avgtime) { this.avgtime = avgtime; }

	public int nbReq() { return nbRequetes; }

	
	
	
	
	
	public static class Requests {
		private static List<Request> requests = new ArrayList<>();

		private Requests() {}

		public static void set(int len) {
			Random r = new Random();
			for(int i = 0 ;i<len;i++){
				if(r.nextBoolean())
					requests.add(new WriteRequest());
				else
					requests.add(new ReadRequest());
			}
		}
		
		public static boolean remove_head() {
			if(requests.size()!=0) {
				requests.remove(0);
				return true;
			}
			return false;
		}
		
		public static List<Request> get() { return requests; }
		public static boolean isEmpty() { return requests.isEmpty(); }
	}
	
	
}

