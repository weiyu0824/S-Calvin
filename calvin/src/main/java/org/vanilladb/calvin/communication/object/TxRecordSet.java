package org.vanilladb.calvin.communication.object;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.vanilladb.calvin.cache.CacheRecord;

public class TxRecordSet implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	long  txNum;
	private Map <String,List<CacheRecord>> remoteRecords;
	
	public TxRecordSet(long txNum,Map<String,List<CacheRecord>> remoteRecords) {
		this.txNum = txNum;
		this.remoteRecords = remoteRecords;
	}
	public Map <String,List<CacheRecord>> getRemoteRecords() {
		return remoteRecords;
	}
	public long getTxNum() {
		return txNum;
	}
	
}