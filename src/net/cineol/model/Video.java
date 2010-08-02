package net.cineol.model;

import java.util.Date;

public class Video {
	
	private String 	description;
	private String 	id;
	private Date	date;
	
	public Video(String id, String desc, Date date) {
		super();
		
		this.id = id;
		this.description = desc;
		this.date = date;
	}
	
	public Video(Video video) {
		this(video.id, video.description, video.date);
	}
	
	public Video() {
		this(null, null, null);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Video [date=" + date + ", description=" + description + ", id="
				+ id + "]";
	}
}
