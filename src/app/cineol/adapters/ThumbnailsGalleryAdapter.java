package app.cineol.adapters;

import java.util.List;

import es.leafsoft.cineol.DARelativeLayout;
import es.leafsoft.cineol.DARelativeLayoutDelegate;
import es.leafsoft.utils.MetricsUtils;

import net.cineol.model.Image;
import net.cineol.model.News;
import net.cineol.model.Post;
import net.cineol.utils.CINeolUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import es.leafsoft.cineol.R;
import app.cineol.rows.CommentRowView;
import app.cineol.rows.NewsRowView;

public class ThumbnailsGalleryAdapter extends ArrayAdapter<Drawable> {
    private static final int IMAGE_VIEW_ID = 1;
	private int galleryItemBackground;
    

	public ThumbnailsGalleryAdapter(Context context, List<Drawable> images) {
		super(context, 0, images);
		
        TypedArray a = context.obtainStyledAttributes(R.styleable.Gallery);
        galleryItemBackground = a.getResourceId(
                R.styleable.Gallery_android_galleryItemBackground, 0);
        a.recycle();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = new LinearLayout(getContext());
			((LinearLayout) convertView).setGravity(Gravity.CENTER);
			
			int n = MetricsUtils.dipsToPixels(this.getContext(), 100);
			convertView.setLayoutParams(new Gallery.LayoutParams(n, n));
			
			convertView.setBackgroundResource(galleryItemBackground);
			
            ImageView i = new ImageView(getContext());
            i.setId(IMAGE_VIEW_ID);
            i.setBackgroundColor(0xFF000000);
            
            n = MetricsUtils.dipsToPixels(this.getContext(), 70);
            i.setLayoutParams(new LinearLayout.LayoutParams(n, n));
            
            i.setAdjustViewBounds(false);
            i.setScaleType(ImageView.ScaleType.CENTER_CROP);
            
            ((LinearLayout) convertView).addView(i);
            convertView.setTag(i);
		}
		
		ImageView i = (ImageView) convertView.getTag();
		i.setImageDrawable(this.getItem(position));
		
        return convertView;
	}
}