package org.vanilladb.calvin.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.vanilladb.calvin.cache.CacheMgr;
import org.vanilladb.calvin.communication.server.ConnectionMgr;
import org.vanilladb.calvin.metadata.MetadataMgr;
import org.vanilladb.calvin.schedule.Scheduler;
import org.vanilladb.calvin.storeprocedure.CalvinStoredProcedureFactory;
import org.vanilladb.calvin.tx.recovery.RecoveryMgr;
import org.vanilladb.core.server.VanillaDb;
import org.vanilladb.core.sql.storedprocedure.StoredProcedureFactory;


public class CalvinServer extends VanillaDb {
	private static int serverId;
	//private static String dir;
	
	
	
	
	// singleton
	private static RecoveryMgr recoveryMgr;
	private static MetadataMgr metadataMgr;
	private static CacheMgr cacheMgr;
	private static ConnectionMgr connectionMgr;
	private static Scheduler scheduler;
	private static CalvinStoredProcedureFactory cspFactory;
	

	// Logger
	public static void init(String dirName, int sid, CalvinStoredProcedureFactory factory) {
		
		serverId = sid;
		cspFactory = factory;
		
		
		// init manger for calvin use()
		initMetadataMgr();
		initCacheMgr();
		initConnectionMgr();
		initScheduler();
		
		// init vanilla core
		VanillaDb.init(dirName);
	};
	
	
	private static void initMetadataMgr(){
		metadataMgr = new MetadataMgr();
	}
	private static void initCacheMgr(){
		cacheMgr = new CacheMgr();
	}
	private static void initConnectionMgr() {
		connectionMgr = new ConnectionMgr(serverId);
	}
	private static void initScheduler(){
		scheduler = new Scheduler();
		new Thread(scheduler).start();
	}

	public static MetadataMgr getMetadataMgr() {
		return metadataMgr;
	}
	public static CacheMgr getCacheMgr() {
		return cacheMgr;
	}
	public static ConnectionMgr getConnectionMgr() {
		return connectionMgr;
	}
	public static Scheduler getScheduler() {
		return scheduler;
	}
	public static CalvinStoredProcedureFactory CSPFactory() {
		return cspFactory;
	}
	public static int serverId() {
		return serverId;
	}

}
