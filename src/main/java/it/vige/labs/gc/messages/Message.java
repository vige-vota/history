package it.vige.labs.gc.messages;

import java.util.Date;

public class Message {

	private Severity severity;

	private String summary;

	private String detail;

	private Date date;

	public Message() {

	}

	public Message(Severity severity, String summary, String detail) {
		this.severity = severity;
		this.summary = summary;
		this.detail = detail;
	}

	public Message(Severity severity, String summary, String detail, Date date) {
		this.severity = severity;
		this.summary = summary;
		this.detail = detail;
		this.date = date;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
