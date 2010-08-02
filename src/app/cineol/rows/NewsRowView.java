package app.cineol.rows;

import es.leafsoft.cineol.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsRowView {

	private TextView 	titleView;
	private TextView 	introductionView;
	private ImageView 	thumbView;
	
	public NewsRowView (View view) {
		titleView = (TextView)view.findViewById(R.id.title);
		introductionView = (TextView)view.findViewById(R.id.intro);
		thumbView = (ImageView)view.findViewById(R.id.thumb);
	}
	
	public TextView getTitleView() {
		return titleView;
	}

	public TextView getIntroductionView() {
		return introductionView;
	}

	public ImageView getThumbView() {
		return thumbView;
	}
}
