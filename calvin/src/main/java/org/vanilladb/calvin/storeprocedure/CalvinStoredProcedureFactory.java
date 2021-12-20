
package org.vanilladb.calvin.storeprocedure;

public interface CalvinStoredProcedureFactory {
	
	CalvinStoredProcedure<?> getCalvinStroredProcedure(int pid);
	
}
