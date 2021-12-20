package org.vanilladb.calvin.cache;


import java.io.Serializable;
import java.util.Map;
import org.vanilladb.core.sql.Constant;


public class CacheRecord implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2872979460798924780L;
	
	private Map <String,Constant> recordValueMap;
	
	public CacheRecord(Map <String,Constant> recordValueMap) {
		this.recordValueMap = recordValueMap;
	}
	
	public Constant getVal(String fieldName) {
		return recordValueMap.get(fieldName);
	}
}