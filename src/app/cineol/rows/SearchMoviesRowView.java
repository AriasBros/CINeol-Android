package app.cineol.rows;

import es.leafsoft.cineol.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class SearchMoviesRowView {

	private TextView 	titleView;
	private TextView 	yearView;

	
	public SearchMoviesRowView (View view) {
		titleView = (TextView)view.findViewById(R.id.title);
		yearView = (TextView)view.findViewById(R.id.year);
	}
	
	public TextView getTitleView() {
		return titleView;
	}
	
	public TextView getYearView() {
		return yearView;
	}
}
