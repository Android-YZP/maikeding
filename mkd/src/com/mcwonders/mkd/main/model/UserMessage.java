package com.mcwonders.mkd.main.model;

public class UserMessage {
	private int id;
	private String title;
	private int type;
	private boolean status;
	private String date;
	private int redirectType;
	private int redirectId;
	private String content;
	public UserMessage() {
		super();
		// TODO Auto-generated constructor stub
	}
	public UserMessage(String title, int type, boolean status, String date,
					   int redirectType, int redirectId) {
		super();
		this.title = title;
		this.type = type;
		this.status = status;
		this.date = date;
		this.redirectType = redirectType;
		this.redirectId = redirectId;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getRedirectType() {
		return redirectType;
	}
	public void setRedirectType(int redirectType) {
		this.redirectType = redirectType;
	}
	public int getRedirectId() {
		return redirectId;
	}
	public void setRedirectId(int redirectId) {
		this.redirectId = redirectId;
	}
	@Override
	public String toString() {
		return "UserMessage [title=" + title + ", type=" + type + ", status="
				+ status + ", date=" + date + ", redirectType=" + redirectType
				+ ", redirectId=" + redirectId + "]";
	}
	
}
