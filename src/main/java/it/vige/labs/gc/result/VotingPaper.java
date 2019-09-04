package it.vige.labs.gc.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VotingPaper extends Electors {

	@JsonIgnore
	private Map<String, Group> mapGroups = new HashMap<String, Group>();

	@JsonIgnore
	private Map<String, Party> mapParties = new HashMap<String, Party>();
	
	private int blankPapers;

	public int getBlankPapers() {
		return blankPapers;
	}

	public void setBlankPapers(int blankPapers) {
		put("blankPapers", blankPapers);
		this.blankPapers = blankPapers;
	}

	public Map<String, Group> getMapGroups() {
		if (mapGroups != null)
			put("mapGroups", mapGroups);
		return mapGroups;
	}

	public Collection<Group> getGroups() {
		return mapGroups.values();
	}

	public void setMapGroups(Map<String, Group> mapGroups) {
		put("mapGroups", mapGroups);
		this.mapGroups = mapGroups;
	}

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
