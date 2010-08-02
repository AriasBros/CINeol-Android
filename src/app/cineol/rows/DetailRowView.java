package app.cineol.rows;

import android.view.View;
import android.widget.TextView;
import es.leafsoft.cineol.R;

public class DetailRowView {

	private TextView 	detailView;
	private TextView 	infoView;
	
	public DetailRowView (View view) {
		detailView	= (TextView)view.findViewById(R.id.detail);
		infoView	= (TextView)view.findViewById(R.id.info);
	}

	public TextView getDetailView() {
		return detailView;
	}

	public TextView getInfoView() {
		return infoView;
	}
}