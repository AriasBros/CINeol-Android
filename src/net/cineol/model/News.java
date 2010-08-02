package net.cineol.model;

import java.util.Date;

import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;


public class News implements Parcelable {
	
	protected long				id;
	protected int				numberOfVisits;
	protected int				numberOfComments;
	protected String 			title;
	protected String 			introduction;
	protected String 			urlCINeol;
	protected String			content;
	protected String			author;
	protected Date				date;
	
	protected BitmapDrawable	image;
	
	public News() {
		super();
	}
	
	public News(String title, String introduction) {
		super();
		
		this.title = title;
		this.introduction = introduction;
	}

	public News(Parcel parcel) {
		this.id 			= parcel.readLong();
		this.title 			= parcel.readString();
		this.introduction 	= parcel.readString();
		this.urlCINeol 		= parcel.readString();	
		this.content 		= parcel.readString();	
		this.author 		= parcel.readString();	
		this.numberOfVisits	= parcel.readInt();	
		this.numberOfComments= parcel.readInt();	
		this.date			= (Date) parcel.readValue(null);	
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getUrlCINeol() {
		return urlCINeol;
	}

	public void setUrlCINeol(String urlCINeol) {
		this.urlCINeol = urlCINeol;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getNumberOfVisits() {
		return numberOfVisits;
	}

	public void setNumberOfVisits(int numberOfVisits) {
		this.numberOfVisits = numberOfVisits;
	}

	public int getNumberOfComments() {
		return numberOfComments;
	}

	public void setNumberOfComments(int numberOfComments) {
		this.numberOfComments = numberOfComments;
	}

	public BitmapDrawable getImage() {
		return image;
	}

	public void setImage(BitmapDrawable image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "News [author=" + author + ", content=" + content + ", date="
				+ date + ", id=" + id + ", image=" + image + ", introduction="
				+ introduction + ", numberOfComments=" + numberOfComments
				+ ", numberOfVisits=" + numberOfVisits + ", title=" + title
				+ ", urlCINeol=" + urlCINeol + "]";
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(title);
		dest.writeString(introduction);
		dest.writeString(urlCINeol);
		dest.writeString(content);
		dest.writeString(author);
		dest.writeInt(numberOfVisits);
		dest.writeInt(numberOfComments);
		dest.writeValue(date);
	}
	
    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
		public News createFromParcel(Parcel in) {
		    return new News(in);
		}
		
		public News[] newArray(int size) {
		    return new News[size];
		}
	};
}