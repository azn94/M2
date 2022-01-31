package projet.util.Etude1;

public class Message1 {
	
	private final long idsrc;
	private final long iddest;
	
	public Message1(long idsrc, long iddest){
		this.iddest=iddest;
		this.idsrc=idsrc;
	}
		
	
	
	
	/** Message Prepare de l'étape 1a) */
	public static class PrepareMessage extends  Message1{
		private final int proposedId;
		
		public PrepareMessage(long idsrc, long iddest, int proposedId) {
			super(idsrc, iddest);
			this.proposedId=proposedId;
		}
		
		public int getRoundId() { return proposedId; }
	}
	
	/** Message Reject */
	public static class RejectMessage extends Message1{
		private int roundId;
	
		public RejectMessage(long idsrc, long iddest , int value) {
			super(idsrc, iddest);
			this.roundId=value;
		}
		
		public int getRoundId() { return roundId; }
	}
	
	/** Message Renvoi */
	public static class RenvoiMessage  {
		public RenvoiMessage() {}
	}
	
	

	/** Le message Promise de l'étape 1b) **/
	public static class PromiseMessage extends Message1{
		private int value;
		private final int roundId;
		
		public PromiseMessage(long idsrc, long iddest, int value, int roundId) {
			super(idsrc, iddest);
			this.value = value;
			this.roundId = roundId;
		}
		
		public int getValue() {return value;}
		public int getRoundId() {return roundId;}
	
	}
	
	
	/** Message Accept de l'étape 2a) */ 
	public static class AcceptMessage extends Message1{
		private final long val;
		private final int roundId;
		
		public AcceptMessage(long idsrc, long iddest, long val, int roundId) {
			super(idsrc, iddest);
			this.val = val;
			this.roundId=roundId;
		}
		
		public long getVal() {return val;}
		public int getRoundId() {return roundId;}
	}
	
	
	/** Message Accepted de l'étape 2b) */
	public static class AcceptedMessage extends Message1{
		private final long val;
		
		public AcceptedMessage(long idsrc, long iddest, long val) {
			super(idsrc, iddest);
			this.val = val;
		}
		
		public long getVal() {return val;}
		
	}
	
	
	/** Message de Fin : Leader enfin décidé (étape 3) */
	public static class LeaderFoundMessage extends Message1 {
		private final long leader;
		private final int round;
		
		public LeaderFoundMessage(long idsrc, long iddest, long leader, int round) {
			super(idsrc, iddest);
			this.leader = leader;
			this.round = round;
		}
		
		public long getLeader() {return leader;}
		public long getRound() {return round;}
	}
	
	
	
	/** Mécanisme Ping-Pong de surveillance du leader */
	public static class PingMessage extends Message1{
		public PingMessage(long idsrc, long iddest) {
			super(idsrc, iddest);
		}
	}
	public static class PongMessage extends Message1{
		public PongMessage(long idsrc, long iddest) {
			super(idsrc,iddest);
		}
	}
	
	
	
	public long getIdSrc() {return idsrc;}
	public long getIdDest() {return iddest;}



}
