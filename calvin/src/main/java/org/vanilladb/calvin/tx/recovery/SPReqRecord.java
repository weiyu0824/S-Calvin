package org.vanilladb.calvin.tx.recovery;

import java.util.LinkedList;
import java.util.List;

import org.vanilladb.core.server.VanillaDb;
import org.vanilladb.core.sql.BigIntConstant;
import org.vanilladb.core.sql.Constant;
import org.vanilladb.core.sql.IntegerConstant;
import org.vanilladb.core.sql.VarcharConstant;
import org.vanilladb.core.storage.log.LogSeqNum;

public class SPReqRecord {
	
	private int clientId;
	private int procId;
	private Object[] parms;
	private long txNum;
	
	public SPReqRecord(int clientId, int procId, Object[] parms, long txNum) {
		this.clientId = clientId;
		this.procId = procId;
		this.parms = parms;
		this.txNum = txNum;
	}
	
	public List<Constant> buildRecord() {
		List<Constant> rec = new LinkedList<Constant>();
		rec.add(new IntegerConstant(clientId));
		rec.add(new IntegerConstant(procId));
		rec.add(new VarcharConstant(parms.toString()));
		rec.add(new BigIntConstant(txNum));
		return rec;
	}
	
	public LogSeqNum writeToLog() {
		List<Constant> rec = buildRecord();
		return VanillaDb.logMgr().append(rec.toArray(new Constant[rec.size()]));
	}
	
	
}