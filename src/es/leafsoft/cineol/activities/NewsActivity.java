package es.leafsoft.cineol.activities;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.flurry.android.FlurryAgent;

import bit.ly.model.BitlyURL;
import bit.ly.webservice.BitlyManager;
import bit.ly.webservice.BitlyManagerObserver;
import bit.ly.xml.XMLParserDelegateAndroid;
import bit.ly.xml.XMLParserGetBitlyURL;
import bit.ly.xml.XMLParserObserver;

import es.leafsoft.cineol.CINeolFacade;
import es.leafsoft.gallery.GalleryActivity;
import es.leafsoft.utils.InputStreamUtils;

import net.cineol.model.News;
import net.cineol.utils.CINeolUtils;
import net.cineol.webservice.CINeolManager;
import net.cineol.webservice.CINeolNotificationCenter;
import net.cineol.xml.XMLParser;
import net.cineol.xml.XMLParserDelegate;
import net.cineol.xml.XMLParserSingleNews;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import es.leafsoft.cineol.R;

public class NewsActivity extends Activity implements CINeolNotificationCenter, XMLParserDelegate, OnClickListener {
	
	private static final String TAG = "NewsActivity";
	private static News news = null;
	private static NewsActivityDelegate delegate = null;
	
	private TextView title;
	private ImageView thumbView;
	private WebView content;
	

    public static void showNews(Context context, News news, NewsActivityDelegate delegate) {
        final Intent intent = new Intent(context, NewsActivity.class);
        NewsActivity.news = news;
        NewsActivity.delegate = delegate;
        context.startActivity(intent);
    }
    
    public static void sendNews(final Context context, final News news) {
		HashMap<String, String> value = new HashMap<String, String>();
		value.put("ID noticia", news.getId() + "");
		value.put("Título noticia", news.getTitle());
		value.put("URL CINeol", news.getUrlCINeol());

		FlurryAgent.onEvent(CINeolFacade.FlurryEventSendNews, value);
		
        final Intent sendIntent = new Intent(Intent.ACTION_SEND); 
        sendIntent.setType("text/plain"); 
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, news.getTitle());        
        
        BitlyManager.sharedManager().addObserver(new BitlyManagerObserver() {

			public void bitlyDidFinishGetShortenURL(final InputStream data) {
				if (data == null) {
			        sendIntent.putExtra(Intent.EXTRA_TEXT, news.getUrlCINeol()); 
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
        
        BitlyManager.sharedManager().getShortenURL(news.getUrlCINeol());
	}


    public static void showNewsOnCINeol(Context context, News news) {
		HashMap<String, String> value = new HashMap<String, String>();
		value.put("ID noticia", news.getId() + "");
		value.put("Título noticia", news.getTitle());
		value.put("URL CINeol", news.getUrlCINeol());

		FlurryAgent.onEvent(CINeolFacade.FlurryEventShowNewsOnCINeol, value);
		
		Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getUrlCINeol()));   
		context.startActivity(viewIntent);
	}
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		this.setContentView(R.layout.news_view);
		
		content = (WebView) this.findViewById(R.id.content);
		title = (TextView) this.findViewById(R.id.title);
		thumbView = (ImageView) this.findViewById(R.id.thumb);
		
		title.setText(news.getTitle());
		content.getSettings().setTextSize(TextSize.NORMAL);
		//content.getSettings().setBuiltInZoomControls(true);
		
		if (news.getImage() == null)
			thumbView.setImageResource(R.drawable.news);
		else {
			thumbView.setImageDrawable(news.getImage());
			thumbView.setOnClickListener(this);
		}
		
        if (news.getContent() == null) {
    		this.setProgressBarIndeterminateVisibility(true);
            this.setTitle("Descargando noticia...");
        	
            content.setWebChromeClient(new WebChromeClient() {
            	public void onProgressChanged(WebView view, int progress) {        		
            		setProgressBarIndeterminateVisibility(false);
            		updateTitleBar();
            	}
            });
            
        	CINeolManager.sharedManager().addObserver(this, CINeolManager.kCINeolDidFinishLoadingSingleNewsNotification);
			CINeolManager.sharedManager().getSingleNews(news.getId());
        }
        else {
        	this.updateWebView();
        	this.updateTitleBar();
        }
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		FlurryAgent.onStartSession(this, CINeolFacade.API_Key_Flurry);
		
		HashMap<String, String> value = new HashMap<String, String>();
		value.put("ID noticia", news.getId() + "");
		value.put("Título noticia", news.getTitle());
		value.put("URL CINeol", news.getUrlCINeol());

		FlurryAgent.onEvent(CINeolFacade.FlurryEventShowNews, value);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		CINeolManager.sharedManager().removeObserver(this, CINeolManager.kCINeolDidFinishLoadingSingleNewsNotification);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_view_menu, menu);

