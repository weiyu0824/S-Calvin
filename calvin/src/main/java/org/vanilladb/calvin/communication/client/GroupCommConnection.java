package org.vanilladb.calvin.communication.client;

import java.io.Serializable;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import org.vanilladb.calvin.communication.object.ServerResponse;
import org.vanilladb.calvin.communication.object.StoredProcedureObject;
import org.vanilladb.comm.client.VanillaCommClient;
import org.vanilladb.comm.client.VanillaCommClientListener;
import org.vanilladb.comm.view.ProcessType;
import org.vanilladb.comm.view.ProcessView;
import org.vanilladb.core.remote.storedprocedure.SpResultSet;

public class GroupCommConnection implements VanillaCommClientListener{
	
	private VanillaCommClient commclient;
	private Queue<ServerResponse> srspQueue;
	
	private int count = 0;
	private int clientId;
	
	public GroupCommConnection(int selfId) {
		commclient = new VanillaCommClient(selfId, this);
		clientId = selfId;
		srspQueue = new LinkedBlockingQueue<ServerResponse>();
		new Thread(commclient).start();
	}
	
	public SpResultSet callStoredProc(int pid, Object... pars) {
		StoredProcedureObject spo = new StoredProcedureObject(clientId, pid, pars);
		
		int serverCount = ProcessView.buildServersProcessList(-1).getSize();
		int targetServerId = count%serverCount;
		
		commclient.sendP2pMessage(ProcessType.SERVER, targetServerId , spo); // temp
		
		while(srspQueue.isEmpty());
		
		ServerResponse srsp = srspQueue.poll();
		
		count++;
		return srsp.getResultSet();
	}

	@Override
	public void onReceiveP2pMessage(ProcessType senderType, int senderId, Serializable message) {
		ServerResponse srsp = (ServerResponse) message;
		srspQueue.add(srsp);	
	}

}