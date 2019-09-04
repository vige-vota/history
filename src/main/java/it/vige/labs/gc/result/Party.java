package it.vige.labs.gc.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Party extends Electors {

	@JsonIgnore
	private Map<String, Candidate> mapCandidates = new HashMap<String, Candidate>();

	public Map<String, Candidate> getMapCandidates() {
		if (mapCandidates != null)
			put("mapCandidates", mapCandidates);
		return mapCandidates;
	}

	public Collection<Candidate> getCandidates() {
		return mapCandidates.values();
	}

	public void setMapCandidates(Map<String, Candidate> mapCandidates) {
		put("mapCandidates", mapCandidates);
		this.mapCandidates = mapCandidates;
	}

}
