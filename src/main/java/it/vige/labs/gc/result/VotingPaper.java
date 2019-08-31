package it.vige.labs.gc.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VotingPaper extends Electors {

	@JsonIgnore
	private Map<Integer, Group> mapGroups = new HashMap<Integer, Group>();

	@JsonIgnore
	private Map<Integer, Party> mapParties = new HashMap<Integer, Party>();
	
	private int blankPapers;

	public int getBlankPapers() {
		return blankPapers;
	}

	public void setBlankPapers(int blankPapers) {
		put("blankPapers", blankPapers);
		this.blankPapers = blankPapers;
	}

	public Map<Integer, Group> getMapGroups() {
		return mapGroups;
	}

	public Collection<Group> getGroups() {
		return mapGroups.values();
	}

	public void setMapGroups(Map<Integer, Group> mapGroups) {
		put("mapGroups", mapGroups);
		this.mapGroups = mapGroups;
	}

	public Map<Integer, Party> getMapParties() {
		return mapParties;
	}

	public Collection<Party> getParties() {
		return mapParties.values();
	}

	public void setMapParties(Map<Integer, Party> mapParties) {
		put("mapParties", mapParties);
		this.mapParties = mapParties;
	}
}
