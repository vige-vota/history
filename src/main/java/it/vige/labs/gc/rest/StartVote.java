package it.vige.labs.gc.rest;

import java.util.Date;

public class StartVote {

	private boolean start;
	
	private Date startingDate;

	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public Date getStartingDate() {
		return startingDate;
	}

	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}
}
