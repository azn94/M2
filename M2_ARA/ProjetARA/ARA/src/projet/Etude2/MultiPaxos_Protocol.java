package projet.Etude2;

import java.util.*;
import peersim.config.Configuration;
import peersim.core.Fallible;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import projet.util.Etude1.ProtocolMessage;
import projet.util.Etude2.Historique;
import projet.util.Etude2.ProtocolRequest;
import projet.util.Etude1.Message1.*;
import projet.util.Etude2.Message2;
import projet.util.Etude2.ProtocolRequest.Requests;
import projet.util.Etude2.Message2.*;


public class MultiPaxos_Protocol implements EDProtocol{

	public static final int BACKOFF_TIME = 600 ;
	private Random rand = new Random();
	
	
	private static final String PAR_TRANSPORT = "transport";
	
	private final int transport_id;
	private int protocol_id; 
	private int roundId = 0;
	private int id;
	
	private int nbPromiseReceived = 0;
	private int nbAcceptedReceived = 0;
	private int nbRejectedReceived = 0;
	
	private final int quorum = (Network.getCapacity()/2)+1;
	private int leader;
	private boolean leaderFound = false;

	private ProtocolMessage ProtocolMessage ;
	
	private List<Integer> AcceptedReceived = new ArrayList<>();
	private HashMap<Integer, Integer> ProposedValuesReceived = new HashMap<>();
	
	/************************ NEW ********/
	
	private int requestRoundId = 0; 
	private ProtocolRequest ProtocolRequest; 

	private int waitTime = 0; /** pour le RejectMessage */
	private int requestWaitTime = 0; /** pour requête */
	private Request req = null;
	private boolean otherphase = false;

	private HashMap<Integer, Request> requestPromiseValues = new HashMap<>(); 
	private List<Request> requestAccepted = new ArrayList<>(); 

	

	public MultiPaxos_Protocol(String prefix) {
		transport_id = Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		String tmp[]=prefix.split("\\.");
		protocol_id=Configuration.lookupPid(tmp[tmp.length-1]);
	}
	
	@Override
	public Object clone() {
		MultiPaxos_Protocol n = null;
		try {n = (MultiPaxos_Protocol) super.clone();} 
		catch (CloneNotSupportedException e) {}
		return n;
	}
	
	public int getId() { return id; }
	public int getLeader() { return leader; }
	public int getRoundId() { return roundId; }
	
