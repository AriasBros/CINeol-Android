package app.cineol.rows;

import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import es.leafsoft.cineol.R;

public class CommentRowView {

	private TextView 	userView;
	private TextView 	dateView;
	private WebView		messageView;
	
	public CommentRowView (View view) {
		userView = (TextView)view.findViewById(R.id.user);
		dateView = (TextView)view.findViewById(R.id.date);
		messageView = (WebView)view.findViewById(R.id.message);
	}

	public TextView getUserView() {
		return userView;
	}

	public TextView getDateView() {
		return dateView;
	}

	public WebView getMessageView() {
		return messageView;
	}
}