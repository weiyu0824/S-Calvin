package org.vanilladb.calvin.communication.object;

import java.io.Serializable;
import java.sql.ResultSet;

public class StoredProcedureObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int clientId;
	private int procId;
	private Object[] parms;
	private long txNum;
	private int masterId;
	
	public StoredProcedureObject(int clientId, int procId, Object... parms) {
		this.clientId = clientId;
		this.procId = procId;
		this.parms = parms;
	}
	
	public Object[] getParms() {
		return parms;
	} 
	
	public int getProcId() {
		return procId;
	} 
	
	public int getClientId() {
		return clientId;
	}

	public void pasteTxNum(long txNum) {
		this.txNum = txNum;
	}

	public long getTxNum() {
		return txNum;
	} 
	public void pasteMasterId(int masterId) {
		this.masterId = masterId;
	}
	public int getMasterId() {
		return masterId;
	}
	
}