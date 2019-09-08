package it.vige.labs.gc.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("mapParties")
public class Group extends Electors {

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

	public void setMapParties(Map<String, Party> mapParties) {
		put("mapParties", mapParties);
	}

	public void setParties(Collection<Party> parties) {
		put("parties", parties);
	}

	public static void fill(Document document) {
		@SuppressWarnings("unchecked")
		Collection<Document> groups = (Collection<Document>) document.get("groups");
		if (groups != null) {
			groups.forEach(group -> {
				Document parties = (Document) group.get("mapParties");
				if (parties != null) {
					group.put("parties", parties.values());
					Party.fill(group);
				}
			});
		}
	}

}
