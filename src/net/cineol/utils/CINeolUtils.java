package net.cineol.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class CINeolUtils {
	
	private static final String TAG = "CINeolUtils";
	
	static private BitmapDrawable bitmapDrawableFromURL(Resources resources, String str_url, boolean scale, int w, int h) {
		if (str_url == null || str_url.length() <= 0)
			return null;
		
		BitmapDrawable thumb = null;
		try {
			URL url = new URL(str_url);
			URLConnection connection = url.openConnection();
			connection.connect();
			
			InputStream stream = connection.getInputStream();
			BufferedInputStream data = new BufferedInputStream(stream);
			
			Bitmap bitmap = BitmapFactory.decodeStream(data);
			if (bitmap != null) {
				if (scale)
					thumb = new BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, w, h, true));
				else
					thumb = new BitmapDrawable(resources, bitmap);
			}

		} catch (MalformedURLException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		
		return thumb;
	}
	
	static public String urlForPosterForMovieWithID(long movieId) {
		return "http://www.cineol.net/galeria/carteles/bigtmp_" + String.valueOf(movieId) + ".jpg";
	}
	
	static public String urlForNews(long l, String title) {
		return "http://www.cineol.net/noticias/" + l + "_" + URLEncoder.encode(title);
	}
	
	static public BitmapDrawable thumbPosterFromURL(Context context, String str_url) {
		return CINeolUtils.bitmapDrawableFromURL(context.getResources(), str_url, true, 70, 105);
	}
	
	static public BitmapDrawable posterFromURL(Context context, String url) {
		return CINeolUtils.bitmapDrawableFromURL(context.getResources(), url, false, 0, 0);
	}
	
	static public BitmapDrawable thumbPosterForMovieWithID(Context context, int id) {		
		return CINeolUtils.thumbPosterFromURL(context, urlForPosterForMovieWithID(id));
	}
	
	static public BitmapDrawable posterForMovieWithID(Context context, int id) {
		String url = "http://www.cineol.net/galeria/carteles/bigtmp_" + String.valueOf(id) + ".jpg";

		return CINeolUtils.posterFromURL(context, url);
	}

	static public int movieIDFromXMLURL(String url) {
		String patternURL = "http://www.cineol.net/api/peliculaxml.php?id=";
		return Integer.valueOf(url.substring(patternURL.length(), url.length()));
	}
	
	
	// TODO -- BORRAR cuando se mejore los scripts de busqueda.
	static public int tempMovieIDFromXMLURL(String url) {
		String patternURL = "http://www.cineol.net/peliculaxml.php?id=";
		return Integer.valueOf(url.substring(patternURL.length(), url.length()));
	}
	
	static public int personIDFromURL(String url) {
		String patternURL = "http://www.cineol.net/gente/";
		return Integer.valueOf(url.substring(patternURL.length(), url.indexOf("_")));
	}
	
	static public String photoIDFromURL(String url) {
		String patternURL = "galeria/fotos/";		
		return url.substring(patternURL.length(), url.indexOf("."));
	}
	
	static public Date dateFromString(String str, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		
		Date result = null;
		try {
			result = formatter.parse(str);
		} catch (ParseException e) {
			//e.printStackTrace();
		} catch (NullPointerException e) {
			//e.printStackTrace();
		}
		
		return result;
	}
	
	static public String stringFromDate(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		
		String result = null;
		try {
			result = formatter.format(date);
		} catch (NullPointerException e) {
			//e.printStackTrace();
		}
		
		return result;
	}
}