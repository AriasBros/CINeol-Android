package app.cineol.rows;

import android.view.View;
import android.widget.TextView;
import es.leafsoft.cineol.R;

public class CreditRowView {

	private TextView 	nameView;
	private TextView 	jobView;
	
	public CreditRowView (View view) {
		nameView	= (TextView)view.findViewById(R.id.name);
		jobView	= (TextView)view.findViewById(R.id.job);
	}

	public TextView getNameView() {
		return nameView;
	}

	public TextView getJobView() {
		return jobView;
	}
}