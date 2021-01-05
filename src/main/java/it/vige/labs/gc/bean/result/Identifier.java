package it.vige.labs.gc.bean.result;

import org.bson.Document;

public class Identifier extends Document {

	public int getId() {
		return getInteger("id");
	}

	public void setId(int id) {
		put("id", id);
	}
	
}
