package app.cineol.adapters;

import java.util.List;

import net.cineol.model.News;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ArrayAdapter;
import es.leafsoft.cineol.R;
import app.cineol.rows.NewsRowView;

public class NewsAdapter extends ArrayAdapter<News> {
	
	private BitmapDrawable defaultImage;
	private int XMLResource;

	public NewsAdapter(Context context, int textViewResourceId, List<News> news) {
		super(context, textViewResourceId, news);
		XMLResource = textViewResourceId;
		defaultImage = (BitmapDrawable) context.getResources().getDrawable(R.drawable.news);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {

		NewsRowView rowView;
		
        if (convertView == null) {
    		Activity activity = (Activity) getContext();
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(XMLResource, null);
            
            rowView = new NewsRowView(convertView);
            convertView.setTag(rowView);
        }
        else
        	rowView = (NewsRowView)convertView.getTag();
        
        News news = this.getItem(position);
        if (news != null) { 

        	if (news.getImage() == null)
        		rowView.getThumbView().setImageDrawable(defaultImage);
        	else
        		rowView.getThumbView().setImageDrawable(news.getImage());
        	
        	if (news.getIntroduction() == null || news.getIntroduction().length() <= 0) {
	        	rowView.getTitleView().getLayoutParams().height = 85;
	        	rowView.getTitleView().setMaxLines(5);
	        	
        		rowView.getIntroductionView().setVisibility(View.GONE);
        	}
        	else {
	        	rowView.getTitleView().getLayoutParams().height = LayoutParams.WRAP_CONTENT;
	        	rowView.getTitleView().setMaxLines(2);

	        	rowView.getIntroductionView().setText(news.getIntroduction());
        		rowView.getIntroductionView().setVisibility(View.VISIBLE);
        	}
        	
        	rowView.getTitleView().setText(news.getTitle());    
        }
        
        return convertView;
	}
}
