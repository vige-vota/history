package it.vige.labs.gc.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("mapGroups,mapParties")
public class VotingPaper extends Electors {

	public int getBlankPapers() {
		return getInteger("blankPapers");
	}

	public void setBlankPapers(int blankPapers) {
		put("blankPapers", blankPapers);
	}

	public Map<String, Group> getMapGroups() {
		@SuppressWarnings("unchecked")
		Map<String, Group> mapGroups = (Map<String, Group>) get("mapGroups");
		if (mapGroups == null) {
			mapGroups = new HashMap<String, Group>();
			put("mapGroups", mapGroups);
		}
		return mapGroups;
	}

	public Collection<Group> getGroups() {
		@SuppressWarnings("unchecked")
		Collection<Group> groups = (Collection<Group>) get("groups");
		if (groups == null) {
			groups = getMapGroups().values();
			put("groups", groups);
		}
		return groups;
	}

	public void setGroups(Collection<Group> groups) {
		put("groups", groups);
	}

	public void setMapGroups(Map<String, Group> mapGroups) {
		put("mapGroups", mapGroups);
	}

	public Map<String, Party> getMapParties() {
		@SuppressWarnings("unchecked")
		Map<String, Party> mapParties = (Map<String, Party>) get("mapParties");
		if (mapParties == null) {
			mapParties = new HashMap<String, Party>();
			put("mapParties", mapParties);
		}
		return mapParties;
	}

	public Collection<Party> getParties() {
		@SuppressWarnings("unchecked")
		Collection<Party> parties = (Collection<Party>) get("parties");
		if (parties == null) {
			parties = getMapParties().values();
			put("parties", parties);
		}
		return parties;
	}

	public void setParties(Collection<Party> parties) {
		put("parties", parties);
	}

	public void setMapParties(Map<String, Party> mapParties) {
		put("mapParties", mapParties);
	}

	public static void fill(Document document) {
		@SuppressWarnings("unchecked")
		Collection<Document> votingPapers = (Collection<Document>) document.get("votingPapers");
		votingPapers.forEach(votingPaper -> {
			Document groups = (Document) votingPaper.get("mapGroups");
			if (groups != null) {
				votingPaper.put("groups", groups.values());
				Group.fill(votingPaper);
			}
			Document parties = (Document) votingPaper.get("mapParties");
			if (parties != null) {
				votingPaper.put("parties", parties.values());
				Party.fill(votingPaper);
			}
		});
	}
}
