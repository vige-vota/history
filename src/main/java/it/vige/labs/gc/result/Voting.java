package it.vige.labs.gc.result;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Voting extends TotalElectors {

	@JsonIgnore
	private Map<Integer, VotingPaper> mapVotingPapers = new HashMap<Integer, VotingPaper>();
	
	private Date affluence;

	public Map<Integer, VotingPaper> getMapVotingPapers() {
		return mapVotingPapers;
	}

	public Collection<VotingPaper> getVotingPapers() {
		return mapVotingPapers.values();
	}

	public void setMapVotingPapers(Map<Integer, VotingPaper> mapVotingPapers) {
		this.mapVotingPapers = mapVotingPapers;
	}

	public Date getAffluence() {
		return affluence;
	}

	public void setAffluence(Date affluence) {
		this.affluence = affluence;
	}

}
