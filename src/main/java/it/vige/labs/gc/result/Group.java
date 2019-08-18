package it.vige.labs.gc.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Group extends Electors {

	@JsonIgnore
	private Map<Integer, Party> mapParties = new HashMap<Integer, Party>();

	public Map<Integer, Party> getMapParties() {
		return mapParties;
	}

	public Collection<Party> getParties() {
		return mapParties.values();
	}

	public void setMapParties(Map<Integer, Party> mapParties) {
		this.mapParties = mapParties;
	}

}