	public ProtocolMessage getProtocolMessage() { return this.ProtocolMessage; }
	public ProtocolRequest getProtocolRequest() { return ProtocolRequest; }
	
	
	/***************************************************************************************/
	@Override
	public void processEvent(Node node, int pid, Object event) {
		if(!leaderFound || (leaderFound && Network.get(leader).getFailState()!= Fallible.OK )) { /** On teste si on doit refaire une élection */
			leader = uniquePaxos(node, pid, event ,leader); /** Faire le mécanisme de ping pong plutôt */
		} else if(leader == id && !otherphase) {
			otherphase=true; 
			ProtocolRequest.envoiBeginReq(2000);
		}
		
		
		if(event instanceof RequestLater) {
			if(Requests.isEmpty()!=true) {
				req = Requests.get().get(0);
				/**System.err.println("[NODE : "+id+"]: va lancer une instance de paxos -> "+req); */
				sequentialRequest(node,req);
				if(Requests.remove_head()) ProtocolRequest.envoiLaterReq(10000 ,req);
			}
		}
		
		else if(event instanceof BeginRequest && !Requests.isEmpty()) {
			/**System.err.println("[NODE : "+id+"]:commence une Sequence ");*/
			if(req == null)
				req = Requests.get().get(0);
			sequentialRequest(node,req);
			if(Requests.remove_head()) ProtocolRequest.envoiLaterReq(10000 ,req);
		}
		
		else if(event instanceof ResetRequest) {
			ResetRequest msg = (ResetRequest) event;
			ProtocolRequest.envoiReady(Network.get((int)msg.getIdSrc()));
		}
		
		else if(event instanceof Message2) {
			/**System.err.println("[ NODE : "+id+" ]: RequestMessage spotted !"); */
			Message2 mess = (Message2) event;
			runSequentialPaxos(node, pid, event, mess.getRequest()); 
		}
	}
	
	
	public void sequentialRequest(Node node, Request r) {
		nbPromiseReceived = 0;
		nbAcceptedReceived = 0;
		nbRejectedReceived = 0;
		for (int i = 0; i < Network.size(); i++) {
			ProtocolRequest.envoiReset(Network.get(i));
			ProtocolRequest.setTime(System.currentTimeMillis());

			ProtocolRequest.envoiPrepareReq(Network.get(i), requestRoundId, r);
		}
	}
	
	
	/*********************************************************************************************************/
	
	
	/** Étape 1a : */
	public void findLeader(Node node) {	
		id = (int) node.getID();
		leader = id; 
		
		roundId =  id ; 

		ProtocolMessage = new ProtocolMessage(node,(Transport) node.getProtocol(transport_id),protocol_id);
		ProtocolRequest = new ProtocolRequest(node, (Transport) node.getProtocol(transport_id), protocol_id);
		
		Historique.addHistorique(id, new ArrayList<Request>()); /** On initialise l'historique pour id */
		for (int i = 0; i < Network.size(); i++) {   /** broadcast */
			ProtocolMessage.envoiPrepareMessage(Network.get(i), roundId);
			
		}
	}

	
	public int uniquePaxos(Node node, int pid, Object event , int value) /** Permet d'exécuter une itération de Paxos */
	{

		/** Étape 1b :  */
		if(event instanceof PrepareMessage ) {
			PrepareMessage msg = (PrepareMessage) event;
			 if(msg.getRoundId() > roundId){
				 roundId = msg.getRoundId();
				 
				 ProtocolMessage.envoiPromiseMessage(Network.get((int)msg.getIdSrc()), value, roundId);
			 }else {
				 
				 ProtocolMessage.envoiRejectMessage(Network.get((int)msg.getIdSrc()), value);
			} 
		}
		
		/** Message de rejet */
		else  if (event instanceof RejectMessage) {
			RejectMessage msgRej = (RejectMessage) event;
			value   = msgRej.getRoundId();

			nbRejectedReceived ++;
			ProtocolMessage.envoiRenvoiMessage(waitTime+rand.nextInt(200));
			waitTime += BACKOFF_TIME;
		}
		
		/** Renvoyer un message Prepare en cas de numéro de round invalide ou obsolète */
		else if (event instanceof RenvoiMessage ) {
				if(nbRejectedReceived ==  Network.getCapacity()) {
					roundId++;
					nbPromiseReceived = 0; 
					nbAcceptedReceived = 0; 
					nbRejectedReceived = 0;
					value = -1;
					for (int i = 0; i < Network.size(); i++) {
						ProtocolMessage.envoiPrepareMessage( Network.get(i), roundId);
					}
				}
			}
		
		
		/** Étape 2a : */
		else if (event instanceof PromiseMessage) {
			PromiseMessage msgPromise = (PromiseMessage) event;
			nbPromiseReceived++;
			ProposedValuesReceived.put(msgPromise.getRoundId(), msgPromise.getValue());

			if(nbPromiseReceived >= quorum) {
				if(ProposedValuesReceived.size()!=0) { 
					int max = -1;
					for (Map.Entry<Integer, Integer> entry : ProposedValuesReceived.entrySet()) { 
						if(entry.getKey() > max){
							max = entry.getKey();
						}
					}
					value = ProposedValuesReceived.get(max);;

				}else {
					value = id;
				}
				
				for (int i = 0; i < Network.size(); i++) 
					ProtocolMessage.envoiAcceptMessage(Network.get(i), value, roundId);
			}
		}
		
			
		/**  Étape 2b : */
		else if (event instanceof AcceptMessage) {
			AcceptMessage msg = (AcceptMessage) event;
			if(msg.getRoundId() >= roundId) {
				value = (int) msg.getVal();
				for (int i = 0; i < quorum; i++) {
					ProtocolMessage.envoiAcceptedMessage(Network.get(i),value);
				}
			}else {
				/** on ignore le message */
			}
		}
		
		
		
		/** Étape 3  : */
		else if (event instanceof AcceptedMessage) {
			AcceptedMessage msg = (AcceptedMessage) event;
	
			AcceptedReceived.add((int) msg.getVal());
			Set<Integer> liste = new HashSet<Integer>(AcceptedReceived); 
		
			for (Integer lead : liste) {
				nbAcceptedReceived = Collections.frequency(AcceptedReceived, lead);
				if(nbAcceptedReceived >= quorum) {
					value = lead;
					leaderFound = true;
					ProtocolMessage.broadcastLeader(value, roundId);
					
					nbPromiseReceived = 0; /** on réinitialise */
					nbAcceptedReceived = 0; 
					nbRejectedReceived = 0;
				}
			}
		}
		
		
		/** Fin : Leader trouvé */
		else if (event instanceof LeaderFoundMessage) {
			LeaderFoundMessage msg = (LeaderFoundMessage) event;
			leaderFound = true;
			value = (int) msg.getLeader();
	
			nbPromiseReceived = 0; /** On réinitialise */
			nbAcceptedReceived = 0;
			nbRejectedReceived = 0;
		}
		
		
		return value;
	}
	
	

