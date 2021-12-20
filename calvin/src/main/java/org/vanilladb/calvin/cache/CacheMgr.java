package org.vanilladb.calvin.cache;

import java.util.HashMap;
import java.util.Map;

public class CacheMgr{
	
	private Map<Long,CacheRecordSet> txCacheRecordMap;
	
	public CacheMgr(){
		txCacheRecordMap = new HashMap<Long,CacheRecordSet>();
	}
	
	public void CreateCacheRecordSet(long txNum) {
		txCacheRecordMap.put(txNum, new CacheRecordSet(txNum));
	}
	
	public CacheRecordSet getCacheRecordSet(long txNum) {
		if(txCacheRecordMap.get(txNum) == null) {
			txCacheRecordMap.put(txNum, new CacheRecordSet(txNum));
		}
		return txCacheRecordMap.get(txNum);	
	}
	
	public void clearCacheRecordSet(long txNum) {
		txCacheRecordMap.remove(txNum);
	}
	

}