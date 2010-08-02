package app.cineol.adapters;

import java.util.HashMap;
import java.util.List;

import com.flurry.android.FlurryAgent;

import es.leafsoft.cineol.CINeolFacade;
import es.leafsoft.cineol.DARelativeLayout;
import es.leafsoft.cineol.DARelativeLayoutDelegate;

import net.cineol.model.Post;
import net.cineol.utils.CINeolUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import es.leafsoft.cineol.R;
import app.cineol.rows.CommentRowView;

public class CommentsAdapter extends ArrayAdapter<Post> implements DARelativeLayoutDelegate {
	
	static private String rowCSS = "<style type=\"text/css\">body { " +
			"color: #888888; " +
			"font-family: helvetica; " +
			"font-weight: normal; " +
			"font-size: 15px; " +
			"background: transparent; " +
			"}</style>";
	
	static private String dialogCSS = "<style type=\"text/css\">body { " +
			"color: #FFFFFF; " +
			"font-family: helvetica; " +
			"font-weight: normal; " +
			"font-size: 15px; " +
			"background: transparent; " +
			"}</style>";
	
	private int resource;

	public CommentsAdapter(Context context, int textViewResourceId, List<Post> posts) {
		super(context, textViewResourceId, posts);
		resource = textViewResourceId;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {

		CommentRowView rowView;
		
        if (convertView == null) {
    		Activity activity = (Activity) getContext();
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(resource, null);
            
            rowView = new CommentRowView(convertView);
            convertView.setTag(rowView);
        }
        else
        	rowView = (CommentRowView)convertView.getTag();
        
        Post post = this.getItem(position);
        if (post != null) {
        	rowView.getUserView().setText(post.getUser());                            
        	rowView.getDateView().setText(CINeolUtils.stringFromDate(post.getDate(), "dd MMM yyyy, HH:mm"));
        	
        	((DARelativeLayout) rowView.getUserView().getParent()).setIndex(position);
        	((DARelativeLayout) rowView.getUserView().getParent()).setDelegate(this);
        	
        	this.reloadMessageView(rowView.getMessageView(), post, rowCSS);
        }
        
        return convertView;
	}
	
	private void reloadMessageView(WebView view, Post post, String CSS) {		
		
		/*
		String html =
		"<script type=\"text/javascript\" src=\"./styles/cineol/template/styleswitcher.js\"></script>" +
		"<script type=\"text/javascript\" src=\"./styles/cineol/template/forum_fn.js\"></script>" +
		"<link href=\"/css/reset.css\" rel=\"stylesheet\" type=\"text/css\" />" +
		"<link href=\"/css/layout.css\" rel=\"stylesheet\" type=\"text/css\" />" +
		"<link href=\"/css/menu.css\" rel=\"stylesheet\" type=\"text/css\" />" +
		//"<link href=\"http://www.cineol.net/css/2columnas.css\" rel=\"stylesheet\" type=\"text/css\" />" + 
		  "<div class=\"inner\"><div class=\"postbody\"><div class=\"content\">" +
		  post.getMessage() + 
		  "</div></div></div>";
		*/
		
		String str = "";
		
		str += CSS + "<div>";
		str += post.getMessage();	
		str += "</div>";
		 
    	view.loadDataWithBaseURL("http://www.cineol.net/", str, "text/html", "utf-8", null);                
	}

	public void didTouchEvent(DARelativeLayout layout) {		
		
		FlurryAgent.onEvent(CINeolFacade.FlurryEventShowMovieComent, null);
		
		AlertDialog.Builder builder;
		AlertDialog alertDialog;

		Context mContext = getContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.comment_dialog, null);

		Post post = this.getItem(layout.getIndex());
		
		((TextView) view.findViewById(R.id.user)).setText(post.getUser());
		((TextView) view.findViewById(R.id.date)).setText(CINeolUtils.stringFromDate(post.getDate(), "dd MMM yyyy, HH:mm"));

		WebView webView = (WebView)view.findViewById(R.id.message);
		webView.setBackgroundColor(0x00000000);
		
		WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setSupportZoom(false);
        
    	this.reloadMessageView(webView, post, dialogCSS);

		builder = new AlertDialog.Builder(mContext);
		builder.setView(view);
		builder.setNeutralButton("Cerrar", null);
		alertDialog = builder.create();
		
		alertDialog.show();
	}
}
