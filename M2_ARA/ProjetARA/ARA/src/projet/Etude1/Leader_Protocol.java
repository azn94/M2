package projet.Etude1;

import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;

import java.util.Random;
import peersim.transport.Transport;
import projet.util.Etude1.Message1.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;



public class Leader_Protocol implements EDProtocol{
	
	/** Objectif : Quantifier l’impact des paramètres suivants sur les métriques décrites précédemment : **/
	public static final boolean BACKOFF = true	 ;// Objectif 2 : Mécanisme de backoff
	public static final int BACKOFF_TIME = 50 ; int waitingTime = 0; 
	private Random rand = new Random();
	public static final  boolean VALEUR_ROUND = false ;// Objectif 3 : true => roundId = 0 et false => roundId = id

	
	private static final String PAR_TRANSPORT = "transport";

	private final int transport_id;
	private int protocol_id; 
	private int roundId; private int id;
	
	private int nbPromiseReceived = 0;
	private int nbAcceptedReceived = 0;
	private int nbRejectedReceived = 0;	

	private List<Integer> AcceptedReceived = new ArrayList<Integer>();	// Valeurs acceptées par les Learners
	private Map<Integer, Integer> ProposedValuesReceived = new HashMap<Integer, Integer>(); // Sert à choisir le max en étape 2a)

	private final int quorum = (Network.size()/2)+1; 
	private int leader;
	private boolean leaderFound = false;

	private ProtocolMessage ProtocolMessage ; 
	
	List<Integer> quorumPromise = new ArrayList<Integer>();
	
	/*********************************************************************************/
	
	
	public Leader_Protocol(String prefix) throws FileNotFoundException, IOException {
		transport_id = Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		String tmp[]=prefix.split("\\.");
		protocol_id=Configuration.lookupPid(tmp[tmp.length-1]);
	}
	
	@Override
	public Object clone() {
		Leader_Protocol n = null;
		try {n = (Leader_Protocol) super.clone();
		} catch (CloneNotSupportedException e) {}
		return n;
	}
	
	
	public int getId() { return id; }
	public int getRoundId() { return roundId; }
	
	public int getLeader() { return leader; }

	public ProtocolMessage getProtocolMessage() { return ProtocolMessage; }



	/*Étape 1a : */
	public void findLeader(Node node) {	
		id = (int) node.getID();
		leader = id;
		
		/** Objectif 3 */
		if(VALEUR_ROUND) { 
			roundId = 0;
		} else { 
			roundId = id; 
		}
		
		//System.err.println("NODE PROPOSER : "+node.getID()+" : Broadcast un Prepare message pour le round "+roundId);

		ProtocolMessage = new ProtocolMessage(node,(Transport) node.getProtocol(transport_id),protocol_id);	
		for (int i = 0; i < Network.size(); i++) { /**Broadcast */
			ProtocolMessage.setTime(System.currentTimeMillis());
			ProtocolMessage.envoiPrepareMessage(Network.get(i), roundId);
		}
	}
	
