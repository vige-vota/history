package it.vige.labs.gc.result;

import org.bson.Document;

public class TotalElectors extends Document {

	public int getElectors() {
		return getInteger("electors");
	}

	public void setElectors(int electors) {
		put("electors", electors);
	}
}
