package it.vige.labs.gc.result;

import org.bson.Document;

public class Identifier extends Document {

	protected int id;

	public Identifier() {
	}
	
	public Identifier(int id) {
		put("id", id);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		put("id", id);
		this.id = id;
	}
	
}
