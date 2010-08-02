package net.cineol.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import es.leafsoft.database.Database;

import static net.cineol.database.Statements.*;
import net.cineol.model.Image;
import net.cineol.model.Movie;
import net.cineol.model.Person;
import net.cineol.model.Video;
import net.cineol.utils.CINeolUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class CINeolDatabase extends Database {

	private static final String TAG = "CINeolDataBase";

    public static final String DATABASE_NAME = "CINeolDataBase";
    public static final int DATABASE_VERSION = 1;
    
    public static final String CINEOL_PATH = "/sdcard/CINeol";
    public static final String CINEOL_THUMBS_PATH = CINEOL_PATH + "/thumbs";
    
    private final Context context;
    private DatabaseHelper databaseHelper;
    
    private static CINeolDatabase singleton;
    
	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(Statements.CREATE_MOVIES_TABLE);
			db.execSQL(Statements.CREATE_VIDEOS_TABLE);			
			db.execSQL(Statements.CREATE_IMAGES_TABLE);		
			db.execSQL(Statements.CREATE_PERSONS_TABLE);
			db.execSQL(Statements.CREATE_CARDS_SECTION_TABLE);						
			db.execSQL(Statements.CREATE_MOVIES_PERSONS_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		}
	}
	
	private CINeolDatabase(Context context) {
		this.context = context;
	}
	
	public static void initDatabase(Context context) {
		if (singleton == null)
			singleton = new CINeolDatabase(context).open();
		
		new File(CINEOL_THUMBS_PATH).mkdirs();
	}
	
	public static CINeolDatabase sharedDatabase() {
		return singleton;
	}
	
    public CINeolDatabase open() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
        
        return this;
    }
    
    public void close() {
    	databaseHelper.close();
    }
    
    
    
    //**
    //**	Movies Data Base Methods.
    //**
    public boolean storeMovie(Movie movie) {
    	ContentValues values = new ContentValues();
    	
    	values.put(Statements.MOVIE_ID, movie.getId());
    	
    	values.put(Statements.MOVIE_COUNTRY, movie.getCountry());
    	values.put(Statements.MOVIE_DURATION, movie.getDuration());
    	values.put(Statements.MOVIE_FORMAT, movie.getFormat());
    	values.put(Statements.MOVIE_GENRE, movie.getGenre());
    	values.put(Statements.MOVIE_INDEX_TITLE, movie.getIndexTitle());
    	values.put(Statements.MOVIE_ORIGINAL_PREMIER,
    			   CINeolUtils.stringFromDate(movie.getOriginalPremier(), "dd-MMM-yyyy"));
    	values.put(Statements.MOVIE_ORIGINAL_TITLE, movie.getOriginalTitle());
    	values.put(Statements.MOVIE_RATING, movie.getRating());
    	values.put(Statements.MOVIE_SPAIN_PREMIER,
    			   CINeolUtils.stringFromDate(movie.getSpainPremier(), "dd-MMM-yyyy"));
    	values.put(Statements.MOVIE_SPAIN_TAKINGS, movie.getSpainTakings());
    	values.put(Statements.MOVIE_SYNOPSIS, movie.getSynopsis());
    	values.put(Statements.MOVIE_TITLE, movie.getTitle());
    	values.put(Statements.MOVIE_URL_CINEOL, movie.getUrlCINeol());
    	values.put(Statements.MOVIE_USA_TAKINGS, movie.getUsaTakings());
    	values.put(Statements.MOVIE_VOTES, movie.getVotes());
    	values.put(Statements.MOVIE_YEAR, movie.getYear());
    	
    	
    	long result;
    	if (this.isMovieStored(movie.getId())) {
    		String where = Statements.MOVIE_ID + " = " + movie.getId();
    		result = database.update(Statements.TABLE_MOVIES, values, where, null);
    		
    		return (result > 0);
    	}
    	else {
    		result = database.insert(Statements.TABLE_MOVIES, null, values);

    		return (result > -1);
    	}
    }
    
    public boolean deleteMovie(long movieId) {
		return this.deleteItems(movieId, Statements.MOVIE_ID, Statements.TABLE_MOVIES);
	}
    
    public boolean addMovieToCardsSection(long movieId) {
    	ContentValues values = new ContentValues();
    	
    	values.put(Statements.MOVIE_ID, movieId);
    	long result = database.insert(Statements.TABLE_CARDS_SECTION, null, values);

    	return (result > -1);
    }
    
	public boolean deleteMovieFromCardsSection(long movieId) {
		return this.deleteItems(movieId, Statements.MOVIE_ID, Statements.TABLE_CARDS_SECTION);
	}
	
	public boolean deleteMovieFromPersons(long movieId) {
		return this.deleteItems(movieId, Statements.MOVIE_ID, Statements.TABLE_MOVIES_PERSONS);
	}
    
    public boolean isMovieStored(long movieId) {
    	return this.isItemStored(movieId, Statements.MOVIE_ID, Statements.TABLE_MOVIES);
    }
    
    public Movie getMovie(long movieId) {
		Movie movie = null;		
		String where = Statements.MOVIE_ID + "=" + movieId;
    	
    	Cursor cursor =  database.query(Statements.TABLE_MOVIES, null, where, null, null, null, null);
    	
    	if (cursor != null && cursor.getCount() > 0) {
    		cursor.moveToFirst();
    		movie = this.movieFromCursor(cursor); 		
    		cursor.close();
    	}
    	
    	return movie;
    }
    
    public ArrayList<Movie> getMovies() {
    	ArrayList<Movie> movies = new ArrayList<Movie>();
    	//String order = Statements, + " asc";
    	
    	Cursor cursor =  database.query(Statements.TABLE_MOVIES, null, null, null, null, null, null);
    	
    	if (cursor != null && cursor.getCount() > 0) {
    		cursor.moveToFirst();
    		
    		do {
    			movies.add(this.movieFromCursor(cursor));
    		} while(cursor.moveToNext());
    		
    		cursor.close();
    	}
    	
    	return movies;   
    }
    
    public ArrayList<Movie> getMoviesOnCardsSection() {
    	
    	ArrayList<Movie> movies = new ArrayList<Movie>();
    	//String order = Statements, + " asc";
    	
    	String selection = Statements.MOVIE_ID + " in (select " + Statements.MOVIE_ID +
    												 " from " + Statements.TABLE_CARDS_SECTION + ")";
    	
    	Cursor cursor =  database.query(Statements.TABLE_MOVIES, null, selection, null, null, null, null);
    	
    	if (cursor != null && cursor.getCount() > 0) {
    		cursor.moveToFirst();
    		
    		do {
    			movies.add(this.movieFromCursor(cursor));
    		} while(cursor.moveToNext());
    		
    		cursor.close();
    	}
    	
    	return movies;    	
    }
    
    public ArrayList<Movie> searchMovies(String query) {
    	ArrayList<Movie> movies = new ArrayList<Movie>();

    	String where = MOVIE_TITLE + " LIKE " + "'%" + query + "%'";
    	
    	Cursor cursor = database.query(true, TABLE_MOVIES, null, where, null, null, null, null, null);
    	
    	if (cursor != null) {
    		
    		if (cursor.getCount() > 0) {
	    		cursor.moveToFirst();
	    		
	    		do {
	    			movies.add(this.movieFromCursor(cursor));
	    		} while(cursor.moveToNext());
    		}
    		
    		cursor.close();
    	}
    	
    	return movies;
    }
        
    
    
    //**
    //**	Persons Data Base Methods.
    //**
    public boolean storePerson(Person person) {
    	
    	ContentValues values = new ContentValues();
    	
    	values.put(Statements.PERSON_ID, person.getId());
    	values.put(Statements.PERSON_NAME, person.getName());
    	values.put(Statements.PERSON_URL_CINEOL, person.getUrlCINeol());
    	
    	long result;
    	if (this.isPersonStored(person.getId())) {
    		String where = Statements.PERSON_ID + " = " + person.getId();
    		result = database.update(Statements.TABLE_PERSONS, values, where, null);
    		
    		return (result > 0);
    	}
    	else {
    		result = database.insert(Statements.TABLE_PERSONS, null, values);

    		return (result > -1);
    	}
    }
    
    public boolean addPersonToMovie(long personId, long movieID, String personJob, int personOrder) {
    	ContentValues values = new ContentValues();
    	
    	values.put(Statements.PERSON_ID, personId);
    	values.put(Statements.MOVIE_ID, movieID);
    	values.put(Statements.PERSON_JOB, personJob);
    	values.put(Statements.PERSON_ORDER, personOrder);

    	long result = database.insert(Statements.TABLE_MOVIES_PERSONS, null, values);

    	return (result > -1);
    }
    
    public boolean deletePerson(long personId) {
		return this.deleteItems(personId, Statements.PERSON_ID, Statements.TABLE_PERSONS);
    }
    
	public boolean deletePersonFromMovies(long personId) {
		return this.deleteItems(personId, Statements.PERSON_ID, Statements.TABLE_MOVIES_PERSONS);
	}
	
    public boolean isPersonStored(long personId) {
    	return this.isItemStored(personId, Statements.PERSON_ID, Statements.TABLE_PERSONS);
    }
    
    public Person getPerson(long personId) {
    	Person person = null;
    	Cursor cursor = this.getCursorForItem(personId, Statements.PERSON_ID, Statements.TABLE_PERSONS);

    	if (cursor != null && cursor.getCount() > 0) {
    		cursor.moveToFirst();
    		person = this.personFromCursor(cursor); 		
    		cursor.close();
    	}
    	
    	return person;
    }
    
    public ArrayList<Person> getCreditsFromMovie(long movieId) {
    	ArrayList<Person> persons = new ArrayList<Person>();
    	
       	String query = "select " + TABLE_PERSONS + "." + PERSON_ID + ", "    +
       							   TABLE_PERSONS + "." + PERSON_NAME + ", "  +
       							   TABLE_PERSONS + "." + PERSON_URL_CINEOL + ", "  +
       							   TABLE_MOVIES_PERSONS + "." + PERSON_JOB + ", " +
       							   TABLE_MOVIES_PERSONS + "." + PERSON_ORDER + 
       							   			
       				  " from " + TABLE_PERSONS + ", " + TABLE_MOVIES_PERSONS +
       				  
       				  " where "  + TABLE_PERSONS + "." + PERSON_ID + " = " + TABLE_MOVIES_PERSONS + "." + PERSON_ID + " and " +
       				  			   MOVIE_ID + " = " + movieId + " and (" + PERSON_JOB + " like " + DIRECTOR + " or " +
       				  													   PERSON_JOB + " like " + PRODUCTOR + " or " +
       				  													   PERSON_JOB + " like " + GUIONISTA + " or " +
       				  													   PERSON_JOB + " like " + MUSICO + " or " +
       				  													   PERSON_JOB + " like " + FOTOGRAFO + ")" +
 					  " order by " + PERSON_ORDER;
       		
    	Cursor cursor = database.rawQuery(query, null);
    	
    	if (cursor != null) {
    		cursor.moveToFirst();
    		
    		do {
    			persons.add(this.personFromCursor(cursor));
    		} while (cursor.moveToNext());    
    		
    		cursor.close();
    	}
    	
    	return persons;
    }
    
    public ArrayList<Person> getActorsFromMovie(long movieId) {
    	ArrayList<Person> persons = new ArrayList<Person>();
    	
       	String query = "select " + TABLE_PERSONS + "." + PERSON_ID + ", "    +
       							   TABLE_PERSONS + "." + PERSON_NAME + ", "  +
       							   TABLE_PERSONS + "." + PERSON_URL_CINEOL + ", "  +
       							   TABLE_MOVIES_PERSONS + "." + PERSON_JOB + ", " +
       							   TABLE_MOVIES_PERSONS + "." + PERSON_ORDER + 
       							   			
       				  " from " + TABLE_PERSONS + ", " + TABLE_MOVIES_PERSONS +
       				  
       				  " where "  + TABLE_PERSONS + "." + PERSON_ID + " = " + TABLE_MOVIES_PERSONS + "." + PERSON_ID + " and " +
       				  			   MOVIE_ID + " = " + movieId + " and (" + PERSON_JOB + " not like " + DIRECTOR + " and " +
       				  													   PERSON_JOB + " not like " + PRODUCTOR + " and " +
       				  													   PERSON_JOB + " not like " + GUIONISTA + " and " +
       				  													   PERSON_JOB + " not like " + MUSICO + " and " +
       				  													   PERSON_JOB + " not like " + FOTOGRAFO + ")" +
 					  " order by " + PERSON_ORDER;
       		
    	Cursor cursor = database.rawQuery(query, null);
    	
    	if (cursor != null) {
    		cursor.moveToFirst();
    		
    		do {
    			persons.add(this.personFromCursor(cursor));
    		} while (cursor.moveToNext());    
    		
    		cursor.close();
    	}
    	
    	return persons;
    }

	public ArrayList<Person> getPersons() {
		return null;
    }
    
    //TODO
    public ArrayList<Person> getPersonsOnPeopleSection() {
		return null;
    }  
    
    
    //******************************
    //** Images Data Base Methods **
    //******************************
    public boolean storeImage(Image image, long movieId, int order) {
    	ContentValues values = new ContentValues();
    	
    	values.put(Statements.MOVIE_ID, movieId);    	
    	values.put(Statements.IMAGE_ID, image.getId());
    	values.put(Statements.IMAGE_IMAGE_PATH, image.getPathImage());
    	values.put(Statements.IMAGE_IMAGE_URL, image.getUrlImage());
    	values.put(Statements.IMAGE_THUMB_PATH, image.getPathThumbImage());
    	values.put(Statements.IMAGE_THUMB_URL, image.getUrlThumbImage());
    	values.put(Statements.IMAGE_ORDER, order);

    	long result;
    	if (this.isImageStored(image.getId())) {
    		String where = Statements.IMAGE_ID + " like " + "\"" + image.getId() + "\"";
    		result = database.update(Statements.TABLE_IMAGES, values, where, null);
    		
    		return (result > 0);
    	}
    	else {
    		result = database.insert(Statements.TABLE_IMAGES, null, values);

    		return (result > -1);
    	}
    }
    
    public boolean deleteImages(long movieId) {
    	return this.deleteItems(movieId, MOVIE_ID, TABLE_IMAGES);
    }
    
    public boolean isImageStored(String imageId) {
		return this.isItemStored(imageId, IMAGE_ID, TABLE_IMAGES);
    }
    
    public Movie getImage(String imageId) {
		return null;    	
    }
    
    public ArrayList<Image> getImagesFromMovie(long movieId) {		
		ArrayList<Image> images = new ArrayList<Image>();
    	Cursor cursor = this.getCursorForItem(movieId, Statements.MOVIE_ID, Statements.TABLE_IMAGES);

    	if (cursor != null && cursor.getCount() > 0) {
    		cursor.moveToFirst();
    		
    		do {
    			images.add(this.imageFromCursor(cursor)); 	
    		} while (cursor.moveToNext());
    		
    		cursor.close();
    	}
    	
    	return images;
    }
    
    
    //******************************
    //** Videos Data Base Methods **
    //******************************
    public boolean storeVideo(Video video, long movieId) {
    	ContentValues values = new ContentValues();
    	
    	values.put(Statements.MOVIE_ID, movieId);    	
    	values.put(Statements.VIDEO_ID, video.getId());
    	values.put(Statements.VIDEO_DESCRIPTION, video.getDescription());
    	values.put(Statements.VIDEO_DATE, CINeolUtils.stringFromDate(video.getDate(), "dd-MMM-yyyy"));


    	long result;
    	if (this.isVideoStored(video.getId())) {
    		String where = Statements.VIDEO_ID + " like " + "\"" + video.getId() + "\"";
    		result = database.update(Statements.TABLE_VIDEOS, values, where, null);
    		
    		return (result > 0);
    	}
    	else {
    		result = database.insert(Statements.TABLE_VIDEOS, null, values);

    		return (result > -1);
    	}
    }
    
    public ArrayList<Video> getVideosFromMovie(long movieId) {		
		ArrayList<Video> videos = new ArrayList<Video>();
    	Cursor cursor = this.getCursorForItem(movieId, Statements.MOVIE_ID, Statements.TABLE_VIDEOS);

    	if (cursor != null && cursor.getCount() > 0) {
    		cursor.moveToFirst();
    		    		
    		do {
    			videos.add(this.videoFromCursor(cursor)); 	
    		} while (cursor.moveToNext());
    		
    		cursor.close();
    	}
    	
    	return videos;
    }
    
    public boolean isVideoStored(String videoId) {
		return this.isItemStored(videoId, VIDEO_ID, TABLE_VIDEOS);
    }
    
    public boolean deleteVideos(long movieId) {
    	return this.deleteItems(movieId, MOVIE_ID, TABLE_VIDEOS);
    }


	private Movie movieFromCursor(Cursor cursorMovie) {
    	Movie movie = null;
    	
		Date date = null;
		movie = new Movie(cursorMovie.getInt(Statements.TABLE_MOVIE_ID_INDEX));  		
	
		movie.setCountry(cursorMovie.getString(Statements.TABLE_MOVIE_COUNTRY_INDEX));
		movie.setDuration(cursorMovie.getInt(Statements.TABLE_MOVIE_DURATION_INDEX));
		movie.setFormat(cursorMovie.getString(Statements.TABLE_MOVIE_FORMAT_INDEX));
		movie.setGenre(cursorMovie.getString(Statements.TABLE_MOVIE_GENRE_INDEX));
		movie.setIndexTitle(cursorMovie.getString(Statements.TABLE_MOVIE_INDEX_TITLE_INDEX));
		movie.setOriginalTitle(cursorMovie.getString(Statements.TABLE_MOVIE_ORIGINAL_TITLE_INDEX));
		movie.setRating(cursorMovie.getFloat(Statements.TABLE_MOVIE_RATING_INDEX));
		movie.setSpainTakings(cursorMovie.getInt(Statements.TABLE_MOVIE_SPAIN_TAKINGS_INDEX));
		movie.setSynopsis(cursorMovie.getString(Statements.TABLE_MOVIE_SYNOPSIS_INDEX));
		movie.setTitle(cursorMovie.getString(Statements.TABLE_MOVIE_TITLE_INDEX));
		movie.setUrlCINeol(cursorMovie.getString(Statements.TABLE_MOVIE_URL_CINEOL_INDEX));	
		movie.setUsaTakings(cursorMovie.getInt(Statements.TABLE_MOVIE_USA_TAKINGS_INDEX));	
		movie.setVotes(cursorMovie.getInt(Statements.TABLE_MOVIE_VOTES_INDEX));	
		movie.setYear(cursorMovie.getInt(Statements.TABLE_MOVIE_YEAR_INDEX));	
		
		date = CINeolUtils.dateFromString(cursorMovie.getString(Statements.TABLE_MOVIE_ORIGINAL_PREMIER_INDEX), "dd-MMM-yyyy");
		movie.setOriginalPremier(date);
		
		date = CINeolUtils.dateFromString(cursorMovie.getString(Statements.TABLE_MOVIE_SPAIN_PREMIER_INDEX), "dd-MMM-yyyy");
		movie.setSpainPremier(date); 
		
    	return movie;
    }
    
    protected Person personFromCursor(Cursor cursor) {

    	Person person = new Person();
    	
    	person.setId(cursor.getLong(Statements.TABLE_PERSON_ID_INDEX));
    	person.setName(cursor.getString(Statements.TABLE_PERSON_NAME_INDEX));
    	person.setUrlCINeol(cursor.getString(Statements.TABLE_PERSON_URL_CINEOL_INDEX));
    	person.setJob(cursor.getString(Statements.TABLE_PERSON_JOB_INDEX));
    	
    	return person;
	}
    
    
    private Image imageFromCursor(Cursor cursor) {
    	Image image = new Image();
    	
    	image.setId(cursor.getString(Statements.TABLE_IMAGE_ID_INDEX));
    	image.setPathImage(cursor.getString(Statements.TABLE_IMAGE_PATH_INDEX));
    	image.setPathThumbImage(cursor.getString(Statements.TABLE_IMAGE_THUMB_PATH_INDEX));
    	image.setUrlImage(cursor.getString(Statements.TABLE_IMAGE_URL_INDEX));
    	image.setUrlThumbImage(cursor.getString(Statements.TABLE_IMAGE_THUMB_URL_INDEX));
    	
    	return image;
    }
    
    private Video videoFromCursor(Cursor cursor) {
    	Video video = new Video();
    	
    	video.setId(cursor.getString(Statements.TABLE_VIDEO_ID_INDEX));
    	video.setDescription(cursor.getString(Statements.TABLE_VIDEO_DESCRIPTION_INDEX));
    	video.setDate(CINeolUtils.dateFromString(cursor.getString(Statements.TABLE_VIDEO_DATE_INDEX), "dd-MMM-yyyy"));

    	return video;
    }
}