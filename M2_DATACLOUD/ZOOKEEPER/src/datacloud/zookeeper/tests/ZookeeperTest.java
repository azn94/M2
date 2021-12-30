package datacloud.zookeeper.tests;

import static datacloud.zookeeper.util.ConfConst.CLIENTPORT_BASE;
import static datacloud.zookeeper.util.ConfConst.CONF_DIR;
import static datacloud.zookeeper.util.ConfConst.DATADIR_BASE;
import static datacloud.zookeeper.util.ConfConst.INITLIMIT;
import static datacloud.zookeeper.util.ConfConst.NAME_CONFFILE_BASE;
import static datacloud.zookeeper.util.ConfConst.NB_SERVERS;
import static datacloud.zookeeper.util.ConfConst.PORT1_BASE;
import static datacloud.zookeeper.util.ConfConst.PORT2_BASE;
import static datacloud.zookeeper.util.ConfConst.SYNCLIMIT;
import static datacloud.zookeeper.util.ConfConst.TICKTIME;

import java.io.File;
import java.io.PrintWriter;

import org.junit.BeforeClass;

public abstract class ZookeeperTest {
	
	static File conf_dir= new File(CONF_DIR);
	static String servers;
	
	@BeforeClass
	public static void deploy() throws Exception {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		      @Override
		      public void run() {
		    	 try {
					stopAll();
				} catch (Exception e) {
					e.printStackTrace();
				}
		      }
		    });
		stopAll();
		if(conf_dir.exists()) {
			new ProcessBuilder("rm","-r",conf_dir.getAbsolutePath()).start().waitFor();
		}
		conf_dir.mkdirs();
		StringBuilder sb_servers= new StringBuilder();
		
		for(int i=1;i<=NB_SERVERS;i++) {
			File conffile = new File(conf_dir,NAME_CONFFILE_BASE+""+i+".cfg");
			File datadir=new File(DATADIR_BASE+""+i);
			if(datadir.exists()) {
				new ProcessBuilder("rm","-r",datadir.getAbsolutePath()).start().waitFor();
			}
			datadir.mkdirs();
			try(PrintWriter out = new PrintWriter(conffile)){
				out.println("tickTime="+TICKTIME);
				out.println("initLimit="+INITLIMIT);
				out.println("syncLimit="+SYNCLIMIT);
				out.println("dataDir="+datadir.getAbsolutePath());
				int port_client=(CLIENTPORT_BASE+i);
				out.println("clientPort="+port_client);
				if(i!=1) sb_servers.append(",");
				sb_servers.append("localhost:"+port_client);
				for(int j=1;j<=NB_SERVERS;j++) {
					out.println("server."+j+"=localhost:"+(PORT1_BASE+j)+":"+(PORT2_BASE+j));
				}
				
			}
			try(PrintWriter out = new PrintWriter(new File(datadir, "myid"))){
				out.println(i+"");
			}
			
		}
		servers=sb_servers.toString();
		for(int i=1;i<=NB_SERVERS;i++) {
			File conffile = new File(conf_dir,NAME_CONFFILE_BASE+""+i+".cfg");
			ProcessBuilder pb = new ProcessBuilder("/home/richard/Documents/DATACLOUD/apache-zookeeper-3.6.3-bin/bin/zkServer.sh",
													"start",
													conffile.getAbsolutePath()).inheritIO(); 
			Process p =pb.start();
			p.waitFor();
			
			
		}
		for(int i=1;i<=NB_SERVERS;i++) {
			Process p;
			do {
				File conffile = new File(conf_dir,NAME_CONFFILE_BASE+""+i+".cfg");
				ProcessBuilder pb = new ProcessBuilder("/home/richard/Documents/DATACLOUD/apache-zookeeper-3.6.3-bin/bin/zkServer.sh",
														"status",
														conffile.getAbsolutePath()).inheritIO(); 
				p =pb.start();
			}while(p.waitFor() != 0);
		}
	}
	
	public static void stopAll() throws Exception {
		for(int i=1;i<=NB_SERVERS;i++) {
			File conffile = new File(conf_dir,NAME_CONFFILE_BASE+""+i+".cfg");
			
			if(new ProcessBuilder("/home/richard/Documents/DATACLOUD/apache-zookeeper-3.6.3-bin/bin/zkServer.sh","status",conffile.getAbsolutePath()).start().waitFor() !=0) continue;
			
			ProcessBuilder pb = new ProcessBuilder("/home/richard/Documents/DATACLOUD/apache-zookeeper-3.6.3-bin/bin/zkServer.sh",
													"stop",
													conffile.getAbsolutePath()).inheritIO(); 
			pb.start().waitFor();
		}
	}

}
