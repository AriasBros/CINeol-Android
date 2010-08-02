package net.cineol.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

	private long	id;
	private int 	duration;
	private float	rating;
	private int		votes;
	private int		year;
	private int		spainTakings;
	private	int		usaTakings;
	protected int	comments;
	
	private String	indexTitle;
	private String 	title;
	private String 	originalTitle;
	private String	genre;
	private String	format;
	private String	synopsis;
	private String	country;
	private String 	urlCINeol;
	
	private Date	spainPremier;
	private Date	originalPremier;
	
	private	BitmapDrawable	posterThumbnail;
	private BitmapDrawable	poster;
	
	private ArrayList<Video>  videos 	= new ArrayList<Video>();
	private ArrayList<Image>  images 	= new ArrayList<Image>();	
	private ArrayList<Person> credits 	= new ArrayList<Person>();
	private ArrayList<Person> actors 	= new ArrayList<Person>();
	
	
	public Movie() {
		super();
	}
	
	public Movie(long id) {
		super();
		this.id = id;
	}
	
	public Movie(Parcel parcel) {
		id = parcel.readLong();
		
		rating = parcel.readFloat();
		
		duration = parcel.readInt();
		votes = parcel.readInt();
		year = parcel.readInt();
		spainTakings = parcel.readInt();
		usaTakings = parcel.readInt();
		comments = parcel.readInt();

		indexTitle = parcel.readString();
		title = parcel.readString();
		originalTitle = parcel.readString();
		genre = parcel.readString();
		format = parcel.readString();
		synopsis = parcel.readString();
		country = parcel.readString();
		urlCINeol = parcel.readString();

		spainPremier = (Date) parcel.readValue(null);
		originalPremier = (Date) parcel.readValue(null);
	}

	public long getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getNumberOfComments() {
		return comments;
	}

	public void setNumberOfComments(int comments) {
		this.comments = comments;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public String getIndexTitle() {
		return indexTitle;
	}

	public void setIndexTitle(String indexTitle) {
		this.indexTitle = indexTitle;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public Date getSpainPremier() {
		return spainPremier;
	}

	public void setSpainPremier(Date spainPremier) {
		this.spainPremier = spainPremier;
	}

	public Date getOriginalPremier() {
		return originalPremier;
	}

	public void setOriginalPremier(Date originalPremier) {
		this.originalPremier = originalPremier;
	}

	public void setPoster(BitmapDrawable poster) {
		this.poster = poster;
	}

	public BitmapDrawable getPoster() {
		return poster;
	}

	public void setPosterThumbnail(BitmapDrawable posterThumbnail) {
		this.posterThumbnail = posterThumbnail;
	}

	public BitmapDrawable getPosterThumbnail() {
		return posterThumbnail;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getSpainTakings() {
		return spainTakings;
	}

	public void setSpainTakings(int spainTakings) {
		this.spainTakings = spainTakings;
	}

	public int getUsaTakings() {
		return usaTakings;
	}

	public void setUsaTakings(int usaTakings) {
		this.usaTakings = usaTakings;
	}

	public String getOriginalTitle() {
		return originalTitle;
	}

	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = originalTitle;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getUrlCINeol() {
		return urlCINeol;
	}

	public void setUrlCINeol(String urlCINeol) {
		this.urlCINeol = urlCINeol;
	}

	public void setImages(ArrayList<Image> images) {
		this.images = images;
	}

	public ArrayList<Image> getImages() {
		return images;
	}
	
	public void addImage(Image image) {
		this.images.add(image);
	}
	
	public void removeImage(Image image) {
		this.images.remove(image);
	}
	
	public void removeImage(int position) {
		this.images.remove(position);
	}

	public void setVideos(ArrayList<Video> videos) {
		this.videos = videos;
	}

	public ArrayList<Video> getVideos() {
		return videos;
	}
	
	public void addVideo(Video video) {
		this.videos.add(video);
	}
	
	public void removeVideo(Video video) {
		this.videos.remove(video);
	}
	
	public void removeVideo(int position) {
		this.videos.remove(position);
	}	

	public ArrayList<Person> getCredits() {
		return credits;
	}

	public void setCredits(ArrayList<Person> credits) {
		this.credits = credits;
	}
	
	public void addCredit(Person person) {
		this.credits.add(person);
	}
	
	public void removeCredit(Person person) {
		this.credits.remove(person);
	}
	
	public void removeCredit(int position) {
		this.credits.remove(position);
	}

	public ArrayList<Person> getActors() {
		return actors;
	}

	public void setActors(ArrayList<Person> actors) {
		this.actors = actors;
	}
	
	public void addActor(Person person) {
		this.actors.add(person);
	}
	
	public void removeActor(Person person) {
		this.actors.remove(person);
	}
	
	public void removeActor(int position) {
		this.actors.remove(position);
	}
	
	public static MovieTitleComparator getTitleComparator(boolean ascending) {
		return new MovieTitleComparator(ascending);
	}
	
	public static MovieIndexTitleComparator getIndexTitleComparator(boolean ascending) {
		return new MovieIndexTitleComparator(ascending);
	}
	
	public static MovieGenreComparator getGenreComparator(boolean ascending) {
		return new MovieGenreComparator(ascending);
	}
	
	public static MovieSpainPremierComparator getSpainPremierComparator(boolean ascending) {
		return new MovieSpainPremierComparator(ascending);
	}	
	
	public static MovieRatingComparator getRatingComparator(boolean ascending) {
		return new MovieRatingComparator(ascending);
	}
	
	public static MovieDurationComparator getDurationComparator(boolean ascending) {
		return new MovieDurationComparator(ascending);
	}
	public static MovieYearComparator getYearComparator(boolean ascending) {
		return new MovieYearComparator(ascending);
	}

	@Override
	public String toString() {
		return "Movie [actors=" + actors + ", country=" + country
				+ ", credits=" + credits + ", duration=" + duration
				+ ", format=" + format + ", genre=" + genre + ", id=" + id
				+ ", images=" + images + ", indexTitle=" + indexTitle
				+ ", originalPremier=" + originalPremier + ", originalTitle="
				+ originalTitle + ", poster=" + poster + ", posterThumbnail="
				+ posterThumbnail + ", rating=" + rating + ", spainPremier="
				+ spainPremier + ", spainTakings=" + spainTakings
				+ ", synopsis=" + synopsis + ", title=" + title
				+ ", urlCINeol=" + urlCINeol + ", usaTakings=" + usaTakings
				+ ", videos=" + videos + ", votes=" + votes + ", year=" + year
				+ "]";
	}	
	
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		
		dest.writeFloat(rating);
		
		dest.writeInt(duration);
		dest.writeInt(votes);
		dest.writeInt(year);
		dest.writeInt(spainTakings);
		dest.writeInt(usaTakings);
		dest.writeInt(comments);

		dest.writeString(indexTitle);
		dest.writeString(title);
		dest.writeString(originalTitle);
		dest.writeString(genre);
		dest.writeString(format);
		dest.writeString(synopsis);
		dest.writeString(country);
		dest.writeString(urlCINeol);

		dest.writeValue(spainPremier);
		dest.writeValue(originalPremier);
	}
	
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
		public Movie createFromParcel(Parcel in) {
		    return new Movie(in);
		}
		
		public Movie[] newArray(int size) {
		    return new Movie[size];
		}
	};
}

