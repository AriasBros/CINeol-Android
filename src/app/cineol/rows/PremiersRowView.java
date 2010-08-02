package app.cineol.rows;

import es.leafsoft.cineol.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PremiersRowView {

	private RelativeLayout 	parentView;
	private TextView 		titleView;
	private TextView 		genreView;
	private TextView		durationView;
	private TextView 		synopsisView;
	
	private ImageView 	posterView;
	private RatingBar 	ratingView;
	
	public PremiersRowView (View view) {
		parentView = (RelativeLayout)view.findViewById(R.id.movie_row);
		titleView = (TextView)view.findViewById(R.id.title);
		genreView = (TextView)view.findViewById(R.id.genre);
		durationView = (TextView)view.findViewById(R.id.duration);
		synopsisView = (TextView)view.findViewById(R.id.synopsis);
		posterView = (ImageView)view.findViewById(R.id.poster);
		ratingView = (RatingBar)view.findViewById(R.id.rating);
	}
	
	public RelativeLayout getParentView() {
		return parentView;
	}
	
	public TextView getTitleView() {
		return titleView;
	}
	public TextView getGenreView() {
		return genreView;
	}
	public TextView getDurationView() {
		return durationView;
	}
	public TextView getSynopsisView() {
		return synopsisView;
	}
	public ImageView getPosterView() {
		return posterView;
	}
	public RatingBar getRatingView() {
		return ratingView;
	}
}
