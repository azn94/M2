package projet.util.Etude2;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Fallible;
import peersim.core.Network;
import peersim.core.Node;
import peersim.transport.Transport;

public final class DeadlyTransport implements Transport {
	
	
	
	private static final String PAR_TRANSPORT = "transport";
	private static final String PAR_DROP = "drop";
	private static final String PAR_FAULTYNODES = "faultynodes";
	
	
	private final int transport;
	private final float loss;
	private final List<Long> faulty_ids;

	
	
	public DeadlyTransport(String prefix)
	{
		transport = Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		loss = (float) Configuration.getDouble(prefix+"."+PAR_DROP);
		faulty_ids = new ArrayList<Long>();
		String tmp = Configuration.getString(prefix+"."+PAR_FAULTYNODES);
		

		if ( tmp.equals("1")) 
		{
			for(int i=0; i<Network.size(); i++) { //parcours du tableau de Node
				//initiliser les noeuds suséptible de tomber en panne aleatoirement
				if (Math.random() < 0.5 && faulty_ids.size() < (Network.size()/2)-1)
				faulty_ids.add((long)i)	;	
			}
			}

	}
	
	@Override
	public Object clone()
	{
		Object res=null;
		try{
			res=super.clone();
		}catch( CloneNotSupportedException e ) {} // never happens
		return res;
	}

	
	@Override
	public void send(Node src, Node dest, Object msg, int pid) {
		try
		{
		
			if(src.getFailState() != Fallible.OK){
				return;
			}
			

			Transport t = (Transport) src.getProtocol(transport);
			t.send(src, dest, msg, pid);
			
			/* N’importe quel nœud du système peut tomber en panne n’importe quand
			 * une panne peut être définitive ou temporaire 
			 */
			if(faulty_ids.contains(src.getID()) &&  CommonState.r.nextFloat() <= loss && src.getFailState() != Fallible.DEAD ){
				// CHANGEMENT D'ETAT DU NODE SI NOT DEAD -> { OK, DOWN, DEAD }
				 Random random = new Random ();
				 switch(random.nextInt(3)) {
				 case 0 :  src.setFailState(Fallible.DEAD); ;	break ; 
				 case 1 :  src.setFailState(Fallible.DOWN);	;	break ;
				 case 2 :  src.setFailState(Fallible.OK); 	;  break ;
				 }
			}

			
		}
		catch(ClassCastException e)
		{
			throw new IllegalArgumentException("Protocol " +
					Configuration.lookupPid(transport) + " does not implement Transport");
		}
	}

	@Override
	public long getLatency(Node src, Node dest) {
		Transport t = (Transport) src.getProtocol(transport);
		return t.getLatency(src, dest);
	}

}