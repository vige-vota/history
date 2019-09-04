package it.vige.labs.gc.result;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Voting extends TotalElectors {

	@JsonIgnore
	private Map<String, VotingPaper> mapVotingPapers = new HashMap<String, VotingPaper>();

	private Date affluence;

	public Voting() {

	}

	public Voting(Document voting) {
		if (voting != null) {
			@SuppressWarnings("unchecked")
			List<VotingPaper> votingPapers = (List<VotingPaper>) voting.get("votingPapers");
			votingPapers.forEach(votingPaper -> getMapVotingPapers().put(votingPaper.getId() + "", votingPaper));
			setAffluence((Date) voting.get("affluence"));
		}
	}

	public Map<String, VotingPaper> getMapVotingPapers() {
		if (mapVotingPapers != null)
			put("mapVotingPapers", mapVotingPapers);
		return mapVotingPapers;
	}

	public Collection<VotingPaper> getVotingPapers() {
		return mapVotingPapers.values();
	}

	public void setMapVotingPapers(Map<String, VotingPaper> mapVotingPapers) {
		put("mapVotingPapers", mapVotingPapers);
		this.mapVotingPapers = mapVotingPapers;
	}

	public Date getAffluence() {
		return affluence;
	}

	public void setAffluence(Date affluence) {
		put("affluence", affluence);
		this.affluence = affluence;
	}

}
