package es.leafsoft.cineol.activities;

import java.io.BufferedInputStream;
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

import es.cineol.dialogs.CommentDialog;
import es.cineol.dialogs.RatingDialog;
import es.cineol.dialogs.CommentDialog.CommentDialogDelegate;
import es.cineol.dialogs.RatingDialog.RatingDialogDelegate;
import es.leafsoft.cineol.CINeolFacade;
import es.leafsoft.cineol.MovieHeaderView;
import es.leafsoft.cineol.MovieHeaderViewDelegate;
import es.leafsoft.gallery.GalleryActivity;

import net.cineol.model.Image;
import net.cineol.model.Movie;
import net.cineol.model.Person;
import net.cineol.model.Post;
import net.cineol.model.Video;
import net.cineol.utils.CINeolUtils;
import net.cineol.webservice.CINeolManager;
import net.cineol.webservice.CINeolNotificationCenter;
import net.cineol.xml.XMLParser;
import net.cineol.xml.XMLParserComments;
import net.cineol.xml.XMLParserDelegate;
import net.cineol.xml.XMLParserMovie;
import es.leafsoft.cineol.R;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;
import app.cineol.adapters.CommentsAdapter;
import app.cineol.adapters.CreditsAdapter;
import app.cineol.adapters.DetailsAdapter;
import app.cineol.adapters.MultimediaAdapter;
import app.cineol.adapters.MyCINeolAdapter;
import app.cineol.adapters.SectionsAdapter;
import app.cineol.adapters.SynopsisAdapter;
import app.cineol.adapters.ThumbnailsGalleryAdapter;

