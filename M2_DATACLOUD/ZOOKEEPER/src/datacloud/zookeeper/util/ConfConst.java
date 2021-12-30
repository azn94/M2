package datacloud.zookeeper.util;

public final class ConfConst {

	private ConfConst() {}
	
	public static final int NB_SERVERS=3;

	public static final int TICKTIME=2000;
	public static final int INITLIMIT=1000;
	public static final int SYNCLIMIT=20;
	public static final String DATADIR_BASE=System.getProperty("java.io.tmpdir")+"/zookeeper";
	public static final int CLIENTPORT_BASE=2180;
	public static final int PORT1_BASE=2887;
	public static final int PORT2_BASE=3887;
	public static final String CONF_DIR=System.getProperty("java.io.tmpdir")+"/zookeeper_confs";
	public static final String NAME_CONFFILE_BASE="zooNode";
	
	
	public static final String ZNODEIDS="/ids";
	
	public static final int TIMEOUT_CLIENT = 3000;
	public static final byte[] EMPTY_CONTENT = "".getBytes(); 
	
}
