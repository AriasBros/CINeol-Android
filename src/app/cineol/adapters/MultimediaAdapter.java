package app.cineol.adapters;

import java.util.ArrayList;

import es.leafsoft.utils.MetricsUtils;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class MultimediaAdapter extends BaseAdapter {

	private static String TAG = "MultimediaAdapter";
	private static int GALLERY_VIEW_TYPE = 0;
	private static int VIDEO_VIEW_TYPE 	 = 1;
	
	private Context context = null;
	
	private ArrayList<String> videos = new ArrayList<String>();
	private ThumbnailsGalleryAdapter gallery = null;
	private OnItemClickListener listener;
	
	private boolean hasGallery = false;
	private int		resourceVideos;
	private int		resourceGallery;
	
	public MultimediaAdapter (Context context, int resourceGallery, int resourceVideos, OnItemClickListener listener) {
		this.context = context;
		this.resourceVideos = resourceVideos;
		this.resourceGallery = resourceGallery;
		this.listener = listener;
	}
	
	public void addVideo(String title) {
		videos.add(title);
	}
	
	public void addGallery(ThumbnailsGalleryAdapter adapter) {
		if (adapter == null)
			return;
		
		this.gallery = adapter;
		this.hasGallery = true;
	}
	
	
	public int getCount() {
		int count = this.videos.size();

		if (hasGallery)
			 count++;

		return count;
	}

	public Object getItem(int index) {
		if (index == 0 && hasGallery)
			return gallery;
		else if (index > 0 && hasGallery)
			return videos.get(index - 1);
		else
			return videos.get(index);		
	}

	public long getItemId(int index) {
		return index;
	} 

	public View getView(int index, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();

			if (!hasGallery || (hasGallery && index > 0)) {
	            convertView = inflater.inflate(resourceVideos, null);
			}
			else if (hasGallery && index == 0) {
	            convertView = inflater.inflate(resourceGallery, null);
	            ((Gallery) convertView).setOnItemClickListener(this.listener);
			}
		}

		if (index == 0 && hasGallery)
			((Gallery) convertView).setAdapter(this.gallery);
		else if (index > 0 && hasGallery)
			((TextView) convertView).setText(videos.get(index - 1));
		else
			((TextView) convertView).setText(videos.get(index));

		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0 && hasGallery)
			return GALLERY_VIEW_TYPE;
		else
			return VIDEO_VIEW_TYPE;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}	
}
