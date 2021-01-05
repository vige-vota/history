package it.vige.labs.gc.bean.votingpapers;

import org.bson.Document;

public class Identifier extends Document {

	public String getName() {
		return this.getString("name");
	}

	public void setName(String name) {
		this.put("name", name);
	}

}
