package it.vige.labs.gc.bean.votingpapers;

import java.util.List;

public class VotingPaper extends Identifier {

	public final static String VOTING_PAPER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	private int maxCandidates;

	private String color;

	private String type;

	private boolean disjointed;

	private int zone = -1;

	private List<Group> groups;

	private List<Party> parties;

	private List<VotingDate> dates;

	public int getMaxCandidates() {
		return maxCandidates;
	}

	public void setMaxCandidates(int maxCandidates) {
		this.maxCandidates = maxCandidates;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isDisjointed() {
		return disjointed;
	}

	public void setDisjointed(boolean disjointed) {
		this.disjointed = disjointed;
	}

	public int getZone() {
		return zone;
	}

	public void setZone(int zone) {
		this.zone = zone;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Party> getParties() {
		return parties;
	}

	public void setParties(List<Party> parties) {
		this.parties = parties;
	}

	public List<VotingDate> getDates() {
		return dates;
	}

	public void setDates(List<VotingDate> dates) {
		this.dates = dates;
	}
}
