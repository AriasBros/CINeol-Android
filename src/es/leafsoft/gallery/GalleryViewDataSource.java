package es.leafsoft.gallery;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public interface GalleryViewDataSource {
	public Drawable photoAtIndex(int index);
	public void loadPhotoAtIndex(int index);
	public String urlForPhotoAtIndex(int index);
}
