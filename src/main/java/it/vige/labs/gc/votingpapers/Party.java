package it.vige.labs.gc.votingpapers;

import java.util.List;

public class Party extends Identifier {

	private String image;
	
	private List<Candidate> candidates;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		put("image", image);
		this.image = image;
	}

	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		put("candidates", candidates);
		this.candidates = candidates;
	}
}
