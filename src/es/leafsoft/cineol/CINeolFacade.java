package es.leafsoft.cineol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import bit.ly.model.BitlyURL;
import bit.ly.webservice.BitlyManager;
import bit.ly.webservice.BitlyManagerObserver;
import bit.ly.xml.XMLParserDelegateAndroid;
import bit.ly.xml.XMLParserGetBitlyURL;
import bit.ly.xml.XMLParserObserver;

import com.flurry.android.FlurryAgent;

import es.leafsoft.sdcard.FileUtils;

import net.cineol.database.CINeolDatabase;
import net.cineol.model.Image;
import net.cineol.model.Movie;
import net.cineol.model.Person;
import net.cineol.model.Video;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.widget.Toast;

public class CINeolFacade {

	//public static final String API_Key_Flurry = "43M5XV7QJNCEDKNLVUUJ"; 	// Public API
	public static final String API_Key_Flurry = "GZCSATUCRW6QLSHVTW9X";		// Dev API
	
	public static final String FlurryEventShowMovie	  		= "CINeolShowMovieEvent";
	public static final String FlurryEventShowMoviePoster	= "CINeolShowMoviePosterEvent";
	public static final String FlurryEventShowMovieDetails 	= "CINeolShowMovieDetailsEvent";
	public static final String FlurryEventShowMovieComent 	= "CINeolShowMovieComentEvent";
	public static final String FlurryEventShowMovieGallery 	= "CINeolShowMovieGalleryEvent";
	public static final String FlurryEventShowMovieOnCINeol	= "CINeolShowMovieOnCINeolEvent";
	public static final String FlurryEventSendMovie			= "CINeolSendMovieEvent";

	public static final String FlurryEventShowNews	  		= "CINeolShowNewsEvent";
	public static final String FlurryEventShowNewsOnCINeol	= "CINeolShowNewsOnCINeolEvent";
	public static final String FlurryEventSendNews			= "CINeolSendNewsEvent";

	public static final String FlurryEventSearchMovie 		= "CINeolSearchMovieEvent";
	public static final String FlurryEventSaveMovie	  		= "CINeolSaveMovieEvent";

	public static final String FlurryEventShowGallery		= "CINeolShowGalleryEvent";
	public static final String FlurryEventShowAbout			= "CINeolShowAboutEvent";
	
	public static final String FlurryEventClickLeafsoftLink	= "CINeolClickLeafsoftLinkEvent";
	public static final String FlurryEventClickCINeolLink	= "CINeolClickCINeolLinkEvent";
	public static final String FlurryEventClickAndroidIconsLink = "CINeolClickAndroidIconsLinkEvent";

	public static final String FlurryErrorImageDidNotLoad 	= "CINeolImageDidNotLoadError";

	protected static Context context;
	