	public boolean runSequentialPaxos(Node node, int pid, Object event ,Request value) { /** Idem que uniquePaxos mais avec les requêtes */
		
		if(value!= null && Historique.getHistorique(id).contains(value)) { return true; }

		
		else if(event instanceof PrepareRequest ) {
			PrepareRequest msg = (PrepareRequest) event;
			if(msg.getRoundId() >= requestRoundId){
				requestRoundId = msg.getRoundId();
				
				ProtocolRequest.envoiPromiseReq(Network.get((int)msg.getIdSrc()),requestRoundId, value );
			}else {
				ProtocolRequest.envoiRejectReq(Network.get((int)msg.getIdSrc()), value);
			} 
		}
		
		else  if (event instanceof RejectRequest) {
			RejectRequest msgRej = (RejectRequest) event;
			value = msgRej.getRequest();

			nbRejectedReceived ++;
			ProtocolRequest.renvoiReq(requestWaitTime, value);
			requestWaitTime += 50;
		}
		
		else if (event instanceof RunRequestAgain ) {
			RunRequestAgain msg = (RunRequestAgain) event;
			if(nbRejectedReceived >= quorum) {
				requestRoundId++;
				
				nbPromiseReceived = 0; 
				nbAcceptedReceived = 0;
				nbRejectedReceived = 0;
				value = msg.getRequest();

				for (int i = 0; i < Network.size(); i++) 
					ProtocolRequest.envoiPrepareReq(Network.get(i), requestRoundId, value);
			}
		}

		
		else if (event instanceof PromiseRequest) {
			PromiseRequest msgPromise = (PromiseRequest) event;
			nbPromiseReceived++;
			requestPromiseValues.put(msgPromise.getRoundId(), msgPromise.getRequest());

			if(nbPromiseReceived >= quorum) {
				if(requestPromiseValues.size()!=0) { 
					Integer max = -1;
					for (Map.Entry<Integer, Request> entry : requestPromiseValues.entrySet())
						if(entry.getKey() > max)
							max = entry.getKey();
					value = requestPromiseValues.get(max);
				}
				nbPromiseReceived = 0 ; 
				for (int i = 0; i < Network.size(); i++) 
					ProtocolRequest.envoiAcceptReq(Network.get(i), requestRoundId, value);
			}
		}
		

		else if (event instanceof AcceptRequest) {
			AcceptRequest msg = (AcceptRequest) event;

			if(msg.getRoundId() >= requestRoundId) {
				value = msg.getRequest();
				for (int i = 0; i < Network.size(); i++) {
					ProtocolRequest.envoiAcceptedReq(Network.get(i), value);
				}
			}else {

			}
		}

		else if (event instanceof AcceptedRequest) {
			AcceptedRequest msg = (AcceptedRequest) event;
			requestAccepted.add(msg.getRequest());

			Set<Request> requetes = new HashSet<Request>(requestAccepted); 
			for (Request r : requetes) {
				nbAcceptedReceived = Collections.frequency(requestAccepted, r);
				if(nbAcceptedReceived >= quorum) {
					List<Request> l = Historique.getHistorique(id);
					l.add(r);
					Historique.setHistorique(id, l);
					
					long time = ProtocolRequest.getTime();
					ProtocolRequest.setTime(System.currentTimeMillis()-time);
					long timer = ProtocolRequest.getavgtime() ;
					if (timer != 0)ProtocolRequest.setavgtime((ProtocolRequest.getTime()+timer )/2);
					
					ProtocolRequest.broadcastReq(requestRoundId, value);
					
					nbPromiseReceived = 0; /** On réinitialise */
					nbAcceptedReceived = 0;
					nbRejectedReceived = 0;
					requestAccepted = new ArrayList<>();	
					requestRoundId++ ;

				}
			}
		}
		

		return true;
	}
	
	
	
	
	


}