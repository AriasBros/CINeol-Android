package app.cineol.adapters;

import java.util.ArrayList;

import es.leafsoft.adapters.DAAdapter;
import es.leafsoft.utils.DateUtils;

import net.cineol.model.Movie;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import es.leafsoft.cineol.R;
import app.cineol.rows.PremiersRowView;

public class PremiersAdapter extends DAAdapter<Movie> {

	private BitmapDrawable poster;
	private int XMLResource;
	
	public PremiersAdapter(Context context, int textViewResourceId, ArrayList<Movie> objects) {
		super(context, textViewResourceId, objects);		
		
		XMLResource = textViewResourceId;
		poster = (BitmapDrawable) context.getResources().getDrawable(R.drawable.poster);
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		PremiersRowView rowView;
		
        if (convertView == null) {
    		Activity activity = (Activity) getContext();
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(XMLResource, null);
            
            rowView = new PremiersRowView(convertView);
            convertView.setTag(rowView);
        }
        else
        	rowView = (PremiersRowView)convertView.getTag();
        
        Movie movie = this.getItem(position);
        if (movie != null) {
        	if (movie.getTitle() != null) {
	        	rowView.getTitleView().getLayoutParams().width = 240;
	        	rowView.getTitleView().setText(movie.getTitle());                            
	        	rowView.getRatingView().setRating(movie.getRating()/2);
	        	rowView.getGenreView().setText(movie.getGenre());
	        	
	        	if (movie.getDuration() > 0)
	        		rowView.getDurationView().setText(String.valueOf(movie.getDuration()) + " min.");
	        	else
	        		rowView.getDurationView().setText("");
	        		
	        	rowView.getSynopsisView().setText(movie.getSynopsis());
	        	
	        	if (movie.getPosterThumbnail() != null)
	        		rowView.getPosterView().setImageDrawable(movie.getPosterThumbnail());
	        	else
	        		rowView.getPosterView().setImageDrawable(poster);
	        	
	        	rowView.getRatingView().setVisibility(View.VISIBLE);
	        	rowView.getPosterView().setVisibility(View.VISIBLE);
	        	rowView.getSynopsisView().setVisibility(View.VISIBLE);
	        	rowView.getDurationView().setVisibility(View.VISIBLE);
        	}
        	else {
	        	rowView.getTitleView().getLayoutParams().width = LayoutParams.FILL_PARENT;
	        	rowView.getTitleView().setText(movie.getIndexTitle());       
	        	rowView.getGenreView().setText(String.valueOf(movie.getYear()));

	        	rowView.getRatingView().setVisibility(View.GONE);
	        	rowView.getPosterView().setVisibility(View.GONE);
	        	rowView.getSynopsisView().setVisibility(View.GONE);
	        	rowView.getDurationView().setVisibility(View.GONE);
        	}
        }
        
        return convertView;
	}
}