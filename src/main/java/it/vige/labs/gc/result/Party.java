package it.vige.labs.gc.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("mapCandidates")
public class Party extends Electors {

	public Map<String, Candidate> getMapCandidates() {
		@SuppressWarnings("unchecked")
		Map<String, Candidate> mapCandidates = (Map<String, Candidate>) get("mapCandidates");
		if (mapCandidates == null) {
			mapCandidates = new HashMap<String, Candidate>();
			put("mapCandidates", mapCandidates);
		}
		return mapCandidates;
	}

	public Collection<Candidate> getCandidates() {
		@SuppressWarnings("unchecked")
		Collection<Candidate> candidates = (Collection<Candidate>) get("candidates");
		if (candidates == null) {
			candidates = getMapCandidates().values();
			put("candidates", candidates);
		}
		return candidates;
	}

	public void setCandidates(Collection<Candidate> candidates) {
		put("candidates", candidates);
	}

	public void setMapCandidates(Map<String, Candidate> mapCandidates) {
		put("mapCandidates", mapCandidates);
	}

	public static void fill(Document document) {
		@SuppressWarnings("unchecked")
		Collection<Document> parties = (Collection<Document>) document.get("parties");
		if (parties != null) {
			parties.forEach(party -> {
				Document candidates = (Document) party.get("mapCandidates");
				if (candidates != null)
					party.put("candidates", candidates.values());
			});
		}
	}

}
