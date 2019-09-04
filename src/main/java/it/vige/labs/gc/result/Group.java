package it.vige.labs.gc.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Group extends Electors {

	@JsonIgnore
	private Map<String, Party> mapParties = new HashMap<String, Party>();

	public Map<String, Party> getMapParties() {
		if (mapParties != null)
			put("mapParties", mapParties);
		return mapParties;
	}

	public Collection<Party> getParties() {
		return mapParties.values();
	}

	public void setMapParties(Map<String, Party> mapParties) {
		put("mapParties", mapParties);
		this.mapParties = mapParties;
	}

}
