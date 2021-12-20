package org.vanilladb.calvin.tx.recovery;

import org.vanilladb.core.storage.log.LogSeqNum;
import org.vanilladb.core.storage.tx.Transaction;
import org.vanilladb.core.storage.tx.TransactionLifecycleListener;

public class RecoveryMgr implements TransactionLifecycleListener{
	
	long txNum;
	
	public RecoveryMgr(long txNum) {
		this.txNum = txNum;
	}
	
	public LogSeqNum logStoreProcedureRequest(int clientId, int procId, Object[] parms, long txNum) {
		SPReqRecord sprq = new SPReqRecord(clientId, procId, parms, txNum);
		return sprq.writeToLog();
	}

	@Override
	public void onTxCommit(Transaction tx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTxRollback(Transaction tx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTxEndStatement(Transaction tx) {
		// TODO Auto-generated method stub
		
	}
	
}