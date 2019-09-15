package it.vige.labs.gc.result;

public class Electors extends Identifier {

	public int getElectors() {
		return getInteger("electors");
	}

	public void setElectors(int electors) {
		put("electors", electors);
	}
}
