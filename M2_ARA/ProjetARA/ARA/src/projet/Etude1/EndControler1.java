package projet.Etude1;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;


public class EndControler1 implements Control {

	private static final String PAR_PROTO_APPLICATIF="leader_protocol";
	
	private final int pid_application;
	
	private long time = 0;
	private int round = 0;
	private int nbMessages = 0;
	private CsvWriter csv = new CsvWriter("/home/richard/Eclipses/eclipse_2019_workspace/ARA/test.csv");
	
	public EndControler1(String prefix) {
		pid_application=Configuration.getPid(prefix+"."+PAR_PROTO_APPLICATIF);
		
	}
	
	@Override
	public boolean execute() {
		System.out.println("**************************** RESULTATS *******************************");
		for(int i=0;i<Network.size();i++){
			Node node =Network.get(i);
			Leader_Protocol protocol = (Leader_Protocol)node.getProtocol(pid_application);
			
			if(protocol.getProtocolMessage().getTime() > time && protocol.getProtocolMessage().getTime() < 100000000) time = protocol.getProtocolMessage().getTime();
			if(protocol.getRoundId() > round) round = protocol.getRoundId();
			nbMessages += protocol.getProtocolMessage().getNbMsg();
			
			System.out.println("NODE "+protocol.getId()+" : Leader => "+protocol.getLeader() + "; METRIQUES : " + "Time => "+protocol.getProtocolMessage().getTime()+"; Round => "+ protocol.getRoundId()+ "; Nombre de messages => "+ protocol.getProtocolMessage().getNbMsg());
		}
		
		System.out.println("######### TOTAL : Time => " +time+ "; Round => " +round+ "; Nombre de messages => "+nbMessages + " ###########");
		csv.write(time, round, nbMessages);
		return false;
	}

}