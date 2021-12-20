package org.vanilladb.calvin.storeprocedure;

import java.sql.Connection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.vanilladb.calvin.communication.server.ConnectionMgr;
import org.vanilladb.calvin.metadata.MetadataMgr;
import org.vanilladb.calvin.server.CalvinServer;
import org.vanilladb.calvin.sql.CalvinRecord;
import org.vanilladb.calvin.tx.concurrency.ConservativeLockingMgr;
import org.vanilladb.calvin.tx.recovery.RecoveryMgr;
import org.vanilladb.core.remote.storedprocedure.SpResultSet;
import org.vanilladb.core.server.VanillaDb;
import org.vanilladb.core.sql.storedprocedure.ManuallyAbortException;
import org.vanilladb.core.sql.storedprocedure.SpResultRecord;
import org.vanilladb.core.storage.tx.Transaction;

	
public abstract class CalvinStoredProcedure<H extends CalvinStoredProcedureParamHelper> implements Runnable{
	
	private int clientId;
	private int serverId; 
	//private static Logger logger = Logger.getLogger(CalvinStoredProcedure.class.getName());
	private ConnectionMgr connMgr = CalvinServer.getConnectionMgr();
	private ConservativeLockingMgr concurrMgr;
	private RecoveryMgr recoveryMgr;
	
	private H paramHelper;
	private Transaction tx;

	protected long txNum;
	protected List<CalvinRecord> localreadSet;
	protected List<CalvinRecord> localwriteSet;
	private int masterId;
	private Set<Integer> activeParticipant;
	
	public CalvinStoredProcedure(H helper) {
		if (helper == null)
			throw new IllegalArgumentException("paramHelper should not be null");
		
		paramHelper = helper;
		serverId = CalvinServer.serverId();
		
		localreadSet = new LinkedList<CalvinRecord>();
		localwriteSet = new LinkedList<CalvinRecord>();
		activeParticipant = new HashSet<Integer>();
	}

	public void prepare(int procId, Object... pars) {
		// prepare parameters
		paramHelper.prepareParameters(pars);
		
		// create a transaction
		boolean isReadOnly = paramHelper.isReadOnly();
		tx = VanillaDb.txMgr().newTransaction(
			Connection.TRANSACTION_SERIALIZABLE, isReadOnly,txNum);
		
		
		concurrMgr = new ConservativeLockingMgr(txNum);	
		recoveryMgr = new RecoveryMgr(txNum);
		
		tx.addLifecycleListener(concurrMgr);
		tx.addLifecycleListener(recoveryMgr);
		
		// create log
		recoveryMgr.logStoreProcedureRequest(clientId, procId, pars, txNum);
		//
		analyzeRWSet();
	}

	public void execute() {
		
		boolean isCommitted = false;
		
		try {
			// serve localread
			executeLocalRead();
			
			// serve remoteread
			for(int activenode :activeParticipant) {
				if(masterId != activenode)
					connMgr.sendLocalRecord(activenode, this.txNum);
			}
			
			// execute Logic
			if(activeParticipant.contains(serverId))
				executeLogic();
			
			// The transaction finishes normally
			tx.commit();
			isCommitted = true;
		
		}catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			isCommitted = false;
		}
		
		// prepare server response		
		if(serverId == masterId) {
			connMgr.sendServerResponse(clientId, txNum, new SpResultSet(
					isCommitted,
					paramHelper.getResultSetSchema(),
					newResultSetRecord()
			));
		}
	}
	@Override
	public void run() {
		execute();
	}
	
	protected abstract void executeLocalRead();
	
	protected abstract void executeLogic();
	
	public abstract SpResultRecord newResultSetRecord();
	
	private void analyzeRWSet() {
		
		int partinum = MetadataMgr.NUM_PARTITIONS;
		int readcount;
		int writecount;
		readcount = paramHelper.getReadCount();
		
		writecount = paramHelper.getWriteCount();
		
		for(int i=0 ;i<readcount; i++) {
			CalvinRecord cr = paramHelper.getReadCalvinRecord(i);
			int partiId = cr.gethashCode()%partinum;
			if(partiId == serverId) {
				localreadSet.add(cr);
			}
		}
		
		for(int i=0 ;i<writecount; i++) {
			CalvinRecord cr = paramHelper.getWriteCalvinRecord(i);
			
			int partiId = cr.gethashCode()%partinum;
			if(partiId == serverId) {
				localwriteSet.add(cr);
			}
			activeParticipant.add(partiId);
		}
		activeParticipant.add(masterId);
		
	}
	
	public H getParamHelper() {
		return paramHelper;
	}
	
	protected Transaction getTransaction() {
		return tx;
	}
	
	public void startLock(){
		concurrMgr.LockRecords(localreadSet, localwriteSet);
	}

	public void pasteTxNum(long txNum) {
		this.txNum = txNum;
	}
	public void pasteMasterId(int masterId) {
		this.masterId = masterId;
	}
	public void pasteClientId(int clientId) {
		this.clientId = clientId;
	}
	
	
	protected void abort() {
		throw new ManuallyAbortException();
	}
	
	protected void abort(String message) {
		throw new ManuallyAbortException(message);
	}
	
}