package it.vige.labs.gc.result;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("mapVotingPapers")
public class Voting extends TotalElectors {

	public Map<String, VotingPaper> getMapVotingPapers() {
		@SuppressWarnings("unchecked")
		Map<String, VotingPaper> mapVotingPapers = (Map<String, VotingPaper>) get("mapVotingPapers");
		if (mapVotingPapers == null) {
			mapVotingPapers = new HashMap<String, VotingPaper>();
			put("mapVotingPapers", mapVotingPapers);
		}
		return mapVotingPapers;
	}

	public Collection<VotingPaper> getVotingPapers() {
		@SuppressWarnings("unchecked")
		Collection<VotingPaper> votingPapers = (Collection<VotingPaper>) get("votingPapers");
		if (votingPapers == null) {
			votingPapers = getMapVotingPapers().values();
			put("votingPapers", votingPapers);
		}
		return votingPapers;
	}

	public void setMapVotingPapers(Map<String, VotingPaper> mapVotingPapers) {
		put("mapVotingPapers", mapVotingPapers);
	}

	public void setVotingPapers(Collection<VotingPaper> votingPapers) {
		put("votingPapers", votingPapers);
	}

	public Date getAffluence() {
		return getDate("affluence");
	}

	public void setAffluence(Date affluence) {
		put("affluence", affluence);
	}

	public static void fill(Document document) {
		Document votingPapers = (Document) document.get("mapVotingPapers");
		if (votingPapers != null) {
			document.put("votingPapers", votingPapers.values());
			VotingPaper.fill(document);
		}
	}

}
