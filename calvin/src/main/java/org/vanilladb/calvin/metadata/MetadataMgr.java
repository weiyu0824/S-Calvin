package org.vanilladb.calvin.metadata;

import org.vanilladb.calvin.sql.CalvinRecord;
import org.vanilladb.calvin.util.CalvinProperties;

public class MetadataMgr{
	public final static int NUM_PARTITIONS;

	static {
		NUM_PARTITIONS = CalvinProperties.getLoader().getPropertyAsInteger(
				MetadataMgr.class.getName() + ".NUM_PARTITIONS", 4);
	}
	
	public int Partition(CalvinRecord cvr) {
		return cvr.gethashCode() % NUM_PARTITIONS;
	}
}