	@Override
	public void processEvent(Node node, int pid, Object event) {

		if(leaderFound) return;
		
		
		/** Étape 1b : */
		else if(event instanceof PrepareMessage ) {
			PrepareMessage msg = (PrepareMessage) event;
			//System.err.println("[ACCEPTOR : "+id +"] Reception message Prepare <"+msg.getRoundId()+"> de  [PROPOSER : " + msg.getIdSrc()+"]");	
			 if(msg.getRoundId() > roundId){//round est superieur au dernier round accepté
				 roundId = msg.getRoundId();
				 //System.err.println("[ACCEPTOR : "+id +"] Envoie message Promise <"+roundId+","+leader+"> à [PROPOSER : " + msg.getIdSrc()+"]");
				 ProtocolMessage.envoiPromiseMessage(Network.get((int)msg.getIdSrc()), leader, roundId);
			 }else {//round refusé -> send message rejeté
				 //System.err.println("[ACCEPTOR : "+id +"] Envoie un msg Reject à [PROPOSER : " + msg.getIdSrc()+"]");
				 ProtocolMessage.envoiRejectMessage(Network.get((int)msg.getIdSrc()), leader);
				} 
			}


		/** Message de rejet */
		else  if (event instanceof RejectMessage) {
			RejectMessage msgRej = (RejectMessage) event;
			leader = msgRej.getRoundId();
			nbRejectedReceived ++;
			//System.err.println("[PROPOSER : "+id+"] Reception Reject Message avec numéro de round invalide  <"+roundId+">");
			
			ProtocolMessage.envoiRenvoiMessage(waitingTime+rand.nextInt(100));
			if(BACKOFF) {
				waitingTime += BACKOFF_TIME;
			}
		}
		
		/** Renvoyer un message Prepare en cas de numéro de round invalide ou obsolète */
		else if (event instanceof RenvoiMessage ) {
			if(nbRejectedReceived ==  Network.getCapacity()) {
				roundId++;
				//System.err.println("[PROPOSER : "+id+"] Proposition rejetée - Redemande élection <"+id+","+roundId+">");
				
				nbAcceptedReceived = 0;
				nbRejectedReceived = 0;
				nbPromiseReceived = 0;
				leader = id;
				for (int i = 0; i < quorum; i++) {
					//System.err.println(" [PROPOSER : "+id+"] diffuse un message prépare <" + roundId+">");
					long temp = ProtocolMessage.getTime();
					ProtocolMessage.setTime(System.currentTimeMillis()-temp);
					ProtocolMessage.envoiPrepareMessage( Network.get(i), roundId);
				}
			}
		}
		


		/** Étape 2a : */
		else if (event instanceof PromiseMessage) {
			PromiseMessage msgPromise = (PromiseMessage) event;
			nbPromiseReceived++;
			quorumPromise.add((int) msgPromise.getIdSrc());
			//System.err.println("[PROPOSER : "+id +"] reception message promise <"+msgPromise.getRoundId()+","+msgPromise.getValue()+"> de l'ACCEPTOR => " + msgPromise.getIdSrc());	
			ProposedValuesReceived.put(msgPromise.getRoundId(), msgPromise.getValue());
			if(nbPromiseReceived >= quorum) { 
				if(ProposedValuesReceived.size()!=0) { /** Trouver le max */
					int max = -1;
					for (Map.Entry<Integer, Integer> entry : ProposedValuesReceived.entrySet()) { 
						if(entry.getKey() > max){
							max = entry.getKey();
						}
					}
					leader = ProposedValuesReceived.get(max);
					
				}else {
					leader = id;
				}
	
				//System.err.println("[PROPOSER :"+id+"] diffuse un Accept à tous les Acceptor : <" + roundId +","+leader+">");
				for (int i = 0; i < quorum; i++) {
					ProtocolMessage.envoiAcceptMessage(Network.get(i), leader, roundId);
				}
			}
		}
		
		
		
		

		/**  Étape 2b : */
		else if (event instanceof AcceptMessage) {
			AcceptMessage msg = (AcceptMessage) event;
			//System.err.println("[ACCEPTOR - "+ id+"] reception message Accept de [ACCEPTOR : "+msg.getIdSrc()+"]");
			if(msg.getRoundId() >= roundId) {
				leader = (int) msg.getVal();
				//System.err.println("[ACCEPTOR : "+ id+"]Diffuse un Accepted message à tous les learners <" + leader +","+roundId+">");
				for (int i = 0; i < quorum; i++) {
					ProtocolMessage.envoiAcceptedMessage(Network.get(i),leader);
				}
			}else {
				//System.err.println("[ACCEPTOR : "+ id+"] Ignore le message accepted de de [ACCEPTOR : "+msg.getIdSrc()+"]");

			}
		}

		
		/** Étape 3  : **/
		else if (event instanceof AcceptedMessage) {
			AcceptedMessage msg = (AcceptedMessage) event;
			//System.err.println("[LEARNER : "+ id+"] Reception message Accepted value (Leader) de [ACCEPTOR : "+msg.getIdSrc()+"]");

			AcceptedReceived.add((int) msg.getVal());
			Set<Integer> liste = new HashSet<Integer>(AcceptedReceived); 
		
			for (Integer lead : liste) {
				nbAcceptedReceived = Collections.frequency(AcceptedReceived, lead);
				if(nbAcceptedReceived >= quorum) {
					leader = lead;
					leaderFound = true;
					//System.err.println("[LEARNER : "+id+"] THE END ! le leader est "+ leader);
					long temp = ProtocolMessage.getTime();
					ProtocolMessage.setTime(System.currentTimeMillis()-temp);
					ProtocolMessage.broadcastLeader(leader, roundId); 
				}
			}
		}

		
		
		/** Fin : Leader trouvé */
		else if (event instanceof LeaderFoundMessage) {
			LeaderFoundMessage msg = (LeaderFoundMessage) event;
			leaderFound = true;
			leader = (int) msg.getLeader();
			long temp = ProtocolMessage.getTime();
			ProtocolMessage.setTime(System.currentTimeMillis()-temp);
			//System.err.println("[LEARNER : "+msg.getIdDest()+"] THE END ! le leader est <"+leader+">");
		}
		
	}

	
	
	
	
	public static class ProtocolMessage {
		
		private Transport transport; private final Node node;
		private int protocol_Id;
		private int nb_mess = 0;
		private long time ;


		public ProtocolMessage(Node node , Transport tr , int protocol_Id) {
			this.transport = tr;
			this.node = node;
			this.protocol_Id = protocol_Id;
		}

		public void envoiAcceptMessage(Node dest , long value, int roundId) {
			AcceptMessage accept = new AcceptMessage(node.getID(), dest.getID(), value, roundId);
			transport.send(node, dest, accept, protocol_Id);
			nb_mess++;
		}

		public void envoiAcceptedMessage(Node dest , long value) {
			AcceptedMessage accepted = new AcceptedMessage(node.getID(), dest.getID(), value);
			transport.send(node, dest, accepted, protocol_Id);
			nb_mess++;
		}

		public void envoiPromiseMessage(Node dest , int value , int roundId) {
			PromiseMessage promise = new PromiseMessage(node.getID(), dest.getID(), value, roundId);
			transport.send(node, dest, promise, protocol_Id);
			nb_mess++;
		}

		public void envoiPrepareMessage(Node dest , int roundId) {
			PrepareMessage prepare = new PrepareMessage(node.getID(), dest.getID(),roundId);
			transport.send(node, dest, prepare, protocol_Id);
			nb_mess++;
		}

		public void envoiRejectMessage(Node dest , int idNode) {
			RejectMessage reject = new RejectMessage(node.getID(),dest.getID(), idNode);
			transport.send(node, dest, reject, protocol_Id);
			nb_mess++;
		}
		
		public void envoiRenvoiMessage(int temps) {
			EDSimulator.add(temps, new  RenvoiMessage(), node, protocol_Id);
		}

		public void broadcastLeader(int leader, int roundId) {
			for (int i = 0; i < Network.size(); i++) {
				LeaderFoundMessage lead = new LeaderFoundMessage(node.getID(), Network.get(i).getID(), leader, roundId);
				transport.send(node, Network.get(i), lead, protocol_Id);
				nb_mess++;
			}
		}


		public void setTransport(Transport tr) {this.transport = tr; }
		
		public long getTime() { return time; }
		public void setTime(long time) { this.time = time;}
		
		public int getNbMsg() {return nb_mess;}
		
	}
	
	
	
	


}