package app.cineol.adapters;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import app.cineol.rows.MyCINeolRowView;


public class MyCINeolAdapter extends BaseAdapter {

	private static final String TAG = "MyCINeolAdapter";
	
	private static final String RATING_LABEL_1 	= "¡Puntúala!";
	private static final String RATING_LABEL_2 	= "Mi puntuación";
	private static final String COMMENT_LABEL_1 = "Publicar un comentario";	
	private static final String COMMENT_LABEL_2 = "Mi comentario";	
	
	private static int RATING_VIEW_TYPE 	= 0;
	private static int COMMENT_VIEW_TYPE 	= 1;
	
	private Context context = null;
	private int XMLResourceRating;
	private int XMLResourceComment;
	
	private float rating = -1;
	private String comment = null;
	
	
	public MyCINeolAdapter(Context context, int resourceRating, int resourceComment) {
		super();
		
		this.context = context;
		XMLResourceRating = resourceRating;
		XMLResourceComment = resourceComment;
	}
	
	public void setRating(float rating) {
		this.rating = rating;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public int getCount() {
		if (rating <= -1)
			return 1;
		
		return 2;
	}

	public Object getItem(int index) {
		if (index == 0)
			return rating;
		
		return comment;
	}

	public long getItemId(int index) {
		return index;
	} 

	public View getView(int index, View convertView, ViewGroup parent) {
		
		MyCINeolRowView rowView;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			if (index == 0)
	            convertView = inflater.inflate(XMLResourceRating, null);
			else
	            convertView = inflater.inflate(XMLResourceComment, null);
			
			rowView = new MyCINeolRowView(convertView, index);
			convertView.setTag(rowView);
		}
		else
			rowView = (MyCINeolRowView) convertView.getTag();
		
		if (index == 0) {
			rowView.getRatingView().setRating(rating);
			if (rating <= -1)
				rowView.getLabelView().setText(RATING_LABEL_1);
			else
				rowView.getLabelView().setText(RATING_LABEL_2);				
		}
		else {
			if (comment == null) {
				rowView.getLabelView().setText(COMMENT_LABEL_1);
				rowView.getCommentView().setVisibility(View.GONE);				
			}
			else {
				rowView.getLabelView().setText(COMMENT_LABEL_2);		
				rowView.getCommentView().setVisibility(View.VISIBLE);				
				rowView.getCommentView().setText(comment);				
			}
		}
		
		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0)
			return RATING_VIEW_TYPE;
		else
			return COMMENT_VIEW_TYPE;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}	
}
