package es.leafsoft.cineol.activities;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import com.flurry.android.FlurryAgent;

import es.leafsoft.cineol.CINeolFacade;


import net.cineol.model.Movie;
import net.cineol.webservice.CINeolManager;
import net.cineol.webservice.CINeolNotificationCenter;
import net.cineol.xml.XMLParser;
import net.cineol.xml.XMLParserDelegate;
import net.cineol.xml.XMLParserMovie;
import net.cineol.xml.XMLParserPremiers;
import net.cineol.xml.XMLParserSearchMovies;
import es.leafsoft.cineol.R;
import app.cineol.adapters.PremiersAdapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TabHost.OnTabChangeListener;


public class CardsActivity extends TabActivity implements CINeolNotificationCenter, XMLParserDelegate, MovieActivityDelegate, OnItemClickListener {
	private static final String TAG = "CardsActivity";
	
	private static final String NO_RESULTS_MSG = "Sin resultados";
	private static final String SEARCHING_MSG = "Buscando...";
	private static final String NO_MOVIES_STORE_MSG = "No hay fichas guardadas";
	
	private static final CharSequence[] itemsMobileTab 	= {"Título", "Año", "Género", "Duración", "Puntuación"};
	private static final CharSequence[] itemsWebTab 	= {"Título", "Año"};
	
	private static final int	MENU_ITEM_COMMENTS 	= 0;
	private static final int	MENU_ITEM_STORE		= 1;
	private static final int	MENU_ITEM_DELETE 	= 2;
	private static final int	MENU_ITEM_WEB 		= 3;
	private static final int	MENU_ITEM_MAIL 		= 4;
	private static final int	MENU_ITEM_CARD 		= 5;
	
	private static final int SORT_BY_TITLE 			= 0;
	private static final int SORT_BY_YEAR 			= 1;
	private static final int SORT_BY_GENRE 			= 2;
	private static final int SORT_BY_DURATION 		= 3;
	private static final int SORT_BY_RATING 		= 4;

	private static final int MOBILE_TAB 			= 0;
	private static final int WEB_TAB	 			= 1;


	private int mobileTabSortedBy = 0;
	private int webTabSortedBy = 0;
	private int lastSelection = 0;
	private int lastOptionContextMenu = -1;
	
	protected PremiersAdapter mobileAdapter;
	protected PremiersAdapter webAdapter;

	protected TabHost   tabHost;
	protected TabWidget tabWidget;
	protected TextView 	mobileTextView;
	protected TextView 	webTextView;
	protected ListView 	mobileListView;
	protected ListView 	webListView;
	protected ProgressDialog progressDialog;
	
	protected String searchQuery;
	protected boolean modeSearch = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        
		this.modeSearch = this.isSearchActivity();

		if (modeSearch)
			this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		this.onConfigureTabs();
		this.onFindViews();
		this.onConfigureViews();
		
        if (modeSearch)
        	this.onPerformSearch();
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
    
    protected void onConfigureTabs() {
        tabHost = this.getTabHost();
        tabWidget = this.tabHost.getTabWidget();
        
        tabWidget.setBackgroundColor(0xFF000000);
        tabWidget.setVisibility(View.GONE);
        
        LayoutInflater.from(this).inflate(R.layout.cards_list_view, tabHost.getTabContentView(), true);

        tabHost.addTab(tabHost.newTabSpec("MOVIL")
                .setIndicator("En el móvil", this.getResources().getDrawable(R.drawable.ic_tab_phone))
                .setContent(R.id.mobileTab));
        tabHost.addTab(tabHost.newTabSpec("WEB")
        		.setIndicator("En la web", this.getResources().getDrawable(R.drawable.ic_tab_web))
                .setContent(R.id.webTab));
    }
    
    protected void onFindViews() {
        mobileTextView 	= (TextView) findViewById(R.id.mobileEmpty);
        webTextView 	= (TextView) findViewById(R.id.webEmpty);
        
        mobileListView 	= (ListView) findViewById(R.id.mobileList);
        webListView 	= (ListView) findViewById(R.id.webList);
    }
    
