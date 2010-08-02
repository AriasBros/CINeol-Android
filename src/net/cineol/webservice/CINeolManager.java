package net.cineol.webservice;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import net.cineol.model.Image;

import es.leafsoft.utils.InputStreamUtils;

import android.os.Handler;
import android.util.Log;


abstract class TaskCINeol implements Runnable {

	private static String TAG = "TaskCINeol";
	protected String url;
	
	TaskCINeol() {
		this.url = null;
	}
	
	TaskCINeol(String anURL) {
		this.url = anURL;
	}
	
	abstract public void run();
	
	protected static void setTag(String tag) {
		TAG = tag;
	}

	protected static String getTag() {
		return TAG;
	}

	public InputStream getInputStream() {
		
		InputStream data = null;
		try {
			if (Thread.interrupted())
				throw new InterruptedException();
			
			URL url = new URL(this.url);
			URLConnection connection = url.openConnection();
			
			if (Thread.interrupted())
				throw new InterruptedException();
			
			data = connection.getInputStream();
		} catch (MalformedURLException e) {
			Log.e(getTag(), "MalformedURLException", e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(getTag(), "IOException", e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			Log.e(getTag(), "InterruptedException", e);
			e.printStackTrace();
		}

		return data;
	}
}

class TaskGetMovie extends TaskCINeol {
	
	private static final String TAG = "TaskGetMovie";
	
	TaskGetMovie(String url) {
		super(url);
		super.setTag(TAG);
	}
	
	public void run() {
		InputStream data = this.getInputStream();
		CINeolManager.sharedManager()._cineolDidFinishLoadingMovie(data);
	}
}


class TaskGetMovieShowtimes extends TaskCINeol {
	
	private static final String TAG = "TaskGetMovieShowtimes";
	
	TaskGetMovieShowtimes(String url) {
		super(url);
		super.setTag(TAG);
	}
	
	public void run() {
		InputStream data = this.getInputStream();		
		CINeolManager.sharedManager()._cineolDidFinishLoadingMovieShowtimes(data);
	}
}

class TaskGetComments extends TaskCINeol {
	
	private static final String TAG = "TaskGetComments";
	
	TaskGetComments(String url) {
		super(url);
		super.setTag(TAG);
	}
	
	public void run() {
		InputStream data = this.getInputStream();
		CINeolManager.sharedManager()._cineolDidFinishLoadingComments(data);
	}
}

class TaskGetNews extends TaskCINeol {
	private static final String TAG = "TaskGetNews";
	
	TaskGetNews(String url) {
		super(url);
		super.setTag(TAG);
	}
	
	public void run() {
		InputStream data = this.getInputStream();
		CINeolManager.sharedManager()._cineolDidFinishLoadingNews(data);
	}
}

class TaskGetSingleNews extends TaskCINeol {
	private static final String TAG = "TaskGetSingleNews";
	
	TaskGetSingleNews(String url) {
		super(url);
		super.setTag(TAG);
	}
	
	public void run() {
		InputStream data = this.getInputStream();
		CINeolManager.sharedManager()._cineolDidFinishLoadingSingleNews(data);
	}
}

class TaskSearchMovies extends TaskCINeol {
	
	private static final String TAG = "TaskSearchMovies";
	
	TaskSearchMovies(String url) {
		super(url);
		super.setTag(TAG);
	}
	
	public void run() {
		InputStream data = this.getInputStream();
		CINeolManager.sharedManager()._cineolDidFinishSearchMovies(data);
	}
}


class TaskGetThumbnail extends TaskCINeol {
	
	private static final String TAG = "TaskGetThumbnail";
	
	TaskGetThumbnail(String url) {
		super(url);
		super.setTag(TAG);
	}
	
	public void run() {
		InputStream data = this.getInputStream();
		CINeolManager.sharedManager()._cineolDidFinishDownloadThumbnail(data);
	}
}

class TaskGetImage extends TaskCINeol {
	
	private static final String TAG = "TaskGetImage";
	
	TaskGetImage(String url) {
		super(url);
		super.setTag(TAG);
	}
	
	public void run() {
		InputStream data = this.getInputStream();
		CINeolManager.sharedManager()._cineolDidFinishDownloadImage(data);
	}
}

class TaskGetThumbnails extends TaskCINeol {
	
	private static final String TAG = "TaskGetThumbnails";
	private ArrayList<Image> images;
	
	
	TaskGetThumbnails(ArrayList<Image> images) {
		super();
		this.images = images;
		super.setTag(TAG);
	}
	
	public void run() {
		
		for (Image image : images) {
			this.url = "http://www.cineol.net/" + image.getUrlThumbImage();
			InputStream data = this.getInputStream();
			CINeolManager.sharedManager()._cineolDidFinishDownloadThumbnail(data);
		}
		
		CINeolManager.sharedManager()._cineolDidFinishDownloadThumbnails();
	}
}


public class CINeolManager {
	private static final String TAG = "CINeolManager";

	// Singleton instance.
	static private CINeolManager sharedManager = null;

	// Parameters and URL Bases.
	static private String kAPIKeyParameter = null;
	
	static private final String kURLBaseMovie 			= "http://www.cineol.net/api/peliculaxml.php?apiKey=";
	static private final String kURLBaseMovieShowtimes 	= "http://www.cineol.net/api/estrenos.php?apiKey=";
	static private final String kURLBaseComments 		= "http://www.cineol.net/api/getcomentarios.php?apiKey=";
	static private final String kURLBaseNews	 		= "http://www.cineol.net/api/getNoticiasPortada.php?apiKey=";
	static private final String kURLBaseSingleNews	 	= "http://www.cineol.net/api/getNoticia.php?apiKey=";
	static private final String kURLBaseSearchMovies	= "http://www.cineol.net/xmltest.php?search=";
	static private final String kURLBaseGetThumbnail	= "http://www.cineol.net/";
	static private final String kURLBaseGetImage		= kURLBaseGetThumbnail;

	static private final String kURLBaseDailymotion 	= "http://iphone.dailymotion.com/video/";
	
	// Threads.
	private Handler mainThread;
	
	private ExecutorService threadGetMovieWithID;
	private Future<?>		pendingGetMovieWithID;
	
	private ExecutorService threadGetMovieShowtimes;
	private Future<?>		pendingGetMovieShowtimes;
	
	private ExecutorService threadGetComments;
	private Future<?>		pendingGetComments;	
	
	private ExecutorService threadGetNews;
	private Future<?>		pendingGetNews;		
	
	private ExecutorService threadGetSingleNews;
	private Future<?>		pendingGetSingleNews;		
	
	private ExecutorService threadSearchMovies;
	private Future<?>		pendingSearchMovies;	
	
	private ExecutorService threadGetThumbnail;
	private Future<?>		pendingGetThumbnail;	
	
	private ExecutorService threadGetThumbnails;
	private Future<?>		pendingGetThumbnails;	
	
	private ExecutorService threadGetImage;
	private Future<?>		pendingGetImage;	
	
	// Observers.
	private Hashtable<String, ArrayList<CINeolNotificationCenter>> observers = null;

	// Notifications types.
	public static final String kCINeolDidFinishLoadingMovieNotification 			= "CINeolDidFinishLoadingMovie";
	public static final String kCINeolDidFinishLoadingMovieShowtimesNotification 	= "CINeolDidFinishLoadingMovieShowtimes";
	public static final String kCINeolDidFinishLoadingCommentsNotification			= "CINeolDidFinishLoadingCommentsNotification";
	public static final String kCINeolDidFinishLoadingNewsNotification				= "CINeolDidFinishLoadingNewsNotification";
	public static final String kCINeolDidFinishLoadingSingleNewsNotification		= "CINeolDidFinishLoadingSingleNewsNotification";
	public static final String kCINeolDidFinishSearchMoviesNotification				= "CINeolDidFinishSearchMoviesNotification";
	public static final String kCINeolDidFinishDownloadThumbnailNotification		= "CINeolDidFinishDownloadThumbnailNotification";
	public static final String kCINeolDidFinishDownloadThumbnailsNotification		= "CINeolDidFinishDownloadThumbnailsNotification";
	public static final String kCINeolDidFinishDownloadImageNotification			= "CINeolDidFinishDownloadImageNotification";


	private CINeolManager() {		
		mainThread = new Handler();
		
		observers = new Hashtable<String, ArrayList<CINeolNotificationCenter>>();	
		observers.put(kCINeolDidFinishLoadingMovieNotification, new ArrayList<CINeolNotificationCenter>());
		observers.put(kCINeolDidFinishLoadingMovieShowtimesNotification, new ArrayList<CINeolNotificationCenter>());
		observers.put(kCINeolDidFinishLoadingCommentsNotification, new ArrayList<CINeolNotificationCenter>());
		observers.put(kCINeolDidFinishLoadingNewsNotification, new ArrayList<CINeolNotificationCenter>());
		observers.put(kCINeolDidFinishLoadingSingleNewsNotification, new ArrayList<CINeolNotificationCenter>());
		observers.put(kCINeolDidFinishSearchMoviesNotification, new ArrayList<CINeolNotificationCenter>());
		observers.put(kCINeolDidFinishDownloadThumbnailNotification, new ArrayList<CINeolNotificationCenter>());
		observers.put(kCINeolDidFinishDownloadThumbnailsNotification, new ArrayList<CINeolNotificationCenter>());
		observers.put(kCINeolDidFinishDownloadImageNotification, new ArrayList<CINeolNotificationCenter>());
	}
	
	static public CINeolManager sharedManager() {
		if (sharedManager == null && kAPIKeyParameter != null)
			sharedManager = new CINeolManager();
		
		return sharedManager;
	}
	
	static public void setAPIKey(String key) {
		CINeolManager.kAPIKeyParameter = key;
	}
	
	public void getMovieWithID(long movieId) {
		threadGetMovieWithID = Executors.newSingleThreadExecutor();
		
		String URL = kURLBaseMovie + kAPIKeyParameter + "&id=" + String.valueOf(movieId);
				
		// Cancel previous thread.
		if (pendingGetMovieWithID != null)
			pendingGetMovieWithID.cancel(true);
		
		TaskGetMovie task = new TaskGetMovie(URL);
		pendingGetMovieWithID = threadGetMovieWithID.submit(task);
	}
	
	public void getMovieShowtimes(int numberOfWeeks) {
		threadGetMovieShowtimes = Executors.newSingleThreadExecutor();
		
		String URL = kURLBaseMovieShowtimes + kAPIKeyParameter + "&numWeeks=" + String.valueOf(numberOfWeeks);
				
		// Cancel previous thread.
		if (pendingGetMovieShowtimes != null)
			pendingGetMovieShowtimes.cancel(true);
		
		TaskGetMovieShowtimes task = new TaskGetMovieShowtimes(URL);
		pendingGetMovieShowtimes = threadGetMovieShowtimes.submit(task);
	}
	
	public void getCommentsForMovieWithID(long movieId, int startOnComment, int numberOfComments) {
		threadGetComments = Executors.newSingleThreadExecutor();
		
		String URL = kURLBaseComments + kAPIKeyParameter + "&idpelicula=" + movieId +
														   "&start=" 	  + startOnComment + 
														   "&numero=" 	  + numberOfComments;
		// Cancel previous thread.
		if (pendingGetComments != null)
			pendingGetComments.cancel(true);
		
		TaskGetComments task = new TaskGetComments(URL);
		pendingGetComments = threadGetComments.submit(task);		
	}
	
	public void getNumberOfCommentsForMovieWithID(int movieID) {
		this.getCommentsForMovieWithID(movieID, 0, 0);
	}	

	public void getNews(int numberOfNews) {
		threadGetNews = Executors.newSingleThreadExecutor();
		
		String URL = kURLBaseNews + kAPIKeyParameter + "&numNoticias=" + numberOfNews;
		
		// Cancel previous thread.
		if (pendingGetNews != null)
			pendingGetNews.cancel(true);
	
		TaskGetNews task = new TaskGetNews(URL);
		pendingGetNews = threadGetNews.submit(task);
	}
	
	public void getSingleNews(long l) {
		threadGetSingleNews = Executors.newSingleThreadExecutor();
		
		String URL = kURLBaseSingleNews + kAPIKeyParameter + "&idNoticia=" + l;
		
		// Cancel previous thread.
		if (pendingGetSingleNews != null)
			pendingGetSingleNews.cancel(true);
	
		TaskGetSingleNews task = new TaskGetSingleNews(URL);
		pendingGetSingleNews = threadGetSingleNews.submit(task);
	}
	
	public void searchMovies(String query) {
		threadSearchMovies = Executors.newSingleThreadExecutor();
		
		String URL = kURLBaseSearchMovies + URLEncoder.encode(query);
		
		// Cancel previous thread.
		if (pendingSearchMovies != null)
			pendingSearchMovies.cancel(true);
	
		TaskSearchMovies task = new TaskSearchMovies(URL);
		pendingSearchMovies = threadSearchMovies.submit(task);		
	}
	
	public void getThumbnailFromURL(String url) {
		threadGetThumbnail = Executors.newSingleThreadExecutor();

		url = kURLBaseGetThumbnail + url;
		
		// Cancel previous thread.
		if (pendingGetThumbnail != null)
			pendingGetThumbnail.cancel(true);
	
		TaskGetThumbnail task = new TaskGetThumbnail(url);
		pendingGetThumbnail = threadGetThumbnail.submit(task);
	}
	
	public void getImageFromURL(String url) {
		threadGetImage = Executors.newSingleThreadExecutor();

		url = kURLBaseGetImage + url;
		
		// Cancel previous thread.
		if (pendingGetImage != null)
			pendingGetImage.cancel(true);
	
		TaskGetImage task = new TaskGetImage(url);
		pendingGetImage = threadGetImage.submit(task);
	}
	
	public void getThumbnails(ArrayList<Image> images) {
		threadGetThumbnails = Executors.newSingleThreadExecutor();
		
		// Cancel previous thread.
		if (pendingGetThumbnails != null)
			pendingGetThumbnails.cancel(true);
	
		TaskGetThumbnails task = new TaskGetThumbnails(images);
		pendingGetThumbnails = threadGetThumbnails.submit(task);
	}
	
	public String getStringURLForVideoWithID(String id) {
		
		String urlBase = kURLBaseDailymotion + id;
		
		String info = null;
		URL url;
		try {
			url = new URL(urlBase);
			URLConnection connection = url.openConnection();

			InputStream data = connection.getInputStream();
			
			info = InputStreamUtils.stringFromInputStream(data);
			
			int index = info.indexOf("http://iphone");
			info = info.substring(index);
			
			index = info.indexOf("\"");
			info = info.substring(0, index);
		} catch (MalformedURLException e) {
			info = null;
			e.printStackTrace();
		} catch (IOException e) {
			info = null;
			e.printStackTrace();
		}
		
		return info;
	}
	
	public void addObserver(CINeolNotificationCenter observer, String notificationType) {		
		if (notificationType != null)
			observers.get(notificationType).add(observer);	
	}
	
	public void removeObserver(CINeolNotificationCenter observer, String notificationType) {
		if (notificationType != null)
			observers.get(notificationType).remove(observer);
	}
	
	protected void _cineolDidFinishLoadingMovie(final InputStream data) {
		mainThread.post(new Runnable() {
			public void run() {
				for (CINeolNotificationCenter observer : observers.get(kCINeolDidFinishLoadingMovieNotification))
					observer.cineolDidFinishLoadingMovie(data);
			}
		});
		
		threadGetMovieWithID.shutdownNow();
	}
	
	protected void _cineolDidFinishLoadingMovieShowtimes(final InputStream data) {
		mainThread.post(new Runnable() {
			public void run() {
				for (CINeolNotificationCenter observer : observers.get(kCINeolDidFinishLoadingMovieShowtimesNotification))
					observer.cineolDidFinishLoadingMovieShowtimes(data);
			}
		});
		
		threadGetMovieShowtimes.shutdownNow();
	}
	
	protected void _cineolDidFinishLoadingComments(final InputStream data) {
		mainThread.post(new Runnable() {
			public void run() {
				for (CINeolNotificationCenter observer : observers.get(kCINeolDidFinishLoadingCommentsNotification))
					observer.cineolDidFinishLoadingComments(data);
			}
		});
		
		threadGetComments.shutdownNow();
	}
	
	protected void _cineolDidFinishLoadingNews(final InputStream data) {
		mainThread.post(new Runnable() {
			public void run() {
				for (CINeolNotificationCenter observer : observers.get(kCINeolDidFinishLoadingNewsNotification))
					observer.cineolDidFinishLoadingNews(data);
			}
		});
		
		threadGetNews.shutdownNow();
	}
	
	protected void _cineolDidFinishLoadingSingleNews(final InputStream data) {		
		mainThread.post(new Runnable() {
			public void run() {
				for (CINeolNotificationCenter observer : observers.get(kCINeolDidFinishLoadingSingleNewsNotification))
					observer.cineolDidFinishLoadingSingleNews(data);
			}
		});
		
		threadGetSingleNews.shutdownNow();
	}
	
	protected void _cineolDidFinishSearchMovies(final InputStream data) {
		mainThread.post(new Runnable() {
			public void run() {
				for (CINeolNotificationCenter observer : observers.get(kCINeolDidFinishSearchMoviesNotification))
					observer.cineolDidFinishSearchMovies(data);
			}
		});
		
		threadSearchMovies.shutdownNow();
	}
	
	protected void _cineolDidFinishDownloadThumbnail(final InputStream data) {
		mainThread.post(new Runnable() {
			public void run() {
				for (CINeolNotificationCenter observer : observers.get(kCINeolDidFinishDownloadThumbnailNotification))
					observer.cineolDidDownloadThumbnail(data);
			}
		});
		
		if (threadGetThumbnail != null)
			threadGetThumbnail.shutdownNow();
	}
	
	protected void _cineolDidFinishDownloadImage(final InputStream data) {
		mainThread.post(new Runnable() {
			public void run() {
				for (CINeolNotificationCenter observer : observers.get(kCINeolDidFinishDownloadImageNotification))
					observer.cineolDidDownloadImage(data);
			}
		});
		
		if (threadGetImage != null)
			threadGetImage.shutdownNow();
	}
	
	protected void _cineolDidFinishDownloadThumbnails() {
		mainThread.post(new Runnable() {
			public void run() {
				for (CINeolNotificationCenter observer : observers.get(kCINeolDidFinishDownloadThumbnailsNotification))
					observer.cineolDidDownloadThumbnails();
			}
		});
		
		if (threadGetThumbnails != null)
			threadGetThumbnails.shutdownNow();
	}
}
