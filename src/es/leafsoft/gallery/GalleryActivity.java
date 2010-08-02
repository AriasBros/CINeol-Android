package es.leafsoft.gallery;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.flurry.android.FlurryAgent;

import es.leafsoft.cineol.CINeolFacade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.FrameLayout.LayoutParams;

public class GalleryActivity extends Activity implements GalleryViewDataSource {
	private static String TAG = "GalleryActivity";
	
	public static String NUMBER_OF_PHOTOS 	= "kNumberOfPhotos";
	public static String INITIAL_PHOTO		= "kInitialPhoto";
	public static String GALLERY_TITLE		= "kGalleryTitle";
	public static String SHOW_HUD			= "kShowHUD";
		
	private static ArrayList<SoftReference<Drawable>> photos = null;
	private static ArrayList<String> url_photos	= null;
	
	private GalleryView galleryView = null;
	
	private DisplayMetrics metrics;
	private Handler mainThread;
		
	public static void showGallery(Context context,
								   String title,
							 	   int initialPhoto,
								   int numberOfPhotos,
								   boolean showHUD,
								   ArrayList<String> url_photos,
								   ArrayList<Drawable> photos)
	{    
		final Intent intent = new Intent(context, GalleryActivity.class);
        
        intent.putExtra(NUMBER_OF_PHOTOS, numberOfPhotos);
        intent.putExtra(INITIAL_PHOTO, initialPhoto);
        intent.putExtra(GALLERY_TITLE, title);
        intent.putExtra(SHOW_HUD, showHUD);

        GalleryActivity.url_photos = url_photos;
    	GalleryActivity.photos = new ArrayList<SoftReference<Drawable>>();

        if (photos == null) {
        	for (int i = 0; i < numberOfPhotos; i++)
        		GalleryActivity.photos.add(new SoftReference<Drawable>(null));
        }
        else {
        	for (Drawable photo : photos)
                GalleryActivity.photos.add(new SoftReference<Drawable>(photo));
        }
        
        context.startActivity(intent);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainThread = new Handler();
        requestWindowFeature(Window.FEATURE_NO_TITLE);

		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		 
		Bundle bundle = this.getIntent().getExtras();
		boolean showHUD		= bundle.getBoolean(SHOW_HUD);
		int numberOfPhotos 	= bundle.getInt(NUMBER_OF_PHOTOS);
		int initialPhoto   	= bundle.getInt(INITIAL_PHOTO);
		String title		= bundle.getString(GALLERY_TITLE);
	
		galleryView = new GalleryView(this, metrics, title, initialPhoto, numberOfPhotos, this);
		galleryView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		galleryView.setVerticalFadingEdgeEnabled(false);
		galleryView.setHorizontalFadingEdgeEnabled(false);
		galleryView.setHorizontalScrollBarEnabled(false);
		galleryView.showHUD(showHUD);
		
		this.setContentView(galleryView);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		FlurryAgent.onStartSession(this, CINeolFacade.API_Key_Flurry);
		
		HashMap<String, String> value = new HashMap<String, String>();
		value.put("Título galería", galleryView.getGalleryTitle());
		value.put("Número de fotos", galleryView.getNumberOfPhotos() + "");

		FlurryAgent.onEvent(CINeolFacade.FlurryEventShowGallery, value);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		FlurryAgent.onEndSession(this);
	}


	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}


	public void loadPhotoAtIndex(int index) {
		ExecutorService threadDownloadTask = Executors.newSingleThreadExecutor();
		DownloadTask task   = new DownloadTask(url_photos.get(index), index, threadDownloadTask);
		threadDownloadTask.submit(task);
	}
	
	public Drawable photoAtIndex(int index){
		return photos.get(index).get();
	}
	
	public String urlForPhotoAtIndex(int index) {return null;}
	
	private void downloadTaskDidFinish(final DownloadTask downloadTask) {
		mainThread.post(new Runnable() {
			public void run() {
				int index = downloadTask.getTaskID();

				try {
					Bitmap bitmap = BitmapFactory.decodeStream(downloadTask.getDownloadedData());
					
					if (bitmap == null) {
						Log.d(TAG, "BITMAP NULO!!!!!");
						
						HashMap<String, String> value = new HashMap<String, String>();
						value.put("Título galería", galleryView.getGalleryTitle());
						value.put("Número de fotos", galleryView.getNumberOfPhotos() + "");

						FlurryAgent.onError(CINeolFacade.FlurryErrorImageDidNotLoad,
											"Error mientras se cargaba una imagen de una galería.",
											"GalleryActivity");
						
						return;
					}
					
				    int w = bitmap.getWidth();
				    int h = bitmap.getHeight();
				    
				    int m = 0;
				    if (metrics.widthPixels > metrics.heightPixels)
				    	m = metrics.widthPixels;
				    else
				    	m = metrics.heightPixels;
				    
				    if (w > h && w > m) {
				        h = m * h / w;
				    	w = m;
				    }
				    
				    else if (w < h && h > m) {
				        w = m * w / h;
				        h = m;
				    }
				    
					BitmapDrawable image = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, w, h, true));
				    
					photos.remove(index);
					photos.add(index, new SoftReference<Drawable>(image));
					galleryView.imageAtIndexDidLoad(image, index);
					bitmap.recycle();
				}
				catch(OutOfMemoryError e) {
					System.gc();
				}
			}
		});
		
		downloadTask.getThread().shutdownNow();
	}
	
	class DownloadTask implements Runnable {

		private static final String TAG = "DownloadTask";
		protected String url;
		protected int task_id;
		protected ExecutorService threadDownloadTask;
		protected BufferedInputStream downloadedData;
		
		DownloadTask(String anURL, int task_id, ExecutorService thread) {
			this.url = anURL;
			this.task_id = task_id;
			this.threadDownloadTask = thread;
		}
		
		public void run() {
			InputStream data = this.getInputStream();
			downloadedData = new BufferedInputStream(data);
			downloadTaskDidFinish(this);
		}
		
		public int getTaskID() {
			return this.task_id;
		}
		
		public ExecutorService getThread() {
			return this.threadDownloadTask;
		}
		
		public BufferedInputStream getDownloadedData() {
			return this.downloadedData;
		}

		protected InputStream getInputStream() {
			
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
				Log.e(TAG, "MalformedURLException", e);
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(TAG, "IOException", e);
				e.printStackTrace();
			} catch (InterruptedException e) {
				Log.e(TAG, "InterruptedException", e);
				e.printStackTrace();
			}

			return data;
		}
	}
}