package it.vige.labs.gc.votingpapers;

import java.util.List;

public class Group extends Identifier {

	public String getImage() {
		return getString("image");
	}

	public void setImage(String image) {
		put("image", image);
	}

	public List<Party> getParties() {
		@SuppressWarnings("unchecked")
		List<Party> parties = (List<Party>) get("parties");
		return parties;
	}

	public void setParties(List<Party> parties) {
		put("parties", parties);
	}

	public String getSubtitle() {
		return getString("subtitle");
	}

	public void setSubtitle(String subtitle) {
		put("subtitle", subtitle);
	}

}
