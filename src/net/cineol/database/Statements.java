package net.cineol.database;

final class Statements {
	
	protected static final String DIRECTOR  = "\"Director\"";
	protected static final String PRODUCTOR = "\"Productor\"";
	protected static final String GUIONISTA = "\"Guionista\"";
	protected static final String MUSICO    = "\"Músico\"";
	protected static final String FOTOGRAFO = "\"Fotógrafo\"";

	// Data base tables.
	protected static final String TABLE_MOVIES 			= "movies";
	protected static final String TABLE_IMAGES 			= "images";
	protected static final String TABLE_VIDEOS 			= "videos";
	protected static final String TABLE_PERSONS			= "persons";
	protected static final String TABLE_MOVIES_PERSONS	= "movies_persons";
	protected static final String TABLE_CARDS_SECTION 	= "cards_section";

	
	// Movie Keys.
	protected static final String MOVIE_ID 	= "mv_id";
	protected static final String MOVIE_DURATION 			= "mg_duration";
	protected static final String MOVIE_RATING 				= "mg_rating";
	protected static final String MOVIE_VOTES 				= "mg_votes";
	protected static final String MOVIE_YEAR 				= "mg_year";
	protected static final String MOVIE_SPAIN_TAKINGS 		= "mg_spainTakings";
	protected static final String MOVIE_USA_TAKINGS 		= "mg_usaTakings";
	protected static final String MOVIE_INDEX_TITLE 		= "mg_indexTitle";
	protected static final String MOVIE_TITLE 				= "mg_title";
	protected static final String MOVIE_ORIGINAL_TITLE 		= "mg_originalTitle";
	protected static final String MOVIE_GENRE 				= "mg_genre";
	protected static final String MOVIE_FORMAT 				= "mg_format";
	protected static final String MOVIE_SYNOPSIS 			= "mg_synopsis";
	protected static final String MOVIE_COUNTRY 			= "mg_country";
	protected static final String MOVIE_URL_CINEOL 			= "mg_urlCINeol";
	protected static final String MOVIE_SPAIN_PREMIER 		= "mg_spainPremier";
	protected static final String MOVIE_ORIGINAL_PREMIER 	= "mg_originalPremier";

	// Image Keys.
	protected static final String IMAGE_ID 					= "mg_id";
	protected static final String IMAGE_THUMB_URL			= "mg_thumb_url";
	protected static final String IMAGE_IMAGE_URL			= "mg_image_url";
	protected static final String IMAGE_THUMB_PATH			= "mg_thumb_path";
	protected static final String IMAGE_IMAGE_PATH			= "mg_image_path";
	protected static final String IMAGE_ORDER				= "mg_order";
	
	
	// Video Keys.
	protected static final String VIDEO_ID 				= "vd_id";
	protected static final String VIDEO_DESCRIPTION		= "vd_description";
	protected static final String VIDEO_DATE			= "vd_date";
	
	// Person Keys.
	protected static final String PERSON_ID 			= "prsn_id";
	protected static final String PERSON_NAME			= "prsn_name";
	protected static final String PERSON_JOB			= "prsn_job";
	protected static final String PERSON_ORDER			= "prsn_order";
	protected static final String PERSON_URL_CINEOL		= "prsn_url_cineol";
	
	// Movie indexes.
	protected static final int TABLE_MOVIE_ID_INDEX					= 0;
	protected static final int TABLE_MOVIE_TITLE_INDEX 				= 1;
	protected static final int TABLE_MOVIE_INDEX_TITLE_INDEX 		= 2;
	protected static final int TABLE_MOVIE_ORIGINAL_TITLE_INDEX 	= 3;
	protected static final int TABLE_MOVIE_GENRE_INDEX 				= 4;
	protected static final int TABLE_MOVIE_FORMAT_INDEX 			= 5;
	protected static final int TABLE_MOVIE_SYNOPSIS_INDEX			= 6;
	protected static final int TABLE_MOVIE_COUNTRY_INDEX			= 7;
	protected static final int TABLE_MOVIE_URL_CINEOL_INDEX			= 8;
	protected static final int TABLE_MOVIE_DURATION_INDEX			= 9;
	protected static final int TABLE_MOVIE_RATING_INDEX				= 10;
	protected static final int TABLE_MOVIE_VOTES_INDEX 				= 11;
	protected static final int TABLE_MOVIE_YEAR_INDEX 				= 12;
	protected static final int TABLE_MOVIE_SPAIN_TAKINGS_INDEX		= 13;
	protected static final int TABLE_MOVIE_USA_TAKINGS_INDEX		= 14;
	protected static final int TABLE_MOVIE_SPAIN_PREMIER_INDEX 		= 15;
	protected static final int TABLE_MOVIE_ORIGINAL_PREMIER_INDEX	= 16;
	
	// Video indexes.
	protected static final int TABLE_VIDEO_ID_INDEX 				= 1;
	protected static final int TABLE_VIDEO_DESCRIPTION_INDEX		= 2;	
	protected static final int TABLE_VIDEO_DATE_INDEX				= 3;
	
