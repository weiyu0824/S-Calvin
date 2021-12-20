/*******************************************************************************
 * Copyright 2016, 2018 vanilladb.org contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.vanilladb.bench.server.calvinprocedure.micro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vanilladb.bench.server.calvinparam.micro.MicroTxnProcParamHelper;
import org.vanilladb.bench.server.procedure.StoredProcedureHelper;
import org.vanilladb.calvin.cache.CacheMgr;
import org.vanilladb.calvin.cache.CacheRecord;
import org.vanilladb.calvin.cache.CacheRecordSet;
import org.vanilladb.calvin.server.CalvinServer;
import org.vanilladb.calvin.sql.CalvinRecord;
import org.vanilladb.calvin.storeprocedure.CalvinStoredProcedure;
import org.vanilladb.core.query.algebra.Scan;
import org.vanilladb.core.sql.Constant;
import org.vanilladb.core.sql.DoubleConstant;
import org.vanilladb.core.sql.IntegerConstant;
import org.vanilladb.core.sql.VarcharConstant;
import org.vanilladb.core.sql.storedprocedure.SpResultRecord;
import org.vanilladb.core.storage.tx.Transaction;
import org.vanilladb.core.sql.Type;

public class MicroTxnProc extends CalvinStoredProcedure<MicroTxnProcParamHelper> {
	// readset
	// writeset
	public MicroTxnProc() {
		super(new MicroTxnProcParamHelper());
	}

	
//	protected void executeSql() {
//		MicroTxnProcParamHelper paramHelper = getParamHelper();
//		Transaction tx = getTransaction();
//		
//		// SELECT
//		for (int idx = 0; idx < paramHelper.getReadCount(); idx++) {
//			int iid = paramHelper.getReadItemId(idx);
//			Scan s = StoredProcedureHelper.executeQuery(
//				"SELECT i_name, i_price FROM item WHERE i_id = " + iid,
//				tx
//			);
//			s.beforeFirst();
//			if (s.next()) {
//				String name = (String) s.getVal("i_name").asJavaVal();
//				double price = (Double) s.getVal("i_price").asJavaVal();
//
//				paramHelper.setItemName(name, idx);
//				paramHelper.setItemPrice(price, idx);
//			} else
//				throw new RuntimeException("Cloud not find item record with i_id = " + iid);
//
//			s.close();
//		}
//		
//		// UPDATE
//		for (int idx = 0; idx < paramHelper.getWriteCount(); idx++) {
//			int iid = paramHelper.getWriteItemId(idx);
//			double newPrice = paramHelper.getNewItemPrice(idx);
//			StoredProcedureHelper.executeUpdate(
//				"UPDATE item SET i_price = " + newPrice + " WHERE i_id =" + iid,
//				tx
//			);
//		}
//	}


	@Override
	protected void executeLocalRead() {
		
		//MicroTxnProcParamHelper paramHelper = getParamHelper();
		Transaction tx = getTransaction();
		//int localReadCount = localreadSet.size();
		
		// SELECT
		//for (int idx = 0; idx < localReadCount; idx++) {
		//int iid = paramHelper.getReadItemId(idx);
		
		for (CalvinRecord cr: localreadSet) {
			
			
			int iid = cr.getId();
			
			Scan s = StoredProcedureHelper.executeQuery(
				"SELECT i_id, i_im_id, i_name, i_price, i_data FROM item WHERE i_id = " + iid,
				tx
			);
			s.beforeFirst();
			if (s.next()) {
//				String name = (String) s.getVal("i_name").asJavaVal();
//				double price = (Double) s.getVal("i_price").asJavaVal();

//				paramHelper.setItemName(name, idx);
//				paramHelper.setItemPrice(price, idx);
				
				
				// write to cache
				CacheMgr cacheMgr = CalvinServer.getCacheMgr();
				CacheRecordSet crs = cacheMgr.getCacheRecordSet(txNum);
				
				Map<String, Constant> recordValueMap = new HashMap<String, Constant>();
				
				recordValueMap.put("i_id", s.getVal("i_id") );
				recordValueMap.put("i_im_id",s.getVal("i_im_id"));
				recordValueMap.put("i_name", s.getVal("i_name") );
				recordValueMap.put("i_price",s.getVal("i_price"));
				recordValueMap.put("i_data", s.getVal("i_data") );
				
				crs.insertLocalRecord("item",new CacheRecord(recordValueMap));
				
				
			} else
				throw new RuntimeException("Cloud not find item record with i_id = " + iid);

			s.close();
		}
		
	}


	@Override
	protected void executeLogic() {
		
		MicroTxnProcParamHelper paramHelper = getParamHelper();
		Transaction tx = getTransaction();
		
		// UPDATE
		for (int idx = 0; idx < paramHelper.getWriteCount(); idx++) {
			int iid = paramHelper.getWriteItemId(idx);
			double newPrice = paramHelper.getNewItemPrice(idx);
			StoredProcedureHelper.executeUpdate(
				"UPDATE item SET i_price = " + newPrice + " WHERE i_id =" + iid,
				tx
			);
		}
		//System.out.println("write count"+getParamHelper().getWriteCount());
		for (CalvinRecord cr :localwriteSet) {
			if(cr.getTableName() == "item") {
				//System.out.println("item");
				int iid = cr.getId();
				double newPrice = paramHelper.IidtoPrice().get(iid);
				StoredProcedureHelper.executeUpdate(
						"UPDATE item SET i_price = " + newPrice + " WHERE i_id =" + iid,
						tx
					);
			}
		}
	}
	
	@Override
	public SpResultRecord newResultSetRecord() {
		SpResultRecord rec = new SpResultRecord();
		int itemNum = 0;

		// through cacheMgr 
		CacheMgr cacheMgr = CalvinServer.getCacheMgr();
		CacheRecordSet cacheRecordSet = cacheMgr.getCacheRecordSet(txNum);
		
		
		
		Map<String,List<CacheRecord>> localRecords =  cacheRecordSet.getLocalRecords();
		Map<String,List<CacheRecord>> remoteRecords =  cacheRecordSet.getRemoteRecords();
		
		// records of table "item"
		List<CacheRecord> localCacheRecords = localRecords.get("item"); 
		List<CacheRecord> remoteCacheRecords = remoteRecords.get("item");
		
		int i = 0;
		if(localCacheRecords!=null) {
			for(CacheRecord lcr : localCacheRecords) {
				rec.setVal("i_name_" + i, new VarcharConstant((String)lcr.getVal("i_name").asJavaVal(), Type.VARCHAR(24)));
				rec.setVal("i_price_" + i, new DoubleConstant((Double)lcr.getVal("i_price").asJavaVal()));
				i++;
			}
		}
		if(remoteCacheRecords!=null) {
			for(CacheRecord rcr : remoteCacheRecords) {
				rec.setVal("i_name_" + i, new VarcharConstant((String)rcr.getVal("i_name").asJavaVal(), Type.VARCHAR(24)));
				rec.setVal("i_price_" + i, new DoubleConstant((Double)rcr.getVal("i_price").asJavaVal()));
				i++;
			}
		}
		rec.setVal("rc", new IntegerConstant(itemNum));
		return rec;
	}
	
	
}