abstract class MovieComparator implements Comparator<Movie> {
	
	protected boolean ascending = true;
	
	public MovieComparator(boolean ascending) {
		this.ascending = ascending;
	}
	
	abstract protected int _compare(Movie object1, Movie object2);
	
	public int compare(Movie object1, Movie object2) {
		if (this.ascending)
			return this._compare(object1, object2);
		else
			return this._compare(object2, object1);
	}
}

final class MovieTitleComparator extends MovieComparator {

	public MovieTitleComparator(boolean ascending) {
		super(ascending);
	}

	@Override
	protected int _compare(Movie object1, Movie object2) {
		return object1.getTitle().compareToIgnoreCase(object2.getTitle());
	}
}

final class MovieIndexTitleComparator extends MovieComparator {

	public MovieIndexTitleComparator(boolean ascending) {
		super(ascending);
	}

	@Override
	protected int _compare(Movie object1, Movie object2) {
		return object1.getIndexTitle().compareToIgnoreCase(object2.getIndexTitle());
	}
}

final class MovieGenreComparator extends MovieComparator {

	public MovieGenreComparator(boolean ascending) {
		super(ascending);
	}

	@Override
	protected int _compare(Movie object1, Movie object2) {
		int result = object1.getGenre().compareToIgnoreCase(object2.getGenre());
		
		if (result == 0 && ascending)
			result = object1.getTitle().compareToIgnoreCase(object2.getTitle());
		else if (result == 0 && !ascending)
			result = object2.getTitle().compareToIgnoreCase(object1.getTitle());
		
		return result;
	}	
}

final class MovieRatingComparator extends MovieComparator {
		
	public MovieRatingComparator(boolean ascending) {
		super(ascending);
	}
	
	@Override
	protected int _compare(Movie object1, Movie object2) {
		if (object1.getRating() < object2.getRating())
			return -1;
		
		else if (object1.getRating() > object2.getRating())
			return 1;
		
		else
			if (ascending)
				return object1.getTitle().compareToIgnoreCase(object2.getTitle());
			else
				return object2.getTitle().compareToIgnoreCase(object1.getTitle());
			
	}
}

final class MovieSpainPremierComparator extends MovieComparator {

	public MovieSpainPremierComparator(boolean ascending) {
		super(ascending);
	}

	@Override
	protected int _compare(Movie object1, Movie object2) {
		int result = object1.getSpainPremier().compareTo(object2.getSpainPremier());
		
		if (result == 0 && ascending)
			result = object1.getTitle().compareToIgnoreCase(object2.getTitle());
		else if (result == 0 && !ascending)
			result = object2.getTitle().compareToIgnoreCase(object1.getTitle());
		
		return result;
	}
}

final class MovieDurationComparator extends MovieComparator {

	boolean ascending = true;
	
	public MovieDurationComparator(boolean ascending) {
		super(ascending);
	}

	@Override
	protected int _compare(Movie object1, Movie object2) {
		if (object1.getDuration() < object2.getDuration())
			return -1;
		
		else if (object1.getDuration() > object2.getDuration())
			return 1;
		
		else
			if (ascending)
				return object1.getTitle().compareToIgnoreCase(object2.getTitle());
			else
				return object2.getTitle().compareToIgnoreCase(object1.getTitle());
	}
}

final class MovieYearComparator extends MovieComparator {

	boolean ascending = true;
	
	public MovieYearComparator(boolean ascending) {
		super(ascending);
	}

	@Override
	protected int _compare(Movie object1, Movie object2) {
		if (object1.getYear() < object2.getYear())
			return -1;
		
		else if (object1.getYear() > object2.getYear())
			return 1;
		
		else if (object1.getTitle() != null && object2.getTitle() != null)
			if (ascending)
				return object1.getTitle().compareToIgnoreCase(object2.getTitle());
			else
				return object2.getTitle().compareToIgnoreCase(object1.getTitle());
		else
			if (ascending)
				return object1.getIndexTitle().compareToIgnoreCase(object2.getIndexTitle());
			else
				return object2.getIndexTitle().compareToIgnoreCase(object1.getIndexTitle());
	}
}