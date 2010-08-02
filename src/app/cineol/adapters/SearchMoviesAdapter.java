package app.cineol.adapters;

import java.util.ArrayList;

import net.cineol.model.Movie;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import es.leafsoft.cineol.R;
import app.cineol.rows.PremiersRowView;
import app.cineol.rows.SearchMoviesRowView;

public class SearchMoviesAdapter extends ArrayAdapter<Movie> {

	private int XMLResource;
	
	public SearchMoviesAdapter(Context context, int textViewResourceId, ArrayList<Movie> objects) {
		super(context, textViewResourceId, objects);		
		
		XMLResource = textViewResourceId;
	}
	

	public View getView(int position, View convertView, ViewGroup parent) {

		SearchMoviesRowView rowView;
		
        if (convertView == null) {
    		Activity activity = (Activity) getContext();
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(XMLResource, null);
            
            rowView = new SearchMoviesRowView(convertView);
            convertView.setTag(rowView);
        }
        else
        	rowView = (SearchMoviesRowView)convertView.getTag();
        
        Movie movie = this.getItem(position);
        if (movie != null) {
        	rowView.getTitleView().setText(movie.getTitle());                            
        	rowView.getYearView().setText(movie.getYear());                            
        }
        
        return convertView;
	}
}