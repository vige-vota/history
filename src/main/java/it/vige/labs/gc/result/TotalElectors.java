package it.vige.labs.gc.result;

import org.bson.Document;

public class TotalElectors extends Document {

	private int electors;

	public int getElectors() {
		return electors;
	}

	public void setElectors(int electors) {
		put("electors", electors);
		this.electors = electors;
	}
}
