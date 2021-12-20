package org.vanilladb.calvin.communication.object;

import java.io.Serializable;

import org.vanilladb.core.remote.storedprocedure.SpResultSet;

public class ServerResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	SpResultSet rss;
	int serverId;
	long txNum;
	
	public ServerResponse(int serverId, long txNum , SpResultSet rss) {
		this.serverId = serverId;
		this.rss = rss;
		this.txNum = txNum;
	}
	
	public SpResultSet getResultSet() {
		return rss;
	}
}