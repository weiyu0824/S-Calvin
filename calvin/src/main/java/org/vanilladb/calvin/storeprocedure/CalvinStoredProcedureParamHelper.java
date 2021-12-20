package org.vanilladb.calvin.storeprocedure;

import org.vanilladb.calvin.sql.CalvinRecord;
import org.vanilladb.core.sql.Schema;
import org.vanilladb.core.sql.storedprocedure.SpResultRecord;

public abstract class CalvinStoredProcedureParamHelper {

	public static CalvinStoredProcedureParamHelper newDefaultParamHelper() {
		return new CalvinStoredProcedureParamHelper() {
			@Override
			public void prepareParameters(Object... pars) {
				// do nothing
			}
			
			@Override
			public Schema getResultSetSchema() {
				return new Schema();
			}

			@Override
			public SpResultRecord newResultSetRecord() {
				return new SpResultRecord();
			}

			@Override
			public int getReadCount() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getWriteCount() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public CalvinRecord getReadCalvinRecord(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CalvinRecord getWriteCalvinRecord(int index) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	private boolean isReadOnly = false;

	/**
	 * Prepare parameters for this stored procedure.
	 * 
	 * @param pars
	 *            An object array contains all parameter for this stored
	 *            procedure.
	 */
	public abstract void prepareParameters(Object... pars);
	
	public abstract Schema getResultSetSchema();
	
	public abstract SpResultRecord newResultSetRecord();
	
	protected void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}
	
	public boolean isReadOnly() {
		return isReadOnly;
	}
	
	public abstract int getReadCount();
	public abstract int getWriteCount();
	public abstract CalvinRecord getReadCalvinRecord(int index);
	public abstract CalvinRecord getWriteCalvinRecord(int index);
}
