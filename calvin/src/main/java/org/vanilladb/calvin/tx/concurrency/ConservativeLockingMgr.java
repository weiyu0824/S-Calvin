package org.vanilladb.calvin.tx.concurrency;

import java.util.List;

import org.vanilladb.calvin.server.CalvinServer;
import org.vanilladb.calvin.sql.CalvinRecord;
import org.vanilladb.core.storage.file.BlockId;
import org.vanilladb.core.storage.record.RecordId;
import org.vanilladb.core.storage.tx.Transaction;
import org.vanilladb.core.storage.tx.concurrency.ConcurrencyMgr;

public class ConservativeLockingMgr extends ConcurrencyMgr{
	
	protected long txNum;
	protected static ConservativeLockTable lockTbl = new ConservativeLockTable();
	
	public ConservativeLockingMgr(long txNumber) {
		txNum = txNumber;
	}
	
	public void LockRecords(List<CalvinRecord> readRecords, List<CalvinRecord> writeRecords) {
		if(readRecords != null) {
			for(CalvinRecord cr : readRecords) {
				lockTbl.sLock(cr, txNum);
			}
		}
		else {
			System.out.println("no read records, serverId =  "+CalvinServer.serverId());
		}
		if(writeRecords != null) {
			for(CalvinRecord cr : writeRecords) {
				lockTbl.xLock(cr, txNum);
			}
		}
		else {
			System.out.println("no write records, serverId =  "+CalvinServer.serverId());
		}
	}
	
	@Override
	public void onTxCommit(Transaction tx) {
		lockTbl.releaseAll(txNum, false);
	}
	@Override
	public void onTxRollback(Transaction tx) {
		lockTbl.releaseAll(txNum, false);
	}

	@Override
	public void onTxEndStatement(Transaction tx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyFile(String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readFile(String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertBlock(BlockId blk) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyBlock(BlockId blk) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readBlock(BlockId blk) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyRecord(RecordId recId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readRecord(RecordId recId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyIndex(String dataFileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readIndex(String dataFileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyLeafBlock(BlockId blk) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readLeafBlock(BlockId blk) {
		// TODO Auto-generated method stub
		
	}
}