	// Image indexes.
	protected static final int TABLE_IMAGE_ID_INDEX 				= 1;
	protected static final int TABLE_IMAGE_THUMB_URL_INDEX 			= 2;
	protected static final int TABLE_IMAGE_URL_INDEX				= 3;
	protected static final int TABLE_IMAGE_THUMB_PATH_INDEX 		= 4;
	protected static final int TABLE_IMAGE_PATH_INDEX				= 5;
	protected static final int TABLE_IMAGE_ORDER_INDEX				= 6;	

	// Person indexes.
	protected static final int TABLE_PERSON_ID_INDEX 				= 0;
	protected static final int TABLE_PERSON_NAME_INDEX	 			= 1;
	protected static final int TABLE_PERSON_URL_CINEOL_INDEX		= 2;
	protected static final int TABLE_PERSON_JOB_INDEX				= 3;
	protected static final int TABLE_PERSON_ORDER_INDEX				= 4;

	
	protected static final String CREATE_CARDS_SECTION_TABLE =
        "create table cards_section (" +
	    	MOVIE_ID 			+ " integer not null," +
	    	
	    	"constraint images_pk primary key (" + MOVIE_ID + ")," +
	    	"constraint images_fk foreign key (" + MOVIE_ID + ") references movies(" + MOVIE_ID + ") on delete cascade);";

	
    protected static final String CREATE_MOVIES_TABLE =
        "create table movies (" +
	        MOVIE_ID 				+ " integer not null," +
	
	        MOVIE_TITLE 			+ " text not null," +
	        MOVIE_INDEX_TITLE 		+ " text," +
	        MOVIE_ORIGINAL_TITLE 	+ " text," +
	        MOVIE_GENRE 			+ " text," +
	        MOVIE_FORMAT 			+ " text," +
	        MOVIE_SYNOPSIS 			+ " text," +
	        MOVIE_COUNTRY 			+ " text," +
	        MOVIE_URL_CINEOL 		+ " text," +
	        MOVIE_DURATION 			+ " integer," +
	        MOVIE_RATING 			+ " float," +
	        MOVIE_VOTES		 		+ " integer," +
	        MOVIE_YEAR 				+ " integer(4, 0)," +
	        MOVIE_SPAIN_TAKINGS 	+ " integer," +
	        MOVIE_USA_TAKINGS 		+ " integer," +
	        MOVIE_SPAIN_PREMIER 	+ " text," +
	        MOVIE_ORIGINAL_PREMIER 	+ " text," +
        	
        	"constraint movies_pk primary key (" + MOVIE_ID + "));";
    
    
    protected static final String CREATE_IMAGES_TABLE =
        "create table images (" +
        	MOVIE_ID + " integer not null," +	    	
        	IMAGE_ID + " text not null," +
	
        	IMAGE_THUMB_URL  + " text," +
        	IMAGE_IMAGE_URL  + " text," +
        	IMAGE_THUMB_PATH + " text," +
        	IMAGE_IMAGE_PATH + " text," +
        	IMAGE_ORDER		 + " integer," +

	    	"constraint images_pk primary key (" + IMAGE_ID + ")," +
	    	"constraint images_fk foreign key (" + MOVIE_ID + ") references movies(" + MOVIE_ID + ") on delete cascade);";
    
    
    protected static final String CREATE_VIDEOS_TABLE =
        "create table videos (" +
        	MOVIE_ID 			+ " integer not null," +	    	
        	VIDEO_ID 			+ " text not null," +
	
        	VIDEO_DESCRIPTION 	+ " text," +
        	VIDEO_DATE 			+ " text," +
	    	
	    	"constraint videos_pk primary key (" + VIDEO_ID + ")," +
	    	"constraint videos_fk foreign key (" + MOVIE_ID + ") references movies(" + MOVIE_ID + ") on delete cascade);";
    
    
    protected static final String CREATE_PERSONS_TABLE =
        "create table persons (" +
    		PERSON_ID 			+ " integer not null," +

	    	PERSON_NAME			+ " text not null," +
	    	PERSON_URL_CINEOL 	+ " text," +
	    	
	    	"constraint credits_pk primary key (" + PERSON_ID + "));";
    
    
    protected static final String CREATE_MOVIES_PERSONS_TABLE =
        "create table movies_persons (" +
    		MOVIE_ID  + " integer not null," +	    	
    		PERSON_ID + " integer not null," +
	
	    	PERSON_JOB   + " text not null," +
	    	PERSON_ORDER + " integer," +
	    	
	    	"constraint jobs_pk primary key (" + PERSON_JOB + "," + PERSON_ID + "," + MOVIE_ID + ")," +
	    	"constraint jobs_fk1 foreign key (" + PERSON_ID + ") references persons(" + PERSON_ID + ") on delete cascade," +
	    	"constraint jobs_fk2 foreign key (" + MOVIE_ID + ") references movies(" + MOVIE_ID + ") on delete cascade);";
}