    protected void onConfigureViews() {
		mobileAdapter = new PremiersAdapter(this, R.layout.movie_row, new ArrayList<Movie>());
		webAdapter = new PremiersAdapter(this, R.layout.movie_row, new ArrayList<Movie>());
		
    	this.mobileListView.setAdapter(mobileAdapter);
    	this.webListView.setAdapter(webAdapter);
    	
    	this.mobileListView.setOnItemClickListener(this);
    	this.webListView.setOnItemClickListener(this);
    	
    	this.registerForContextMenu(mobileListView);
    	this.registerForContextMenu(webListView);
    }
    
    protected boolean isSearchActivity() {
    	return Intent.ACTION_SEARCH.equals(this.getIntent().getAction());
    }
    
    protected void onPerformSearch() {
    	this.searchQuery = this.getIntent().getStringExtra(SearchManager.QUERY);
        this.setTitle("Buscando " + "\"" + searchQuery + "\"");
    	this.tabWidget.setVisibility(View.VISIBLE);
    	
		HashMap<String, String> value = new HashMap<String, String>();
		value.put("Consulta", searchQuery);
		FlurryAgent.onEvent(CINeolFacade.FlurryEventSearchMovie, value);
    	
		CINeolManager.sharedManager().addObserver(this, CINeolManager.kCINeolDidFinishLoadingMovieNotification);
		CINeolManager.sharedManager().addObserver(this, CINeolManager.kCINeolDidFinishSearchMoviesNotification);

		this.searchMoviesByQuery(searchQuery);
    }

	@Override
	protected void onResume() {
		super.onResume();
        
		if (!modeSearch) {
			ArrayList<Movie> movies = CINeolFacade.getMoviesOnCardsSection();
        	this.setMovies(movies, MOBILE_TAB);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		CINeolManager.sharedManager().removeObserver(this, CINeolManager.kCINeolDidFinishLoadingMovieNotification);
		CINeolManager.sharedManager().removeObserver(this, CINeolManager.kCINeolDidFinishSearchMoviesNotification);
	}
	
	// Options Menu Methods.
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cards_view_menu, menu);

        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    	
    	if (this.tabHost.getCurrentTab() == MOBILE_TAB)
    		menu.findItem(R.id.sort).setEnabled((mobileAdapter != null && !this.mobileAdapter.isEmpty()));
    	else
    		menu.findItem(R.id.sort).setEnabled((webAdapter != null && !this.webAdapter.isEmpty()));

    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    	switch (item.getItemId()) {
	    	case R.id.sort:
	    		this.showDialogSortMovies();
	    		break;
	    		
	    	case R.id.search:
	    		this.onSearchRequested();
	    		break;	    
	    		
	    	case R.id.about:
	    		AboutActivity.open(this);
	    		break;
    	}
    	
