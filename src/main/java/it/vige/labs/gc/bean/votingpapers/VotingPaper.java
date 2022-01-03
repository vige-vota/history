package it.vige.labs.gc.bean.votingpapers;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class VotingPaper extends Identifier implements Validation {

	public final static String VOTING_PAPER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	private int maxCandidates;

	private String color;

	private String type;

	@JsonFormat(shape = STRING, pattern = VOTING_PAPER_DATE_FORMAT)
	private Date startingDate;

	@JsonFormat(shape = STRING, pattern = VOTING_PAPER_DATE_FORMAT)
	private Date endingDate;

	private boolean disjointed;

	private int zone = -1;

	private List<Group> groups;

	private List<Party> parties;

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

	public Date getStartingDate() {
		return startingDate;
	}

	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}

	public Date getEndingDate() {
		return endingDate;
	}

	public void setEndingDate(Date endingDate) {
		this.endingDate = endingDate;
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

	@Override
	public boolean validate() {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat(VOTING_PAPER_DATE_FORMAT);
		Date startingDate;
		Date endingDate;
		boolean result = false;
		try {
			startingDate = dateFormat.parse(getString("startingDate"));
			endingDate = dateFormat.parse(getString("endingDate"));
			result = startingDate.compareTo(endingDate) < 0 && endingDate.compareTo(date) > 0;
		} catch (ParseException e) {
			result = false;
		}
		return result;
	}
}
