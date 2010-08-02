package es.leafsoft.cineol.activities;

import java.io.InputStream;
import java.util.ArrayList;

import com.flurry.android.FlurryAgent;

import es.leafsoft.cineol.CINeolFacade;
import es.leafsoft.utils.InputStreamUtils;

import net.cineol.database.CINeolDatabase;
import net.cineol.model.Movie;
import net.cineol.model.News;
import net.cineol.webservice.CINeolManager;
import net.cineol.webservice.CINeolNotificationCenter;
import net.cineol.xml.XMLParser;
import net.cineol.xml.XMLParserDelegate;
import net.cineol.xml.XMLParserMovie;
import net.cineol.xml.XMLParserNews;
import net.cineol.xml.XMLParserPremiers;
import net.cineol.xml.XMLParserSingleNews;
import es.leafsoft.cineol.R;
import app.cineol.adapters.NewsAdapter;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;


public class NewsListActivity extends ListActivity
						  	  implements CINeolNotificationCenter, XMLParserDelegate, NewsActivityDelegate, SearchableActivity
{
	private static final String TAG = "NewsListActivity";
	
	private static final String NO_LOAD_NEWS 	= "NO_LOAD_NEWS";
	private static final String NEWS_COUNT 		= "NEWS_COUNT";

	private static final int MENU_ITEM_SHOW = 0;
	private static final int MENU_ITEM_WEB 	= 1;
	private static final int MENU_ITEM_MAIL	= 2;
	
	private NewsAdapter adapter;
	
	private int lastIndexProcessedNews = 0;
	private int sizeOfProgress = 0;
	private int currentProgress = 1;
	private int lastSelection = 0;
	private int lastOptionContextMenu = -1;
	
	private boolean downloading = true;
	private boolean processing  = false;
	private boolean onPause  = false;
	
	private ProgressDialog progressDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    

        if (this.isSearchActivity())
        	this.onPerformSearch();
        else {
	        this.requestWindowFeature(Window.FEATURE_PROGRESS);
	        this.setContentView(R.layout.cover_list_view);
	        this.registerForContextMenu(this.getListView());
	
	        adapter = new NewsAdapter(this, R.layout.news_row, new ArrayList<News>());
	        this.setListAdapter(adapter);
	        
			CINeolManager.sharedManager().addObserver(this, CINeolManager.kCINeolDidFinishLoadingNewsNotification);
			CINeolManager.sharedManager().addObserver(this, CINeolManager.kCINeolDidFinishLoadingSingleNewsNotification);
			
			this.downloading = savedInstanceState == null ||
							  !savedInstanceState.getBoolean(NO_LOAD_NEWS);
			if (downloading)
				CINeolManager.sharedManager().getNews(10);
			else {
				int news = savedInstanceState.getInt(NEWS_COUNT);
				boolean noLoad = !this.downloading && !this.processing;

				if (noLoad) {
					this.adapter.clear();
					for (int i = 0; i < news; i++)
						adapter.add((News) savedInstanceState.getParcelable("news " + i));
				}
			}
        }
    }
    
	@Override
	protected void onStart() {
		super.onStart();
		
		FlurryAgent.onStartSession(this, CINeolFacade.API_Key_Flurry);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		this.adapter.clear();

		CINeolManager.sharedManager().removeObserver(this, CINeolManager.kCINeolDidFinishLoadingNewsNotification);
		CINeolManager.sharedManager().removeObserver(this, CINeolManager.kCINeolDidFinishLoadingSingleNewsNotification);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		onPause = true;
				
		if (this.downloading || this.processing) {
	        this.getParent().setTitle("CINeol");
	        this.getParent().setProgressBarVisibility(false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		onPause = false;
		
		if (this.downloading) {
	        this.getParent().setProgressBarVisibility(true);
			this.getParent().setProgressBarIndeterminateVisibility(false);
	        this.getParent().setProgress(1); // HACK para que se muestre la barra de progreso nada mas empezar la Activity.
	        this.getParent().setProgressBarIndeterminate(true);
	        this.getParent().setTitle("Descargando datos...");
		}
		else if (this.processing) {
			this.getParent().setProgressBarIndeterminateVisibility(false);
        	this.getParent().setProgressBarIndeterminate(false);
        	this.getParent().setProgress(this.currentProgress * this.sizeOfProgress);
	        this.getParent().setTitle("Procesando datos...");
		}
	}
	
	protected void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState(outState);
		
		int news = adapter.getCount();
		boolean noLoad = !this.downloading && !this.processing;
			
		outState.putBoolean(NO_LOAD_NEWS, noLoad);
		outState.putInt(NEWS_COUNT, news);
		
		if (noLoad)
			for (int i = 0; i < news; i++)
				outState.putParcelable("news " +  i, adapter.getItem(i));		
	}
	
    public boolean isSearchActivity() {
    	return Intent.ACTION_SEARCH.equals(this.getIntent().getAction());
    }
    
    public void onPerformSearch() {
    	String query = this.getIntent().getStringExtra(SearchManager.QUERY);
    	
    	Intent intent = new Intent(this, CardsActivity.class);
    	
    	intent.setAction(Intent.ACTION_SEARCH);
    	intent.putExtra(SearchManager.QUERY, query);
    	
    	this.startActivity(intent);
    	this.finish();
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_list_view_menu, menu);
        
        return true;
    }
   
	/*
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    	return true;
    }
    */
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		
			case R.id.about:
				AboutActivity.open(this);
				return true;
		
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		lastSelection = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
		News news = adapter.getItem(lastSelection);
		
		lastOptionContextMenu = item.getItemId();
		
		switch (lastOptionContextMenu) {
			case MENU_ITEM_SHOW:
				NewsActivity.showNews(this, news, this);
				break;
				
			case MENU_ITEM_WEB:
				NewsActivity.showNewsOnCINeol(this, news);
				break;
				
			case MENU_ITEM_MAIL:				
				if (news.getContent() == null) {
					progressDialog = ProgressDialog.show(this, "", "Descargando noticia...", true);
					CINeolManager.sharedManager().getSingleNews(news.getId());
				}
				else 				
					NewsActivity.sendNews(this, news);
				break;
		}
		
		return true;
	}
	
	@Override
	public void onCreateContextMenu (ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		News news = adapter.getItem(((AdapterContextMenuInfo) menuInfo).position);
		
		menu.setHeaderTitle(news.getTitle());		
		menu.add(0, MENU_ITEM_SHOW, 0, "Ver noticia");		
		menu.add(0, MENU_ITEM_WEB, 0, "Ver noticia en la web");
		menu.add(0, MENU_ITEM_MAIL, 0, "Compartir noticia");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		lastSelection = position;
		NewsActivity.showNews(this, adapter.getItem(position), this);
	}

    
    // CINeol Manager Delegate Methods.
	public void cineolDidFinishLoadingComments(InputStream data) {}
	public void cineolDidFinishLoadingMovie(InputStream data) {}
	public void cineolDidFinishLoadingMovieShowtimes(InputStream data) {}
	public void cineolDidFinishSearchMovies(InputStream data) {}
	public void cineolDidDownloadThumbnail(final InputStream data) {}
	public void cineolDidDownloadThumbnails() {}
	public void cineolDidDownloadImage(final InputStream data) {}
	
	public void cineolDidFinishLoadingSingleNews(InputStream data) {
		if (progressDialog != null && progressDialog.isShowing()) {
			XMLParserSingleNews parser = new XMLParserSingleNews(this, data, this);
			parser.parse();
		}
	}
	
	public void cineolDidFinishLoadingNews(InputStream data) {
		this.downloading = false;
		XMLParserNews parser = new XMLParserNews(this, data, this);
		parser.parse();
	}

	
	// XMLParser Delegate Methods.
	public void xmlParserDidFindAttribute(XMLParser parser, String attributeName, String attributeValue) {
		if (attributeName.equals(XMLParserNews.kXMLAttributeTotalNews)) {
			sizeOfProgress = 10000 / (Integer.valueOf(attributeValue).intValue() + 1);

	        this.getParent().setProgressBarIndeterminate(false);
	        
	        if (!onPause)
	        	this.getParent().setProgress(sizeOfProgress);
	        
	        currentProgress++;
		}
	}

	public void xmlParserDidStartDocument(XMLParser parser) {
		this.processing = true;
    	this.getParent().setTitle("Procesando datos...");
	}
	
	public void xmlParserDidEndDocument(XMLParser parser) {
		if (parser.parserType().equals(XMLParserNews.PARSE_TYPE)) {			
	        this.getParent().setProgress(10000);
	        this.getParent().setTitle("CINeol");
	        
	        this.processing = false;   
		}
		else if (parser.parserType().equals(XMLParserSingleNews.PARSE_TYPE)) {
			News news = ((XMLParserSingleNews) parser).getNews();
			if (news == null)
				return;
			this.onUpdateNews(news);

			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();

				switch (lastOptionContextMenu) {
					case MENU_ITEM_MAIL:
						NewsActivity.sendNews(this, news);
						break;
				}
			}
		}
	}

	public void xmlParserDidEndTag(XMLParser parser, String tag) {
		if (parser.parserType().equals(XMLParserNews.PARSE_TYPE)) {			
			News news = ((XMLParserNews)parser).getNews();
			
			if (news == null)
				return;
			
	        adapter.add(news);	        
			adapter.notifyDataSetChanged();
			
	        if (!onPause)
	        	this.getParent().setProgress(sizeOfProgress * currentProgress);
	        
	        currentProgress++;
			lastIndexProcessedNews++;
		}
	}
	
	private void onUpdateNews(News news) {
		News oldNews = this.adapter.getItem(lastSelection);
		news.setImage(oldNews.getImage());
		
		this.adapter.remove(oldNews);
		this.adapter.insert(news, lastSelection);		
	}

	public void newsActivityDidLoadNews(News news) {
		this.onUpdateNews(news);		
	}
}