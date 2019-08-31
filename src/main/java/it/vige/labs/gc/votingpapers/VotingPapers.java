package it.vige.labs.gc.votingpapers;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

public class VotingPapers extends Document {

	private boolean admin;

	private List<VotingPaper> votingPapers = new ArrayList<VotingPaper>();

	public VotingPapers() {

	}

	public VotingPapers(Document votingPapers) {
		if (votingPapers != null) {
			@SuppressWarnings("unchecked")
			List<VotingPaper> fromDocument = (List<VotingPaper>) votingPapers.get("votingPapers");
			setVotingPapers(fromDocument);
			setAdmin((boolean) votingPapers.get("admin"));
		}
	}

	public List<VotingPaper> getVotingPapers() {
		return votingPapers;
	}

	public void setVotingPapers(List<VotingPaper> votingPapers) {
		put("votingPapers", votingPapers);
		this.votingPapers = votingPapers;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		put("admin", admin);
		this.admin = admin;
	}
}
