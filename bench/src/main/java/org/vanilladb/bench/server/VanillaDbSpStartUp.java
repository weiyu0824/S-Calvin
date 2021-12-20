/*******************************************************************************
 * Copyright 2016, 2017 vanilladb.org contributors
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
package org.vanilladb.bench.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.vanilladb.bench.BenchmarkerParameters;
import org.vanilladb.bench.server.procedure.BasicStoredProcFactory;
import org.vanilladb.bench.server.calvinprocedure.micro.MicrobenchStoredProcFactory;
import org.vanilladb.bench.server.procedure.tpcc.TpccStoredProcFactory;
import org.vanilladb.calvin.server.CalvinServer;
import org.vanilladb.calvin.storeprocedure.CalvinStoredProcedureFactory;
import org.vanilladb.core.remote.storedprocedure.SpStartUp;
import org.vanilladb.core.server.VanillaDb;
import org.vanilladb.core.sql.storedprocedure.StoredProcedureFactory;

public class VanillaDbSpStartUp implements SutStartUp {
	private static Logger logger = Logger.getLogger(VanillaDbSpStartUp.class
			.getName());

	public void startup(String[] args) {
		if (logger.isLoggable(Level.INFO))
			logger.info("initing...");
		
		
		// server id
		int serverId = Integer.valueOf(args[1]);
		
		//VanillaDb.init(args[0], getStoredProcedureFactory());
		CalvinServer.init(args[0],serverId , getCalvinFactory());
		
		if (logger.isLoggable(Level.INFO))
			logger.info("VanillaBench server ready");
		
		try {
			SpStartUp.startUp(1099+serverId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private CalvinStoredProcedureFactory getCalvinFactory() {
		CalvinStoredProcedureFactory factory = null;
		if (logger.isLoggable(Level.INFO))
			logger.info("using Micro-benchmark stored procedures");
		factory = new MicrobenchStoredProcFactory();
		return factory;
	}
	
//	private StoredProcedureFactory getStoredProcedureFactory() {
//		StoredProcedureFactory factory = null;
//		switch (BenchmarkerParameters.BENCH_TYPE) {
//		case MICRO:
//			if (logger.isLoggable(Level.INFO))
//				logger.info("using Micro-benchmark stored procedures");
//			factory = new MicrobenchStoredProcFactory();
//			break;
//		case TPCC:
//			if (logger.isLoggable(Level.INFO))
//				logger.info("using TPC-C stored procedures");
//			factory = new TpccStoredProcFactory();
//			break;
//		}
//		factory = new BasicStoredProcFactory(factory);
//		return factory;
//	}

}