public class MovieActivity extends ListActivity
						   implements CINeolNotificationCenter,
						   			  XMLParserDelegate,
						   			  OnClickListener,
						   			  OnItemClickListener,
						   			  MovieHeaderViewDelegate,
						   			  RatingDialogDelegate,
						   			  CommentDialogDelegate
{
	private static final int kNumberOfCommentsToLoad = 5;
	
	private static final String TAG = "MovieActivity";
	private static Movie movie = null;
	private static MovieActivityDelegate delegate = null;

	private static boolean showFirstComments = true;
	private static boolean finishAfterDelete = false;
	private static boolean downloadThumbPoster = false;
	
	private static final int LIST_FRONT = 0;
	private static final int LIST_BACK  = 1;
	
	private SectionsAdapter adapterListFront;
	private SectionsAdapter adapterListBack;

	private MyCINeolAdapter sectionMyCINeol;
	private ArrayAdapter<String> sectionSynopsis;
	private CommentsAdapter sectionComments;

	private MultimediaAdapter sectionMultimedia;
	private DetailsAdapter sectionDetails;
	private CreditsAdapter sectionCredits;
	private CreditsAdapter sectionActors;
	
	private ThumbnailsGalleryAdapter thumbsGalleryAdapter;

	private MovieHeaderView headerView;
	private ListView 		listBack;
	private ViewFlipper 	viewFlipper;
	private Button		 	moreCommentsButton;
	
	private int numberOfComments = 0;
	private int numberOfLoadedComments = 0;
	private boolean movieLoaded = false;
	private int listLoaded = 0;
	
	
	
    static void showMovie(Context context, Movie movie, MovieActivityDelegate delegate, boolean showFirstComments, boolean finishAfterDelete) {
        final Intent intent = new Intent(context, MovieActivity.class);
        
        MovieActivity.delegate = delegate;
        MovieActivity.movie = movie;
        MovieActivity.showFirstComments = showFirstComments;
        MovieActivity.finishAfterDelete = finishAfterDelete;
        
        context.startActivity(intent);
    }
    
    static void showMovie(Context context, Movie movie, MovieActivityDelegate delegate, boolean thumbPoster, boolean showFirstComments, boolean finishAfterDelete) {
        final Intent intent = new Intent(context, MovieActivity.class);
        
        MovieActivity.delegate = delegate;
        MovieActivity.movie = movie;
        MovieActivity.showFirstComments = showFirstComments;
        MovieActivity.finishAfterDelete = finishAfterDelete;
        MovieActivity.downloadThumbPoster = thumbPoster;
        
        context.startActivity(intent);
    }
    
    private void _updateHeaderView() {
    	
    	if (movie.getPosterThumbnail() != null)
    		((ImageView)headerView.findViewById(R.id.poster_thumb_view)).setImageDrawable(movie.getPosterThumbnail());
    	
    	((TextView)headerView.findViewById(R.id.title)).setText(movie.getTitle());    	
    	((RatingBar)headerView.findViewById(R.id.rating)).setRating(movie.getRating() / 2);
    	((TextView)headerView.findViewById(R.id.genre)).setText(movie.getGenre());    
    	
    	int votes = movie.getVotes();
    	String rating = "(" + movie.getRating() + " - " + votes;
    	if (votes == 1)
    		rating += " voto)";
    	else
    		rating += " votos)";
    		
    	((TextView)headerView.findViewById(R.id.votes)).setText(rating);    
    	
    	int duration = movie.getDuration();
     	
    	if (duration == 0)
        	//((TextView)headerView.findViewById(R.id.duration)).setText("N/A min."); 
        	((TextView)headerView.findViewById(R.id.duration)).setText(movie.getCountry());    		
        else
       		((TextView)headerView.findViewById(R.id.duration)).setText(String.valueOf(duration) + " min.");    	
    	
    	String date = CINeolUtils.stringFromDate(movie.getSpainPremier(), "EEEE, dd MMMM yyyy");
    	
    	if (date != null)
    		((TextView)headerView.findViewById(R.id.premier)).setText("Estreno el " + date);
    	else
    		((TextView)headerView.findViewById(R.id.premier)).setText("Año " + movie.getYear());

    }
    
    private void _updateListViewBack() {
    	
    	if (movie.getImages() != null && movie.getImages().size() > 0) {
			thumbsGalleryAdapter = new ThumbnailsGalleryAdapter(this, new ArrayList<Drawable>());
			sectionMultimedia.addGallery(thumbsGalleryAdapter);
    	}
    	
    	// Actualizamos la seccion multimedia.
    	for (Video video : movie.getVideos())
    		sectionMultimedia.addVideo(video.getDescription());
    	
    	// Actualizamos la seccion de los detalles.
    	sectionDetails.add(String.valueOf(movie.getYear()));
    	sectionDetails.add(movie.getCountry());
    	sectionDetails.add(movie.getFormat());
    	sectionDetails.add(movie.getOriginalTitle());    	
    	
    	if (movie.getOriginalPremier() != null)
    		sectionDetails.add(CINeolUtils.stringFromDate(movie.getOriginalPremier(), "EEEE, dd MMMM yyyy"));
    	else
    		sectionDetails.add("N/A");
    	
    	if (movie.getSpainTakings() > 0)
        	sectionDetails.add(String.valueOf(movie.getSpainTakings()));
    	else
    		sectionDetails.add("N/A");
    	
    	if (movie.getUsaTakings() > 0)
        	sectionDetails.add(String.valueOf(movie.getUsaTakings()));
    	else
    		sectionDetails.add("N/A");
    	
    	// Actualizamos la seccion de los creditos.
    	for (Person person : movie.getCredits())
    		sectionCredits.add(person);
    	
    	// Actualizamos la seccion del reparto.
    	for (Person person : movie.getActors())
    		sectionActors.add(person);    	
    }
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        this.setContentView(R.layout.movie_view);

        //this.setProgressBarIndeterminate(true);
        //this.setProgress(1); // HACK para que se muestre la barra de progreso nada mas empezar la Activity.
        
        headerView = (MovieHeaderView)findViewById(R.id.header);
        headerView.setDelegate(this);
        
        // Actualizamos la vista con los datos de la pelicula.
        this._updateHeaderView();
        
        moreCommentsButton = new Button(this);
        moreCommentsButton.setOnClickListener(this);
        moreCommentsButton.setText("Cargando...");
        moreCommentsButton.setEnabled(false);
        moreCommentsButton.setTextColor(0xFF777777);
        moreCommentsButton.setShadowLayer(0.5f, 0.0f, 1.0f, 0xFFFFFFFF);
        moreCommentsButton.setTextSize(16);
        moreCommentsButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        
		listBack	 = (ListView) this.findViewById(R.id.list_back);
		this.listBack.setOnItemClickListener(this);

        viewFlipper  = (ViewFlipper) this.findViewById(R.id.viewFlipper);
		
        this.getListView().addFooterView(moreCommentsButton);
        
        // Actualizamos la lista.
        adapterListFront = new SectionsAdapter(this, R.layout.list_header);
        adapterListBack = new SectionsAdapter(this, R.layout.list_header);
        
        // listFront Sections.
        sectionMyCINeol = new MyCINeolAdapter(this, R.layout.my_cineol_rating_row, R.layout.my_cineol_comment_row);
        sectionSynopsis = new SynopsisAdapter(this, R.layout.synopsis_row, new String[] {movie.getSynopsis()});
        sectionComments = new CommentsAdapter(this, R.layout.comment_row, new ArrayList<Post>());
        
        adapterListFront.addSection("Mi CINeol", this.sectionMyCINeol);
        adapterListFront.addSection("Sinopsis", this.sectionSynopsis);
        adapterListFront.addSection("Comentarios", this.sectionComments);
        
        // listBack Sections.
        sectionMultimedia = new MultimediaAdapter(this, R.layout.thumbs_gallery_view, R.layout.my_cineol_row, this);
        sectionDetails	  = new DetailsAdapter(this, R.layout.detail_row, new ArrayList<String>());
        sectionCredits	  = new CreditsAdapter(this, R.layout.credit_row, new ArrayList<Person>());
        sectionActors	  = new CreditsAdapter(this, R.layout.credit_row, new ArrayList<Person>());

        adapterListBack.addSection("Multimedia", this.sectionMultimedia);
        adapterListBack.addSection("Detalles", this.sectionDetails);
        adapterListBack.addSection("Créditos", this.sectionCredits);
        adapterListBack.addSection("Reparto", this.sectionActors);
        
        this.setListAdapter(adapterListFront);
        this.listBack.setAdapter(adapterListBack);
        
		CINeolManager.sharedManager().addObserver(this, CINeolManager.kCINeolDidFinishDownloadThumbnailNotification);
		CINeolManager.sharedManager().addObserver(this, CINeolManager.kCINeolDidFinishDownloadThumbnailsNotification);
		CINeolManager.sharedManager().addObserver(this, CINeolManager.kCINeolDidFinishLoadingMovieNotification);
		CINeolManager.sharedManager().addObserver(this, CINeolManager.kCINeolDidFinishLoadingCommentsNotification);
		
		if (movie.getUrlCINeol() != null) {
			movieLoaded = true;
			this.setProgressBarIndeterminateVisibility(false);
	        this.setTitle(movie.getTitle());
	        this._updateListViewBack();
	       
	        CINeolManager.sharedManager().getThumbnails(movie.getImages());
	        
	        if (!showFirstComments) {
	    		HashMap<String, String> value = new HashMap<String, String>();
	    		value.put("Modo", "Opción menú contextual");

	    		FlurryAgent.onEvent(CINeolFacade.FlurryEventShowMovieDetails, value);
	        	
	        	this.switchToBackList();
	        }
		}
		else {
			this.setProgressBarIndeterminateVisibility(true);
	        this.setTitle("Descargando ficha...");
			CINeolManager.sharedManager().getMovieWithID(movie.getId());
		}
		
		this.loadComments();
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		FlurryAgent.onStartSession(this, CINeolFacade.API_Key_Flurry);
		
		HashMap<String, String> value = new HashMap<String, String>();
		value.put("ID película", movie.getId() + "");
		value.put("Título película", movie.getTitle());
		value.put("Género pelicula", movie.getGenre());
		value.put("URL CINeol", movie.getUrlCINeol());

		FlurryAgent.onEvent(CINeolFacade.FlurryEventShowMovie, value);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
				
		CINeolManager.sharedManager().removeObserver(this, CINeolManager.kCINeolDidFinishDownloadThumbnailNotification);
		CINeolManager.sharedManager().removeObserver(this, CINeolManager.kCINeolDidFinishDownloadThumbnailsNotification);		CINeolManager.sharedManager().removeObserver(this, CINeolManager.kCINeolDidFinishLoadingMovieNotification);
		CINeolManager.sharedManager().removeObserver(this, CINeolManager.kCINeolDidFinishLoadingCommentsNotification);
	}
	
	

	/*
	 * CINeol Delegate Methods
	 */
	public void cineolDidFinishLoadingMovie(InputStream data) {
		XMLParserMovie parser = new XMLParserMovie(this, data, this, downloadThumbPoster);
		parser.parse();
	}
	
	public void cineolDidFinishLoadingComments(InputStream data) {
		XMLParserComments parser = new XMLParserComments(this, data);
		parser.setDelegate(this);
		parser.parse();
	}
	
	public void cineolDidDownloadThumbnail(final InputStream data) {
		BufferedInputStream stream = new BufferedInputStream(data);		
		Bitmap bitmap = BitmapFactory.decodeStream(stream);
		
		if (bitmap != null)
			this.thumbsGalleryAdapter.add(new BitmapDrawable(this.getResources(), bitmap));
	}
	
	public void cineolDidDownloadThumbnails() {}
	public void cineolDidFinishLoadingNews(InputStream data) {}
	public void cineolDidFinishLoadingMovieShowtimes(InputStream data) {}
	public void cineolDidFinishSearchMovies(InputStream data) {}
	public void cineolDidDownloadImage(final InputStream data) {}
	public void cineolDidFinishLoadingSingleNews(InputStream data) {}

	/*
	 * XMLParser Delegate Methods
	 */
	public void xmlParserDidEndDocument(XMLParser parser) {
		if (parser.parserType().equals(XMLParserMovie.PARSE_TYPE)) {
			Movie newMovie = ((XMLParserMovie)parser).getMovie();

			newMovie.setPosterThumbnail(movie.getPosterThumbnail());
			movie = newMovie;
			
			if (delegate != null)
				delegate.movieActivityDidLoadMovie(newMovie);
			
			this.movieLoaded = true;
			this._updateListViewBack();
			this.setProgressBarIndeterminateVisibility(false);
			this.setTitle(movie.getTitle());
			
			if (movie.getImages() != null && movie.getImages().size() > 0)
	    		CINeolManager.sharedManager().getThumbnails(movie.getImages());
		}
		else if (parser.parserType() == XMLParserComments.PARSE_TYPE) {
			
			ArrayList<Post> posts = ((XMLParserComments) parser).getComments();
			
			for (Post post : posts) {
				this.sectionComments.add(post);
				numberOfLoadedComments++;
			}

	        adapterListFront.notifyDataSetChanged();
	        
	        if (numberOfComments > 0) {
		        if (numberOfComments == numberOfLoadedComments && moreCommentsButton.isEnabled()) {
		            moreCommentsButton.setText("No hay más comentarios");
		            moreCommentsButton.setTextColor(0xFF999999);
		        	moreCommentsButton.setEnabled(false);
		        }
		        else {
		        	moreCommentsButton.setText("Más comentarios");
		        	moreCommentsButton.setEnabled(true);
		        }
	        }
	        else {
	            moreCommentsButton.setText("No hay comentarios");
	            moreCommentsButton.setTextColor(0xFF999999);
	        	moreCommentsButton.setEnabled(false);	        	
	        }
		}
	}

	public void xmlParserDidStartDocument(XMLParser parser) {}
	public void xmlParserDidEndTag(XMLParser parser, String tag) {}	
	
	public void xmlParserDidFindAttribute(XMLParser parser, String attributeName, String attributeValue) {
		if (attributeName.equals(XMLParserComments.kXMLAttributeTotalPosts)) {
			numberOfComments = Integer.valueOf(attributeValue);
		}
	}

	public void ratingDialogDidClickOnPositiveButton(RatingDialog dialog, float rating) {
 	   	//TODO
		sectionMyCINeol.setRating(rating);
 	   	adapterListFront.notifyDataSetChanged();
	}
	
	public void commentDialogDidClickOnPositiveButton(CommentDialog dialog, String comment) {
 	   	//TODO
		if (comment.length() <= 0)
			return;
		
		sectionMyCINeol.setComment(comment);
 	   	adapterListFront.notifyDataSetChanged();
	}
	
	// Click en la listFront.
	protected void onListItemClick (ListView l, View v, int position, long id) {
		if (position == 1) {
			RatingDialog dialog = new RatingDialog(this);	
			dialog.setDelegate(this);
			dialog.setTitle("¡Puntúala!");
			dialog.setLabel("Puntuar la película con un ");
			dialog.setPositiveTitleButton("Aceptar");
			dialog.setNegativeTitleButton("Cancelar");
			dialog.show();
		}
		else if (position == 2) {
			CommentDialog dialog = new CommentDialog(this);
			dialog.setDelegate(this);
			dialog.setTitle("Publicar comentario");
			dialog.setPositiveTitleButton("Aceptar");
			dialog.setNegativeTitleButton("Cancelar");
			dialog.show();
		}
	}
	
	// Click en la listBack.
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		if (adapter == this.listBack) {
			Video video = null;
			
			if( movie.getImages() != null && movie.getImages().size() > 0)
				video = movie.getVideos().get(position - 2);
			else
				video = movie.getVideos().get(position - 1);
			
			String url = CINeolManager.sharedManager().getStringURLForVideoWithID(video.getId());
			VideoPlayerActivity.play(this, url);
		}
		else {
    		HashMap<String, String> value = new HashMap<String, String>();
    		value.put("ID película", movie.getId() + "");
    		value.put("Título película", movie.getTitle());
    		value.put("Género pelicula", movie.getGenre());

    		FlurryAgent.onEvent(CINeolFacade.FlurryEventShowMovieGallery, value);
    		
			ArrayList<String> url_photos = new ArrayList<String>();
			for (Image image : movie.getImages())
				url_photos.add("http://www.cineol.net/" + image.getUrlImage());
			
			GalleryActivity.showGallery(this, (String)this.getTitle(), position, url_photos.size(), true, url_photos, null);		
		}
	}
	
	public void onClick(View view) {
		if (view == this.moreCommentsButton)
			this.loadComments();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_view_menu, menu);

        return this.movieLoaded;
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    	boolean store = CINeolFacade.isMovieStored(movie.getId());

    	menu.findItem(R.id.movie).setVisible(listLoaded == LIST_FRONT);
    	menu.findItem(R.id.comments).setVisible(listLoaded != LIST_FRONT);
    	
    	menu.findItem(R.id.delete).setVisible(store);
    	menu.findItem(R.id.save).setVisible(!store);
    		
    	return this.movieLoaded;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    	switch (item.getItemId()) {
	    	case R.id.movie:
	    		HashMap<String, String> value = new HashMap<String, String>();
	    		value.put("Modo", "Opción menú");

	    		FlurryAgent.onEvent(CINeolFacade.FlurryEventShowMovieDetails, value);
	    		
	    		this.switchToBackList();
	    		break;
	    		
	    	case R.id.comments:
	    		this.switchToFrontList();
	    		break;
	    		
	    	case R.id.save:
	    		CINeolFacade.storeMovie(movie);
	    		break;
	    		
	    	case R.id.delete:
	    		if (CINeolFacade.deleteMovie(movie.getId()) && finishAfterDelete)
	    			this.finish();
	    		break;
    	
	    	case R.id.web:
	    		CINeolFacade.showMovieOnCINeol(this, movie);
	    		break;
	    		
	    	case R.id.mail:
	    		CINeolFacade.sendMovie(this, movie);
	    		break;
    	}
    	
		return true;
    }
	
	private void loadComments() {
		CINeolManager.sharedManager().getCommentsForMovieWithID(movie.getId(), numberOfLoadedComments + 1, kNumberOfCommentsToLoad);
	}
	
	private void switchToBackList() {
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,  R.anim.push_left_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
		
		viewFlipper.showNext();
		listLoaded = LIST_BACK;
	}
	
	private void switchToFrontList() {
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,  R.anim.push_right_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
		
		viewFlipper.showPrevious();
		listLoaded = LIST_FRONT;
	}

	public void movieHeaderViewDidTouchMove(int offset) {
		this.viewFlipper.scrollTo(viewFlipper.getScrollX() + offset, 0);
	}

	public void movieHeaderViewDidTouchToLeftToRight() {
		if (this.movieLoaded && listLoaded == LIST_BACK)
			this.switchToFrontList();
	}

	public void movieHeaderViewDidTouchToRightToLeft() {
		HashMap<String, String> value = new HashMap<String, String>();
		value.put("Modo", "Deslizando");

		FlurryAgent.onEvent(CINeolFacade.FlurryEventShowMovieDetails, value);
		
		if (this.movieLoaded && listLoaded == LIST_FRONT)
			this.switchToBackList();
	}
	
	public void movieHeaderViewDidTouchOnPosterThumbView(){
		HashMap<String, String> value = new HashMap<String, String>();
		value.put("ID película", movie.getId() + "");
		value.put("Título película", movie.getTitle());
		value.put("URL CINeol", movie.getUrlCINeol());

		FlurryAgent.onEvent(CINeolFacade.FlurryEventShowMoviePoster, value);
		
		ArrayList<String> photos = new ArrayList<String>();
		photos.add(CINeolUtils.urlForPosterForMovieWithID(movie.getId()));
		GalleryActivity.showGallery(this, "Cartel de " + movie.getTitle(), 0, 1, false, photos, null);		
	}
}