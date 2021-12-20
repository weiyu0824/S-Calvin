package org.vanilladb.calvin.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CacheRecordSet implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8187967051074075399L;

	private long txNum;
	
	private Map <String,List<CacheRecord>> localRecords;
	private Map <String,List<CacheRecord>> remoteRecords;
	
	boolean localisDirty;
	boolean remoteisDirty;
	
	public CacheRecordSet(long txNum){
		this.txNum = txNum;
		this.localRecords = new HashMap <String,List<CacheRecord>>();
		this.remoteRecords = new HashMap <String,List<CacheRecord>>();
		
		localisDirty = false;
		remoteisDirty = false;
	}
	
	public long getTxNum() {
		return txNum;
	}
	
	public void insertLocalRecord(String tableName, CacheRecord cr) {
		if(localRecords.containsKey(tableName)) {
			localRecords.get(tableName).add(cr);
		}else {
			List<CacheRecord> newCacheRecordList= new LinkedList<CacheRecord>();
			newCacheRecordList.add(cr);
			localRecords.put(tableName, newCacheRecordList);
		}
		localisDirty = true;
	}
	
	public void insertRemoteRecords(String tableName, List<CacheRecord> crs) {
		if(remoteRecords.containsKey(tableName)) {
			remoteRecords.get(tableName).addAll(crs);
		}else {
			remoteRecords.put(tableName, crs);
		}
		remoteisDirty = true;
	}
	
	public Map<String,List<CacheRecord>> getLocalRecords() {
		return localRecords;
	}
	
	public Map<String,List<CacheRecord>> getRemoteRecords() {
		return remoteRecords;
	}
	
	public boolean localisDirty() {
		return localisDirty;
	}
	
	public boolean remoteisDirty() {
		return remoteisDirty;
	}
}