	public static void initFacade(Context context) {
		CINeolFacade.context = context;
	}
	
	
    public static void showMovieOnCINeol(Context context, Movie movie) {
		HashMap<String, String> value = new HashMap<String, String>();
		value.put("ID película", movie.getId() + "");
		value.put("Título película", movie.getTitle());
		value.put("URL CINeol", movie.getUrlCINeol());

		FlurryAgent.onEvent(CINeolFacade.FlurryEventShowMovieOnCINeol, value);
		
		Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getUrlCINeol()));   
		context.startActivity(viewIntent);
    }
    
    
    public static void sendMovie(final Context context, final Movie movie) {
		HashMap<String, String> value = new HashMap<String, String>();
		value.put("ID película", movie.getId() + "");
		value.put("Título película", movie.getTitle());
		value.put("URL CINeol", movie.getUrlCINeol());

		FlurryAgent.onEvent(CINeolFacade.FlurryEventSendMovie, value);
		
        final Intent sendIntent = new Intent(Intent.ACTION_SEND); 
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, movie.getTitle());        
        sendIntent.setType("text/plain"); 

        BitlyManager.sharedManager().addObserver(new BitlyManagerObserver() {

			public void bitlyDidFinishGetShortenURL(final InputStream data) {
				if (data == null) {
			        sendIntent.putExtra(Intent.EXTRA_TEXT, movie.getUrlCINeol()); 
			        context.startActivity(Intent.createChooser(sendIntent, "Compartir noticia:"));
				}
				else {
					XMLParserGetBitlyURL parser = new XMLParserGetBitlyURL(data, new XMLParserDelegateAndroid(), new XMLParserObserver() {
	
						public void xmlParserDidEndDocument(bit.ly.xml.XMLParser parser) {
							BitlyURL url = ((XMLParserGetBitlyURL) parser).getBitlyURL();
					        sendIntent.putExtra(Intent.EXTRA_TEXT, url.getUrl()); 
					        context.startActivity(Intent.createChooser(sendIntent, "Compartir noticia:"));
						}
						
						public void xmlParserDidEndTag(bit.ly.xml.XMLParser arg0, String arg1) {}
						public void xmlParserDidFindAttribute(bit.ly.xml.XMLParser arg0, String arg1, String arg2) {}
						public void xmlParserDidStartDocument(bit.ly.xml.XMLParser arg0) {}
					});
					
					parser.parse();
					BitlyManager.sharedManager().removeObserver(this, BitlyManager.kBitlyDidFinishGetShortenURLNotification);
				}
			}
        }, BitlyManager.kBitlyDidFinishGetShortenURLNotification);
        
        BitlyManager.sharedManager().getShortenURL(movie.getUrlCINeol());
    }
    
	
	public static ArrayList<Movie> searchMovies(String query) {
		
		ArrayList<Movie> movies = CINeolDatabase.sharedDatabase().searchMovies(query);
		
		 //TODO
		if (movies != null && movies.size() > 0) {
			 for (Movie movie : movies) {
				 movie.setActors(CINeolDatabase.sharedDatabase().getActorsFromMovie(movie.getId()));
				 movie.setCredits(CINeolDatabase.sharedDatabase().getCreditsFromMovie(movie.getId()));
				 movie.setImages(CINeolDatabase.sharedDatabase().getImagesFromMovie(movie.getId()));
				 movie.setVideos(CINeolDatabase.sharedDatabase().getVideosFromMovie(movie.getId()));
				 movie.setPosterThumbnail(CINeolFacade.loadPosterThumbnail(movie.getId()));
			 }
		}
		
		return movies;
	}
	
	public static boolean storeMovie(Movie movie) {
    	boolean result = CINeolDatabase.sharedDatabase().storeMovie(movie);

    	if (result) {
    		result = CINeolDatabase.sharedDatabase().addMovieToCardsSection(movie.getId());

    		CINeolFacade.storePersonsAndAddToMovie(movie.getActors(), movie.getId());
    		CINeolFacade.storePersonsAndAddToMovie(movie.getCredits(), movie.getId());
    		CINeolFacade.storeImages(movie.getImages(), movie.getId());
    		CINeolFacade.storeVideos(movie.getVideos(), movie.getId());
    		CINeolFacade.storePosterThumbnail(movie.getId(), movie.getPosterThumbnail());
    	}
    	
    	String text = "";
    	if (result) {
        	text = "Ficha guardada";
    	
    		HashMap<String, String> value = new HashMap<String, String>();
    		value.put("ID película", movie.getId() + "");
    		value.put("Título película", movie.getTitle());
    		value.put("URL CINeol", movie.getUrlCINeol());
    		
    		FlurryAgent.onEvent(CINeolFacade.FlurryEventSaveMovie, value);
    	}
    	else
        	text = "No ha sido posible guardar la ficha";

    	CINeolFacade.showToastMessage(text);
    	
    	return result;
	}

	public static boolean deleteMovie(long movieId) {
    	boolean result = CINeolDatabase.sharedDatabase().deleteMovie(movieId);
    	
    	if (result) {
    		CINeolDatabase.sharedDatabase().deleteMovieFromCardsSection(movieId);
    		CINeolDatabase.sharedDatabase().deleteMovieFromPersons(movieId);
    		CINeolDatabase.sharedDatabase().deleteImages(movieId);
    		CINeolDatabase.sharedDatabase().deleteVideos(movieId);
    		//TODO delete thumb poster
    	}
    	
    	String text = "";
    	if (result)
        	text = "Ficha borrada";
   		else
        	text = "No ha sido posible borrar la ficha";
    	
    	int duration = Toast.LENGTH_SHORT;
    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();
    	
    	return result;
    }
	
	public static boolean isMovieStored(long movieId) {
		return CINeolDatabase.sharedDatabase().isMovieStored(movieId);
	}
	
	public static ArrayList<Movie> getMoviesOnCardsSection() {
		 ArrayList<Movie> movies = CINeolDatabase.sharedDatabase().getMoviesOnCardsSection();
		 
		 //TODO
		 for (Movie movie : movies) {
			 movie.setActors(CINeolDatabase.sharedDatabase().getActorsFromMovie(movie.getId()));
			 movie.setCredits(CINeolDatabase.sharedDatabase().getCreditsFromMovie(movie.getId()));
			 movie.setImages(CINeolDatabase.sharedDatabase().getImagesFromMovie(movie.getId()));
			 movie.setVideos(CINeolDatabase.sharedDatabase().getVideosFromMovie(movie.getId()));
			 movie.setPosterThumbnail(CINeolFacade.loadPosterThumbnail(movie.getId()));
		 }
		 
		 return movies;
	}
	
	public static void storePosterThumbnail(long movieId, BitmapDrawable posterThumbnail) {
		if (posterThumbnail == null)
			return;
		
		String path = "/sdcard/CINeol/thumbs/thumb_poster_" + movieId + ".jpg";	
		
		try {
			FileUtils.writeJPEG(posterThumbnail.getBitmap(), path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static BitmapDrawable loadPosterThumbnail(long movieId) {
		String path = "/sdcard/CINeol/thumbs/thumb_poster_" + movieId + ".jpg";
		
		if (new File(path).exists())
			return new BitmapDrawable(path);
		else
			return null;
	}

	public static void storePersonsAndAddToMovie(ArrayList<Person> persons, long movieId) {
		int order = 0;
		for (Person person : persons) {
			CINeolDatabase.sharedDatabase().storePerson(person);
			CINeolDatabase.sharedDatabase().addPersonToMovie(person.getId(), movieId, person.getJob(), order);
			order++;
		}
	}
	
    public static void storeImages(ArrayList<Image> images, long movieId) {
		int order = 0;
		for (Image image : images) {
			CINeolDatabase.sharedDatabase().storeImage(image, movieId, order);
			order++;
		}
	}
    
    public static void storeVideos(ArrayList<Video> videos, long movieId) {
		int order = 0;
		for (Video video : videos) {
			CINeolDatabase.sharedDatabase().storeVideo(video, movieId);
			order++;
		}
	}
	
	//*****************************
	// Private Methods.
	//*****************************
    private static void showToastMessage(String msg) {
    	int duration = Toast.LENGTH_SHORT;
    	Toast toast = Toast.makeText(CINeolFacade.context, msg, duration);
    	toast.show();
    }
    
    private static void showToastMessage(int msg) {
    	int duration = Toast.LENGTH_SHORT;
    	Toast toast = Toast.makeText(CINeolFacade.context, msg, duration);
    	toast.show();
    }
}
