package org.vanilladb.calvin.schedule;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.vanilladb.calvin.communication.object.StoredProcedureObject;
import org.vanilladb.calvin.server.CalvinServer;
import org.vanilladb.calvin.storeprocedure.CalvinStoredProcedure;
import org.vanilladb.core.server.VanillaDb;


// singleton
public class Scheduler implements Runnable{
	
	
	Queue<StoredProcedureObject> waitingSpos;
	
	public Scheduler(){
		this.waitingSpos = new LinkedBlockingQueue<StoredProcedureObject>();
	}
	public void schedule(StoredProcedureObject... spos) {
		for(int i = 0; i < spos.length; i++) {
			waitingSpos.add(spos[i]);
		}	
	}
	@Override
	public void run() {
		
		while(true) {
			// get store procedure object
			if(!waitingSpos.isEmpty()) {
				StoredProcedureObject nextSpo = waitingSpos.poll();			
				
				int clientId = nextSpo.getClientId();
				int procId = nextSpo.getProcId();				
				Object[] parms = nextSpo.getParms();
				long txNum = nextSpo.getTxNum();
				int masterId = nextSpo.getMasterId();	
				
				// prepare parameters for Calvin store procedure
				CalvinStoredProcedure<?> sp = CalvinServer.CSPFactory().getCalvinStroredProcedure(procId);
				sp.pasteClientId(clientId);
				sp.pasteTxNum(txNum);
				sp.pasteMasterId(masterId);
				

				// prepare parameters , add concurrency Mgr, do log and analyze
				sp.prepare(procId, parms);
				
				// start lock
				sp.startLock();
				
				// dispatch
				new Thread(sp).start();
			}

		}
	}
	
}