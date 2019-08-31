package it.vige.labs.gc.votingpapers;

import org.bson.Document;

public class Identifier extends Document {

	private int id;

	private String name;

	public Identifier() {
	}
	
	public Identifier(int id, String name) {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		put("name", name);
		this.name = name;
	}
	
}
