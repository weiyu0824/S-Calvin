package org.vanilladb.bench.server.calvinparam.micro;

import java.util.HashMap;
import java.util.Map;

import org.vanilladb.calvin.sql.CalvinRecord;
import org.vanilladb.calvin.storeprocedure.CalvinStoredProcedureParamHelper;
import org.vanilladb.core.sql.DoubleConstant;
import org.vanilladb.core.sql.IntegerConstant;
import org.vanilladb.core.sql.Schema;
import org.vanilladb.core.sql.Type;
import org.vanilladb.core.sql.VarcharConstant;
import org.vanilladb.core.sql.storedprocedure.SpResultRecord;


public class MicroTxnProcParamHelper extends CalvinStoredProcedureParamHelper {

	private int readCount;
	private int writeCount;
	private int[] readItemId;
	private int[] writeItemId;
	private double[] newItemPrice;
	private String[] itemName;
	private double[] itemPrice;
	private Map<Integer, Double> IidtoPrice;

	public int getReadCount() {
		return readCount;
	}

	public int getWriteCount() {
		return writeCount;
	}

	public int getReadItemId(int index) {
		return readItemId[index];
	}

	public int getWriteItemId(int index) {
		return writeItemId[index];
	}

	public double getNewItemPrice(int index) {
		return newItemPrice[index];
	}

	public void setItemName(String s, int idx) {
		itemName[idx] = s;
	}

	public void setItemPrice(double d, int idx) {
		itemPrice[idx] = d;
	}

	@Override
	public void prepareParameters(Object... pars) {

		// Show the contents of paramters
	   //System.out.println("Params: " + Arrays.toString(pars));

		int indexCnt = 0;
		IidtoPrice = new HashMap<Integer, Double>(); // new define
		readCount = (Integer) pars[indexCnt++];
		readItemId = new int[readCount];
		itemName = new String[readCount];
		itemPrice = new double[readCount];

		for (int i = 0; i < readCount; i++)
			readItemId[i] = (Integer) pars[indexCnt++];

		writeCount = (Integer) pars[indexCnt++];
		writeItemId = new int[writeCount];
		for (int i = 0; i < writeCount; i++)
			writeItemId[i] = (Integer) pars[indexCnt++];
		newItemPrice = new double[writeCount];
		for (int i = 0; i < writeCount; i++)
			newItemPrice[i] = (Double) pars[indexCnt++];
		
		for (int i = 0; i < writeCount; i++) {
			IidtoPrice.put(writeItemId[i], newItemPrice[i]);
		}
		if (writeCount == 0)
			setReadOnly(true);
		
	}

	@Override
	public Schema getResultSetSchema() {
		Schema sch = new Schema();
		Type intType = Type.INTEGER;
		Type itemPriceType = Type.DOUBLE;
		Type itemNameType = Type.VARCHAR(24);
		sch.addField("rc", intType);
		for (int i = 0; i < itemName.length; i++) {
			sch.addField("i_name_" + i, itemNameType);
			sch.addField("i_price_" + i, itemPriceType);
		}
		return sch;
	}

	@Override
	public SpResultRecord newResultSetRecord() {
		SpResultRecord rec = new SpResultRecord();
		rec.setVal("rc", new IntegerConstant(itemName.length));
		for (int i = 0; i < itemName.length; i++) {
			rec.setVal("i_name_" + i, new VarcharConstant(itemName[i], Type.VARCHAR(24)));
			rec.setVal("i_price_" + i, new DoubleConstant(itemPrice[i]));
		}

		return rec;
	}

	@Override
	public CalvinRecord getReadCalvinRecord(int index) {
		return new CalvinRecord("item", readItemId[index]);
	}

	@Override
	public CalvinRecord getWriteCalvinRecord(int index) {
		return new CalvinRecord("item", writeItemId[index]);
	}

	public Map<Integer, Double> IidtoPrice() {
		return IidtoPrice;
	}
}