		return true;
    }
    
	// Context Menu Methods.
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		lastSelection = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
		lastOptionContextMenu = item.getItemId();

		Movie movie = null;
		
		if (this.tabHost.getCurrentTab() == MOBILE_TAB) {
			movie = this.mobileAdapter.getItem(lastSelection);
		}
		else {
			movie = this.webAdapter.getItem(lastSelection);
			
			if (!CINeolFacade.isMovieStored(movie.getId())) {
				progressDialog = ProgressDialog.show(this, "", "Descargando ficha...", true);
				progressDialog.setCancelable(true);
				CINeolManager.sharedManager().getMovieWithID(movie.getId());
				
				return true;
			}
		}	
		
		this.onContextItemSelected(movie, lastOptionContextMenu);
		
		return true;
	}
	
	public void onContextItemSelected(Movie movie, int item) {

		switch (item) {
			case MENU_ITEM_COMMENTS:
				MovieActivity.showMovie(this, movie, this, true, false);
				break;
				
			case MENU_ITEM_STORE:
				CINeolFacade.storeMovie(movie);
				break;
				
			case MENU_ITEM_DELETE:
				if (CINeolFacade.deleteMovie(movie.getId())) {
					this.mobileAdapter.remove(movie);
					this.mobileAdapter.notifyDataSetChanged();
					this.notifyMobileListChanged();
				}
				break;
				
			case MENU_ITEM_CARD:
				MovieActivity.showMovie(this, movie, this, false, false);
				break;
	
			case MENU_ITEM_WEB:
				CINeolFacade.showMovieOnCINeol(this, movie);
				break;
	
			case MENU_ITEM_MAIL:
				CINeolFacade.sendMovie(this, movie);
				break;
		}	
	}
	
	protected void notifyMobileListChanged() {
       	if (mobileAdapter.isEmpty()) {
       		this.mobileTextView.setVisibility(View.VISIBLE);
       		this.mobileListView.setVisibility(View.GONE);
       	}
       	else {
       		this.mobileTextView.setVisibility(View.GONE);
       		this.mobileListView.setVisibility(View.VISIBLE);	 
       	}
	}
	
	protected void notifyWebListChanged() {
       	if (webAdapter.isEmpty()) {
       		this.webTextView.setVisibility(View.VISIBLE);
       		this.webListView.setVisibility(View.GONE);
       	}
       	else {
       		this.webTextView.setVisibility(View.GONE);
       		this.webListView.setVisibility(View.VISIBLE);	 
       	}
	}

	public void onCreateContextMenu (ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		this.lastSelection = ((AdapterContextMenuInfo) menuInfo).position;
		
		Movie movie = null;
		
		if (this.tabHost.getCurrentTab() == MOBILE_TAB) {
			movie = mobileAdapter.getItem(lastSelection);
			menu.setHeaderTitle(movie.getTitle());		
		}
		else {
			movie = webAdapter.getItem(lastSelection);
			menu.setHeaderTitle(movie.getIndexTitle());			
		}
		
		menu.add(0, MENU_ITEM_COMMENTS, 0, "Ver comentarios");
		menu.add(0, MENU_ITEM_CARD, 0, "Ver ficha");
		
		if (this.tabHost.getCurrentTab() == MOBILE_TAB)
			menu.add(0, MENU_ITEM_DELETE, 0, "Borrar ficha");
		else {
			if (CINeolFacade.isMovieStored(movie.getId()))
				menu.add(0, MENU_ITEM_DELETE, 0, "Borrar ficha");
			else
				menu.add(0, MENU_ITEM_STORE, 0, "Guardar ficha");
		}
		
		menu.add(0, MENU_ITEM_WEB, 0, "Ver ficha en la web");
		menu.add(0, MENU_ITEM_MAIL, 0, "Compartir ficha");
	}
	
	public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
		lastSelection = position;
		Movie movie = null;

		if (listView == this.mobileListView) {
			movie = this.mobileAdapter.getItem(lastSelection);
			MovieActivity.showMovie(this, movie, this, true, true);
		}
		else {
			movie = this.webAdapter.getItem(lastSelection);
			progressDialog = ProgressDialog.show(this, "", "Descargando ficha...", true);
			progressDialog.setCancelable(true);
			lastOptionContextMenu = MENU_ITEM_COMMENTS;
			CINeolManager.sharedManager().getMovieWithID(movie.getId());
		}		
	}
	
	private void setMovies(ArrayList<Movie> movies, int tab) {
		
		if (!this.modeSearch)
			mobileTextView.setText(NO_MOVIES_STORE_MSG);
		else
			mobileTextView.setText(NO_RESULTS_MSG);
		
		if (movies == null)
			return;
		
		if (tab == MOBILE_TAB) {
			if (!this.modeSearch)
				mobileTextView.setText(NO_MOVIES_STORE_MSG);
				
	       	mobileAdapter.setItems(movies);
	       	this.notifyMobileListChanged();
	       	this.sortMoviesBy(mobileTabSortedBy);
		}
		else {
			webTextView.setText(NO_RESULTS_MSG);
			
	       	webAdapter.setItems(movies);
	       	this.notifyWebListChanged();
	       	this.sortMoviesBy(webTabSortedBy);
		}       		
	}
	
	// Methods to sort movies.
    public void sortMoviesBy(int sortOption) {
    	Comparator<Movie> comparator = null;
    	switch (sortOption) {
    		case SORT_BY_TITLE:
    			if (this.tabHost.getCurrentTab() == MOBILE_TAB)
    				comparator = Movie.getTitleComparator(true);
    			else
    				comparator = Movie.getIndexTitleComparator(true);
    			break;
    			
    		case SORT_BY_GENRE:
    	    	comparator = Movie.getGenreComparator(true);
    	    	break;
    	    	
    		case SORT_BY_DURATION:
    	    	comparator = Movie.getDurationComparator(false);
    	    	break;
    	    	
    		case SORT_BY_RATING:
    	    	comparator = Movie.getRatingComparator(false);
    	    	break;
    	    	
    		case SORT_BY_YEAR:
    	    	comparator = Movie.getYearComparator(false);
    	    	break;
    	}
    	
    	if (this.tabHost.getCurrentTab() == MOBILE_TAB) {
    		mobileAdapter.sort(comparator);
        	mobileTabSortedBy = sortOption;
    	}
    	else {
    		webAdapter.sort(comparator);
        	webTabSortedBy = sortOption;
    	}
    }
    
	public void showDialogSortMovies() {
		
		CharSequence[] items = itemsWebTab;
		int temp = webTabSortedBy;
		
		if (this.tabHost.getCurrentTab() == MOBILE_TAB) {
			items = itemsMobileTab;
			temp = mobileTabSortedBy;
		}
			
		final int cardsSortedBy = temp;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Ordenar películas por...");
		
		builder.setSingleChoiceItems(items, cardsSortedBy, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	if (cardsSortedBy != item) {		       	
		    		sortMoviesBy(item);
		    	}
		    	
		    	dialog.dismiss();
		    }
		});
		
		AlertDialog alert = builder.create();
		alert.show();
    }

	
	public void searchMoviesByQuery(String query) {
		mobileTextView.setText(SEARCHING_MSG);
		webTextView.setText(SEARCHING_MSG);
		
		CINeolManager.sharedManager().searchMovies(query);
		this.setMovies(CINeolFacade.searchMovies(query), MOBILE_TAB);
	}

	public void cineolDidFinishLoadingComments(InputStream data) {}
	public void cineolDidFinishLoadingMovieShowtimes(InputStream data) {}
	public void cineolDidFinishLoadingNews(InputStream data) {}
	public void cineolDidDownloadThumbnail(final InputStream data) {}
	public void cineolDidDownloadThumbnails() {}
	public void cineolDidDownloadImage(final InputStream data) {}
	public void cineolDidFinishLoadingSingleNews(InputStream data) {}

	public void cineolDidFinishSearchMovies(final InputStream data) {
		XMLParserSearchMovies parser = new XMLParserSearchMovies(this, data, this);
		parser.parse();
	}
	
	public void cineolDidFinishLoadingMovie(InputStream data) {
		if (progressDialog != null && progressDialog.isShowing()) {
			XMLParserMovie parser = new XMLParserMovie(this, data, this, tabHost.getCurrentTab() == WEB_TAB);
			parser.parse();
		}
	}

	public void xmlParserDidStartDocument(XMLParser parser) {}
	public void xmlParserDidEndTag(XMLParser parser, String tag) {}
	public void xmlParserDidFindAttribute(XMLParser parser, String attributeName, String attributeValue) {}

	public void xmlParserDidEndDocument(XMLParser parser) {
		if (parser.parserType() == XMLParserSearchMovies.PARSE_TYPE) {			
			this.setMovies(((XMLParserSearchMovies) parser).getResults(), WEB_TAB);
		}
		else if (parser.parserType().equals(XMLParserMovie.PARSE_TYPE)) {
			Movie movie = ((XMLParserMovie) parser).getMovie();
			//this.onUpdateMovie(movie);

			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
				this.onContextItemSelected(movie, lastOptionContextMenu);
			}
		}
	}
	
	/*
	protected void onUpdateMovie(Movie movie) {
		Movie oldMovie = this.webAdapter.getItem(lastSelection);
		movie.setPosterThumbnail(oldMovie.getPosterThumbnail());
		
		this.webAdapter.replace(movie, lastSelection);
	}
	*/

	public void movieActivityDidLoadMovie(Movie movie) {
		// TODO Auto-generated method stub
	}
}