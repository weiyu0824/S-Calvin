package org.vanilladb.calvin.sql;

public class CalvinRecord {
	
	private String tableName;
	private int id;
	private int hashCode;
	
	public CalvinRecord(String tableName, int id) {
		this.tableName = tableName;
		this.id = id;
		this.hashCode = hashCode();
	}
	
	public int gethashCode() {
		return this.hashCode;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
//		result = prime * result + id;
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CalvinRecord other = (CalvinRecord) obj;
		if (id != other.id)
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}
	public int getId() {
		return id;
	}
	public String getTableName() {
		return tableName;
	}
	
}