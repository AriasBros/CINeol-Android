package app.cineol.rows;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import es.leafsoft.cineol.R;

public class MyCINeolRowView {

	private static int RATING_VIEW_TYPE 	= 0;
	private static int COMMENT_VIEW_TYPE 	= 1;
	
	private RatingBar 	ratingView;
	private TextView 	commentView;	
	private TextView 	labelView;
	
	public MyCINeolRowView (View view, int type) {
		labelView	= (TextView)view.findViewById(R.id.label);

		if (type == RATING_VIEW_TYPE)
			ratingView	= (RatingBar)view.findViewById(R.id.rating);
		else
			commentView	= (TextView)view.findViewById(R.id.comment);			
	}

	public RatingBar getRatingView() {
		return ratingView;
	}

	public TextView getCommentView() {
		return commentView;
	}

	public TextView getLabelView() {
		return labelView;
	}
}