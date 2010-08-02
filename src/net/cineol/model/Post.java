package net.cineol.model;

import java.util.Date;

public class Post {
	
	protected String 	user;
	protected String	message;

	protected Date 		date;
	
	public Post() {
		super();
	}

	public Post(String user, String message, Date date) {
		super();
		
		this.date	 = date;
		this.message = message;
		this.user	 = user;
	}
	
	public Post(Post post) {
		this(post.user, post.message, post.date);
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
