package it.vige.labs.gc.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Party extends Electors {

	@JsonIgnore
	private Map<Integer, Candidate> mapCandidates = new HashMap<Integer, Candidate>();

	public Map<Integer, Candidate> getMapCandidates() {
		return mapCandidates;
	}

	public Collection<Candidate> getCandidates() {
		return mapCandidates.values();
	}

	public void setMapCandidates(Map<Integer, Candidate> mapCandidates) {
		this.mapCandidates = mapCandidates;
	}

}
