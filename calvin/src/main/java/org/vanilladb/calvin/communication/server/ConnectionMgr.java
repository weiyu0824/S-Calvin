package org.vanilladb.calvin.communication.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.vanilladb.calvin.cache.CacheMgr;
import org.vanilladb.calvin.cache.CacheRecord;
import org.vanilladb.calvin.cache.CacheRecordSet;
import org.vanilladb.calvin.communication.object.ServerResponse;
import org.vanilladb.calvin.communication.object.StoredProcedureObject;
import org.vanilladb.calvin.communication.object.TxRecordSet;
import org.vanilladb.calvin.metadata.MetadataMgr;
import org.vanilladb.calvin.server.CalvinServer;
import org.vanilladb.calvin.util.CalvinProperties;
import org.vanilladb.comm.server.ServerDemo;
import org.vanilladb.comm.server.VanillaCommServer;
import org.vanilladb.comm.server.VanillaCommServerListener;
import org.vanilladb.comm.view.ProcessType;
import org.vanilladb.core.remote.storedprocedure.SpResultSet;

public class ConnectionMgr implements VanillaCommServerListener{
	
	public final static int TIME_DURATION;

	static {
		TIME_DURATION = CalvinProperties.getLoader().getPropertyAsInteger(
				ConnectionMgr.class.getName() + ".TIME_DURATION", 5);
	}

	private int selfId;
	private VanillaCommServer commserver;
	private BlockingQueue<Serializable> totalMsgQueue = new LinkedBlockingDeque<Serializable>();
	//private List<Serializable> batchMsg = new ArrayList<Serializable>();
	private BlockingQueue<Serializable> batchMsg = new LinkedBlockingDeque<Serializable>();
	
	private BlockingQueue<Serializable> p2pMsg = new LinkedBlockingDeque<Serializable>();
	private long nextTxNum;
	
	public ConnectionMgr(int selfId) {
		this.selfId = selfId;
		nextTxNum = 0;
		commserver = new VanillaCommServer(selfId, this);
		new Thread(commserver).start();
		
		new Thread() {
			
			@Override
			public void run() {
				
				while(true) {
			

					try {
						Thread.sleep(TIME_DURATION);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// start send
					while(!batchMsg.isEmpty()) {
						
						sendTotalOrderMessage(batchMsg.poll());
								
						//sendTotalOrderMessages(batchMsg);		
						
					}
				}
			}
		}.start();
		


	}
	// send to client
	public void sendServerResponse(int receiverId , long txNum, SpResultSet rss) {
		commserver.sendP2pMessage(ProcessType.CLIENT, receiverId, 
				new ServerResponse(selfId, txNum, rss));
		
		CalvinServer.getCacheMgr().clearCacheRecordSet(txNum); // always commit
	}
	
	// send to server
	public void sendTotalOrderMessages(List<Serializable> messages) {
		
		commserver.sendTotalOrderMessages(messages);	// storedprocedure obj
		//System.out.println("sent:" + messages.toString() );
		batchMsg.clear();
	}
	public void sendTotalOrderMessage(Serializable messages) {
		
		commserver.sendTotalOrderMessage(messages);	// storedprocedure obj
		//System.out.println("sent one:" + messages.toString() );
	}
	
	public void sendLocalRecord(int receiverId, long txNum) {
		
		CacheMgr cacheMgr = CalvinServer.getCacheMgr();
		CacheRecordSet cacheRecordSet = cacheMgr.getCacheRecordSet(txNum);
		if(cacheRecordSet.localisDirty()) {
			TxRecordSet txRecordSet = new TxRecordSet(txNum, cacheRecordSet.getLocalRecords());	
			commserver.sendP2pMessage(ProcessType.SERVER, receiverId, txRecordSet);
		}
	}
	@Override
	public void onServerReady() {
		System.out.println("calvin server "+ selfId +" connection ready");
	}
	
	/* client -> server (sp object)
	 * server -> client (server response)
	 * server -> server (sp object , dataRecord)
	 * dataRecord includes localrecord & remoterecord 
	 * */
	@Override
	public void onReceiveP2pMessage(ProcessType senderType, int senderId, Serializable message) {
		
		CacheMgr cacheMgr = CalvinServer.getCacheMgr();
		
			//System.out.println("receive");
			if( message.getClass().equals(StoredProcedureObject.class) ){
				StoredProcedureObject spo = (StoredProcedureObject)message;
				spo.pasteMasterId(selfId);
				
				batchMsg.add(spo);
				
			}else if( message.getClass().equals(TxRecordSet.class) ){
				
				// insert remote record to cache set
				TxRecordSet txRecordSet = (TxRecordSet) message;
				
				long msgTxNum = txRecordSet.getTxNum();
				Map<String,List<CacheRecord>> remoteRecords = txRecordSet.getRemoteRecords();
				
				CacheRecordSet cacheRecordSet = cacheMgr.getCacheRecordSet(msgTxNum);
				
				for(String tableName : remoteRecords.keySet()) {
					cacheRecordSet.insertRemoteRecords(tableName, remoteRecords.get(tableName));
				}
				
			}else {
				System.out.println("strange sender type");
			}

		
	}

	@Override
	public void onReceiveTotalOrderMessage(long serialNumber, Serializable message) {

		//System.out.println("recieve tot");
		StoredProcedureObject spo = (StoredProcedureObject) message;
		spo.pasteTxNum(this.nextTxNum);
		this.nextTxNum ++ ;
		totalMsgQueue.add(message);
		
		// give to scheduler
		CalvinServer.getScheduler().schedule(spo);
		
	}
	
	@Override
	public void onServerFailed(int failedServerId) {
		// TODO Auto-generated method stub
		
	}

	
}