        return true;
    }
	    
	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		switch (item.getItemId()) {
	    	case R.id.web:
	    		NewsActivity.showNewsOnCINeol(this, news);
	    		break;
	    		
	    	case R.id.mail:
	    		NewsActivity.sendNews(this, news);
	    		break;
		}
		
		return true;
	}

	    
	// CINeol Manager Delegate Methods.
	public void cineolDidFinishLoadingComments(InputStream data) {}
	public void cineolDidFinishLoadingMovie(InputStream data) {}
	public void cineolDidFinishLoadingMovieShowtimes(InputStream data) {}
	public void cineolDidFinishSearchMovies(InputStream data) {}
	public void cineolDidDownloadThumbnail(final InputStream data) {}
	public void cineolDidDownloadThumbnails() {}
	public void cineolDidDownloadImage(final InputStream data) {}
	public void cineolDidFinishLoadingNews(InputStream data) {}
	
	public void cineolDidFinishLoadingSingleNews(InputStream data) {
		XMLParserSingleNews parser = new XMLParserSingleNews(this, data, this);		
		parser.parse();
	}
	
	public void xmlParserDidEndTag(XMLParser parser, String tag) {}
	public void xmlParserDidFindAttribute(XMLParser parser, String attributeName, String attributeValue) {}
	public void xmlParserDidStartDocument(XMLParser parser) {}
	
	public void xmlParserDidEndDocument(XMLParser parser) {
		News news = ((XMLParserSingleNews) parser).getSingleNews();
		news.setImage(NewsActivity.news.getImage());
		NewsActivity.news = news;
				
		this.updateWebView();
		
		if (delegate != null)
			delegate.newsActivityDidLoadNews(news);
	}

	
	protected void updateTitleBar() {
		String title = CINeolUtils.stringFromDate(news.getDate(), "dd/MM/yyyy");
		title += " - Por " + news.getAuthor();
		setTitle(title);
	}
	
	protected void updateWebView() {
		
		String HTML = InputStreamUtils.stringFromInputStream(this.getResources().openRawResource(R.raw.news_template));
		
		if (news.getContent() == null || news.getContent().length() < news.getIntroduction().length())
			HTML = String.format(HTML, news.getIntroduction());
		else
			HTML = String.format(HTML, news.getContent());
				
		content.loadDataWithBaseURL("http://www.cineol.net/", HTML, "text/html", "utf-8", null);  

		/*
		if (news.getContent() == null || news.getContent().length() < news.getIntroduction().length())
			content.loadData(news.getIntroduction(), "text/html", "utf-8");                
		else {
			String html = "<link href=\"http://www.cineol.net/css/2columnas.css\" rel=\"stylesheet\" type=\"text/css\" />" + 
						  "<div id=\"contenido-noticia\" class=\"bloque-texto\">" +
						  news.getContent() + 
						  "</div>";
			
			content.loadDataWithBaseURL("http://www.cineol.net/", html, "text/html", "utf-8", null);  
		}
		*/
	}


	public void onClick(View v) {
		if (v == thumbView) {
			ArrayList<Drawable> photos = new ArrayList<Drawable>();
			photos.add(news.getImage());
			GalleryActivity.showGallery(this, (String)news.getTitle(), 0, 1, false, null, photos);		
		}
	}
}