package projet.util.Etude2;

import java.util.Random;

import projet.util.Etude1.Message1;

public interface Message2 {       /** REQUETES MESSAGES */
	
	public Request getRequest();
	
	
	
	public class PrepareRequest extends Message1 implements Message2 {
			private Request req;
			private int roundId;
			
			public PrepareRequest(long idsrc, long iddest, int roundId , Request req) {
				super(idsrc, iddest);
				this.req=req;
				this.roundId=roundId;
			}
			public Request getSeq() { return req; }
			public int getRoundId() { return roundId; }
			@Override
			public Request getRequest() { return req; }
	}
	
	public class RejectRequest extends Message1 implements Message2 {
			private Request req;
		
			public RejectRequest(long idsrc, long iddest , Request req) {
				super(idsrc, iddest);
				this.req=req;
			}
			public Request getRequest() { return req; }
	}
	
	public class RunRequestAgain extends Message1 implements Message2 {
			private Request r;
			public RunRequestAgain(long idsrc, long iddest , Request r) {
				super(idsrc, iddest);
				this.r = r;
			}
			public Request getRequest() { return r; }
	}
	
	
	public class PromiseRequest extends Message1 implements Message2{
			private Request req;
			private int roundId;
			public PromiseRequest(long idsrc, long iddest , int roundId ,Request req) {
				super(idsrc, iddest);
				this.req=req;
				this.roundId=roundId;
			}
			public Request getRequest() {
				return req;
			}
			public int getRoundId() {
				return roundId;
			}
	}
	
	public class AcceptRequest  extends Message1 implements Message2{
			private Request req;
			private int roundId;
			public AcceptRequest(long idsrc, long iddest , int roundId , Request req) {
				super(idsrc, iddest);
				this.req=req;
				this.roundId=roundId;
			}
			public Request getRequest() {
				return req;
			}
			public int getRoundId() {
				return roundId;
			}
	}
	
	public class AcceptedRequest  extends Message1 implements Message2{
			private Request req;
	
			public AcceptedRequest(long idsrc, long iddest , Request req) {
				super(idsrc, iddest);
				this.req=req;
			}
			public Request getRequest() {
				return req;
			}
	
	}
	
	public class FoundRequest  extends Message1 implements Message2{
		private Request req;
		private int roundId;
		public FoundRequest(long idsrc, long iddest , int roundId , Request req) {
			super(idsrc, iddest);
			this.req=req;
			this.roundId=roundId;
		}
		
		public Request getRequest() {
			return req;
		}
		
		public int getRoundId() {
			return roundId;
		}
	}
	
	
	
	public abstract class Request implements Message2 {
		private static int cpt = new Random().nextInt(100);
		private final int id;
	
		public Request(){
			id = cpt;
			cpt = new Random().nextInt(100);
		}
		public long getID() {return id;}
		
		@Override
		public String toString() {
			return "Request [ id = " + id + " ]";
		}
	}
	
	public class ReadRequest extends Request{
		public ReadRequest() {}
	
		@Override
		public Request getRequest() { return null; }
	}
	public class WriteRequest extends Request {
		public WriteRequest() {}
	
		@Override
		public Request getRequest() { return null; }
	}
	
	
	
	
	public class BeginRequest implements Message2 {
		@Override
		public Request getRequest() { return null; }
	}
	public class RequestLater implements Message2 {
		private Request req;
		public RequestLater(Request req) {
			this.req = req;
		}
		public Request getRequest() { return req; }
	}
	public class ResetRequest extends Message1 implements Message2 {
		public ResetRequest(long idsrc, long iddest) {
			super(idsrc, iddest);
		}
		@Override
		public Request getRequest() { return null; }
	}
	public class ReadyRequest extends Message1 implements Message2 {
		public ReadyRequest(long idsrc, long iddest) {
			super(idsrc, iddest);
		}
		@Override
		public Request getRequest() { return null; }
	}

	
	
}

