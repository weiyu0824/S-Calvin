package org.vanilladb.calvin.communication.client;


import org.vanilladb.calvin.communication.client.GroupCommConnection;
import org.vanilladb.core.remote.storedprocedure.SpResultSet;

public class GroupCommDriver {

	private int clientId;
	
	public GroupCommConnection connect(int clientId) {
		this.clientId = clientId;
		return new GroupCommConnection(clientId);
		
	}
	
}