package it.vige.labs.gc.result;

public class Electors extends Identifier {

	private int electors;

	public int getElectors() {
		return electors;
	}

	public void setElectors(int electors) {
		put("electors", electors);
		this.electors = electors;
	}
}
