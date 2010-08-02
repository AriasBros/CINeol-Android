package es.leafsoft.cineol.activities;

import es.leafsoft.cineol.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;

import com.flurry.android.FlurryAgent;

import es.leafsoft.cineol.CINeolFacade;

import net.cineol.model.Movie;
import net.cineol.model.News;
import net.cineol.webservice.CINeolManager;
import net.cineol.webservice.CINeolNotificationCenter;
import net.cineol.xml.XMLParser;
import net.cineol.xml.XMLParserDelegate;
import net.cineol.xml.XMLParserMovie;
import net.cineol.xml.XMLParserPremiers;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
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
import app.cineol.adapters.PremiersAdapter;

public class PremiersActivity extends ListActivity
							  implements CINeolNotificationCenter, XMLParserDelegate, MovieActivityDelegate, SearchableActivity
{   
	private static final String TAG = "PremiersActivity";
	
	private static final String NO_LOAD_MOVIES 	= "NO_LOAD_MOVIES";
	private static final String MOVIES_COUNT	= "MOVIES_COUNT";
	
	private static final int	MENU_ITEM_COMMENTS 	= 0;
	private static final int	MENU_ITEM_STORE		= 1;
	private static final int	MENU_ITEM_DELETE 	= 2;
	private static final int	MENU_ITEM_WEB 		= 3;
	private static final int	MENU_ITEM_MAIL 		= 4;
	private static final int	MENU_ITEM_CARD 		= 5;
	
	private static final int	PREMIERS_SORTED_BY_DATE 	= 0;
	private static final int	PREMIERS_SORTED_BY_GENRE 	= 1;
	private static final int	PREMIERS_SORTED_BY_DURATION = 2;
	private static final int	PREMIERS_SORTED_BY_RATING 	= 3;

	private static int premiersSortedBy = 0;
	private int sizeOfProgress = 0;
	private int currentProgress = 1;
	private int lastSelection = 0;
	private int lastOptionContextMenu = -1;
	
	private boolean downloading = true;
	private boolean processing  = false;
	private boolean onPause  = false;

	private PremiersAdapter adapter;
	private ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
	private ProgressDialog progressDialog;
	
			
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (this.isSearchActivity())
        	this.onPerformSearch();
        else {
	        requestWindowFeature(Window.FEATURE_PROGRESS);
	        setContentView(R.layout.premiers_list_view);
	        this.setProgressBarIndeterminateVisibility(false);
	                        
	        adapter = new PremiersAdapter(this, R.layout.movie_row, new ArrayList<Movie>());
	        setListAdapter(adapter);
	        
	        this.registerForContextMenu(this.getListView());
	        
			CINeolManager.sharedManager().addObserver(this, CINeolManager.kCINeolDidFinishLoadingMovieShowtimesNotification);
			CINeolManager.sharedManager().addObserver(this, CINeolManager.kCINeolDidFinishLoadingMovieNotification);
			
			this.downloading = savedInstanceState == null ||
			  						!savedInstanceState.getBoolean(NO_LOAD_MOVIES);
						
			if (downloading)
				CINeolManager.sharedManager().getMovieShowtimes(1);
			else {
				int news = savedInstanceState.getInt(MOVIES_COUNT);
				boolean noLoad = !this.downloading && !this.processing;

				if (noLoad) {
					this.adapter.clear();
					for (int i = 0; i < news; i++)
						adapter.add((Movie) savedInstanceState.getParcelable("item " + i));
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

		CINeolManager.sharedManager().removeObserver(this, CINeolManager.kCINeolDidFinishLoadingMovieShowtimesNotification);
		CINeolManager.sharedManager().removeObserver(this, CINeolManager.kCINeolDidFinishLoadingMovieNotification);
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
		
		int movies = adapter.getCount();
		boolean noLoad = !this.downloading && !this.processing;
			
		outState.putBoolean(NO_LOAD_MOVIES, noLoad);
		outState.putInt(MOVIES_COUNT, movies);
		
		if (noLoad)
			for (int i = 0; i < movies; i++)
				outState.putParcelable("item " +  i, adapter.getItem(i));
	}
	
	protected void onUpdateMovie(Movie movie) {
		Movie oldMovie = this.adapter.getItem(lastSelection);
		movie.setPosterThumbnail(oldMovie.getPosterThumbnail());
		
		this.adapter.remove(oldMovie);
		this.adapter.insert(movie, lastSelection);
	}

    // CINeol Delegate Methods.
	public void cineolDidFinishLoadingComments(InputStream data){}
	public void cineolDidFinishLoadingNews(InputStream data) {}
	public void cineolDidFinishSearchMovies(InputStream data) {}
	public void cineolDidDownloadThumbnail(final InputStream data) {}
	public void cineolDidDownloadThumbnails() {}
	public void cineolDidDownloadImage(final InputStream data) {}
	public void cineolDidFinishLoadingSingleNews(InputStream data) {}

	public void cineolDidFinishLoadingMovieShowtimes(InputStream data) {
		this.downloading = false;

		XMLParserPremiers parser = new XMLParserPremiers(this, data);
		parser.setDelegate(this);
		parser.parse();		
	}
	
	public void cineolDidFinishLoadingMovie(InputStream data) {
		if (progressDialog != null && progressDialog.isShowing()) {
			XMLParserMovie parser = new XMLParserMovie(this, data, this, false);
			parser.parse();
		}
	}

	
	public void xmlParserDidStartDocument(XMLParser parser) {
		if (parser.parserType().equals(XMLParserPremiers.PARSE_TYPE)) {
			this.processing = true;
        	this.getParent().setTitle("Procesando datos...");
		}
	}

	public void xmlParserDidEndDocument(XMLParser parser) {
		if (parser.parserType() == XMLParserPremiers.PARSE_TYPE) {			
	        this.getParent().setProgress(10000);
	        this.getParent().setTitle("CINeol");
	        
	        // Habilitamos los items del menu.
	        for (MenuItem item : menuItems)
	        	item.setEnabled(true);
	        
	        this.processing = false;
		}
		else if (parser.parserType().equals(XMLParserMovie.PARSE_TYPE)) {
			Movie movie = ((XMLParserMovie) parser).getMovie();
			this.onUpdateMovie(movie);

			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();

				switch (lastOptionContextMenu) {
					case MENU_ITEM_CARD:
						MovieActivity.showMovie(this, movie, this, false, false);
						break;
				
					case MENU_ITEM_STORE:
						CINeolFacade.storeMovie(movie);
						break;
	
					case MENU_ITEM_WEB:
						CINeolFacade.showMovieOnCINeol(this, movie);
						break;
	
					case MENU_ITEM_MAIL:
						CINeolFacade.sendMovie(this, movie);
						break;
				}
			}
		}
	}

	public void xmlParserDidEndTag(XMLParser parser, String tag) {		
		if (parser.parserType().equals(XMLParserPremiers.PARSE_TYPE)) {
			// Obtenemos la ultima pelicula procesada.
			Movie movie = ((XMLParserPremiers)parser).getMovie();
			
			if (movie == null)
				return;
			
			// La añadimos al adapter.
	        adapter.add(movie);
	        
			// Recargamos la ListView.
			adapter.notifyDataSetChanged();
	        
	        if (!onPause)
	        	this.getParent().setProgress(sizeOfProgress * currentProgress);
	        currentProgress++;
		}
	}
	
	public void xmlParserDidFindAttribute(XMLParser parser, String attributeName, String attributeValue) {
		if (attributeName.equals(XMLParserPremiers.kXMLAttributeTotal)) {
			sizeOfProgress = 10000 / (Integer.valueOf(attributeValue).intValue() + 1);

	        this.getParent().setProgressBarIndeterminate(false);
	        
	        if (!onPause)
	        	this.getParent().setProgress(sizeOfProgress);
	        
	        currentProgress++;
		}
	}
	
	protected void onListItemClick (ListView l, View v, int position, long id) {
		lastSelection = position;
		MovieActivity.showMovie(this, adapter.getItem(position), this, true, false);
	}
	
	
	// ContextMenu Methods.
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		lastSelection = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
		Movie movie = adapter.getItem(lastSelection);
		
		lastOptionContextMenu = item.getItemId();
		
		switch (lastOptionContextMenu) {
			case MENU_ITEM_COMMENTS:
				MovieActivity.showMovie(this, movie, this, true, false);
				break;
				
			case MENU_ITEM_DELETE:
				CINeolFacade.deleteMovie(movie.getId());
				break;
				
			case MENU_ITEM_CARD:
			case MENU_ITEM_STORE:
			case MENU_ITEM_WEB:
			case MENU_ITEM_MAIL:
				
				if (movie.getUrlCINeol() == null) {
					progressDialog = ProgressDialog.show(this, "", "Descargando ficha...", true);
					progressDialog.setCancelable(true);
					CINeolManager.sharedManager().getMovieWithID(movie.getId());
				}
				else 				
					switch (lastOptionContextMenu) {
						case MENU_ITEM_CARD:
							MovieActivity.showMovie(this, movie, this, false, false);
							break;
					
						case MENU_ITEM_STORE:
							CINeolFacade.storeMovie(movie);
							break;
		
						case MENU_ITEM_WEB:
							CINeolFacade.showMovieOnCINeol(this, movie);
							break;
		
						case MENU_ITEM_MAIL:
							CINeolFacade.sendMovie(this, movie);
							break;
					}
				break;
		}
		
		return true;
	}
	
	public void onCreateContextMenu (ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		Movie movie = adapter.getItem(((AdapterContextMenuInfo) menuInfo).position);
		
		menu.setHeaderTitle(movie.getTitle());		
		menu.add(0, MENU_ITEM_COMMENTS, 0, "Ver comentarios");
		menu.add(0, MENU_ITEM_CARD, 0, "Ver ficha");
		
		
		if (CINeolFacade.isMovieStored(movie.getId()))
			menu.add(0, MENU_ITEM_DELETE, 0, "Borrar ficha");
		else
			menu.add(0, MENU_ITEM_STORE, 0, "Guardar ficha");
		
		menu.add(0, MENU_ITEM_WEB, 0, "Ver ficha en la web");
		menu.add(0, MENU_ITEM_MAIL, 0, "Compartir ficha");
	}
	
	
	// OptionsMenu Methods.
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.premiers_view_menu, menu);
        
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    	menu.findItem(R.id.sort).setEnabled(!downloading && !processing);
		
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    	switch (item.getItemId()) {
	    	case R.id.sort:
	    		this.showDialogSortMovies();
	    		break;
	    		
	    	case R.id.about:
	    		AboutActivity.open(this);
	    		break;
    	}
    	
		return true;
    }
    
    public void sortMoviesBy(int sortOption) {
    	if (premiersSortedBy == sortOption)
    		return;
    	
    	Comparator<Movie> comparator = null;
    	switch (sortOption) {
    		case PREMIERS_SORTED_BY_DATE:
    	    	comparator = Movie.getSpainPremierComparator(false);
    	    	break;
    	    	
    		case PREMIERS_SORTED_BY_GENRE:
    	    	comparator = Movie.getGenreComparator(true);
    	    	break;
    	    	
    		case PREMIERS_SORTED_BY_DURATION:
    	    	comparator = Movie.getDurationComparator(false);
    	    	break;
    	    	
    		case PREMIERS_SORTED_BY_RATING:
    	    	comparator = Movie.getRatingComparator(false);
    	    	break;
    	}
    	
    	adapter.sort(comparator);
    	premiersSortedBy = sortOption;
    }
    
	public void showDialogSortMovies() {
		final CharSequence[] items = {"Fecha de estreno", "Género", "Duración", "Puntuación"};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Ordenar películas por...");
		
		builder.setSingleChoiceItems(items, premiersSortedBy, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	sortMoviesBy(item);
		    	dialog.dismiss();
		    }
		});
		
		AlertDialog alert = builder.create();
		alert.show();
    }


	public void movieActivityDidLoadMovie(Movie movie) {
		this.onUpdateMovie(movie);
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
}