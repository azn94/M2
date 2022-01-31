package projet.Etude2;


import java.util.List;
import java.util.Map;

import peersim.config.Configuration;
import peersim.core.*;
import projet.util.Etude2.Historique;
import projet.util.Etude2.Message2.*;

public class EndControler2  implements Control {

	private static final String PAR_PROTO_APPLICATIF="multipaxos_protocol";
	
	private final int pid_application;
	
	private long time = 0;
	private int nbMessages = 0;
	private CsvWriter2 csv = new CsvWriter2("/home/richard/Eclipses/eclipse_2019_workspace/ARA/test.csv");
	
	public EndControler2(String prefix) {
		pid_application=Configuration.getPid(prefix+"."+PAR_PROTO_APPLICATIF);
	}
	
	@Override
	public boolean execute() {
		
		System.out.println("################################# RESULTATS ###############################");
		for(int i=0;i<Network.size();i++){
			Node node = Network.get(i);
			MultiPaxos_Protocol protocol = (MultiPaxos_Protocol)node.getProtocol(pid_application);
			System.out.println("NODE "+protocol.getId()+" : Leader => "+protocol.getLeader() + "; METRIQUES : " + "Round => "+ protocol.getRoundId()+ "; Nombre de messages => "+ protocol.getProtocolMessage().getNbMsg() +" ("+ (node.getFailState()==Fallible.OK?"alive": (node.getFailState()==Fallible.DOWN?"down":"dead"))+")");
		}
		
		System.out.println("################################# HISTORIQUE NODES ###############################");
		for (Map.Entry<Integer, List<Request>> entry : Historique.getHistorique().entrySet() ) {
			Node node1 =Network.get(entry.getKey());
			MultiPaxos_Protocol protocol1 = (MultiPaxos_Protocol)node1.getProtocol(pid_application);
			
			if(protocol1.getProtocolRequest().getTime() > time && protocol1.getProtocolRequest().getTime() < 100000000) time = protocol1.getProtocolRequest().getTime();
			nbMessages += protocol1.getProtocolRequest().nbReq();
			
			System.out.println("NODE "+entry.getKey()+" : METRIQUES : Nombre de messages => "+protocol1.getProtocolRequest().nbReq()+ "; Time (latence moyenne) => "+protocol1.getProtocolRequest().getTime()+" ("+ (node1.getFailState()==Fallible.OK?"alive": (node1.getFailState()==Fallible.DOWN?"down":"dead"))+")");
			
			for(Request req : entry.getValue() ) {
				System.out.println("["+req.getID()+","+req.getClass().getSimpleName()+"]");
			}
		}
		System.out.println("######### TOTAL : Time => " +time+ "; Nombre de messages => "+nbMessages + " ###########");
		csv.write(time, nbMessages);	
		return false;
	}

}