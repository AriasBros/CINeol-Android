package es.leafsoft.gallery;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.BaseSavedState;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;
import es.leafsoft.cineol.R;

public class GalleryView extends FrameLayout implements OnClickListener {
	
	private static final String TAG = "GalleryView";
	private static int[] BAR_COLOR = {0x77303030, 0x77000000};

	private Context context;
	private DisplayMetrics metrics = null;

	private GalleryScrollView 	galleryScrollView = null;
	private View 				titlebar = null;
	private FrameLayout 		toolbar = null;
	private ImageButton 		nextImageButton = null;
	private ImageButton 		previousImageButton = null;
	private TextView 			counterTextView = null;

	
	private String galleryTitle = "Gallery";
	
	private int numberOfPhotos;
	private boolean titlebarVisible;
	private boolean toolbarVisible;

	
	public GalleryView(Context context, DisplayMetrics metrics, String title, int initialPhoto, int numberOfPhotos, GalleryViewDataSource dataSource) {
		super(context);
		this.context = context;
		this.metrics = metrics;
		this.numberOfPhotos = numberOfPhotos;
		this.galleryTitle = title;
		
		this.initTitleBar();
		this.initToolBar();
		
		galleryScrollView = new GalleryScrollView(context, metrics, initialPhoto, numberOfPhotos, dataSource);
		galleryScrollView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		galleryScrollView.setVerticalFadingEdgeEnabled(false);
		galleryScrollView.setHorizontalFadingEdgeEnabled(false);
		galleryScrollView.setHorizontalScrollBarEnabled(false);
		
		this.addView(galleryScrollView);
		this.addView(titlebar);
		this.addView(toolbar);
		
		this.galleryScrollView.scrollToPhoto(initialPhoto, true);
	}
	
	public String getGalleryTitle() {
		return galleryTitle;
	}
	
	public int getNumberOfPhotos() {
		return this.numberOfPhotos;
	}

	public void onClick(View v) {
		if (v == this.nextImageButton)
			this.galleryScrollView.scrollToPhoto(this.galleryScrollView.indexCurrentPhoto() + 1, true);
		
		else if (v == this.previousImageButton)
			this.galleryScrollView.scrollToPhoto(this.galleryScrollView.indexCurrentPhoto() - 1, true);
	}
	
	private void initTitleBar() {
		titlebar = new TextView(context);
		titlebar.setLayoutParams(new LayoutParams(metrics.widthPixels, 40, Gravity.TOP));				
		titlebar.setBackgroundDrawable(new GradientDrawable(Orientation.BOTTOM_TOP, BAR_COLOR));
		((TextView) titlebar).setText(this.galleryTitle);
		((TextView) titlebar).setTextSize(16);
		((TextView) titlebar).setTextColor(0xFFFFFFFF);
		((TextView) titlebar).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		((TextView) titlebar).setGravity(Gravity.CENTER);
	}
	
	private void initToolBar() {
		toolbar = new FrameLayout(context);
		toolbar.setLayoutParams(new LayoutParams(metrics.widthPixels, 40, Gravity.BOTTOM));				
		toolbar.setBackgroundDrawable(new GradientDrawable(Orientation.TOP_BOTTOM, BAR_COLOR));
				
		nextImageButton = new ImageButton(context);
		nextImageButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.RIGHT));
		nextImageButton.setImageResource(R.drawable.toolbar_icon_right);
		nextImageButton.setBackgroundColor(0x00000000);
		nextImageButton.setOnClickListener(this);

		previousImageButton = new ImageButton(context);
		previousImageButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.LEFT));
		previousImageButton.setImageResource(R.drawable.toolbar_icon_left);
		previousImageButton.setBackgroundColor(0x00000000);
		previousImageButton.setOnClickListener(this);

		counterTextView = new TextView(context);
		counterTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		counterTextView.setText("000 / 000");
		counterTextView.setTextSize(16);
		counterTextView.setTextColor(0xFFFFFFFF);
		counterTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		counterTextView.setGravity(Gravity.CENTER);
		
		toolbar.addView(previousImageButton);
		toolbar.addView(counterTextView);
		toolbar.addView(nextImageButton);
	}
	
	public void imageAtIndexDidLoad(Drawable image, int index) {
		this.galleryScrollView.imageAtIndexDidLoad(image, index);
	}

	
	// TODO
	public void showToolbar(boolean show) {
		if (show)
			toolbar.setVisibility(VISIBLE);
		else			
			toolbar.setVisibility(INVISIBLE);
	}

	//TODO
	public void showTitlebar(boolean show) {
		if (show)
			titlebar.setVisibility(VISIBLE);
		else			
			titlebar.setVisibility(INVISIBLE);	}
	
	//TODO
	public void showHUD(boolean show) {
		this.showToolbar(show);
		this.showTitlebar(show);
	}
	
	public boolean isHUDVisible() {
		return (toolbar.getVisibility() == VISIBLE && titlebar.getVisibility() == VISIBLE);
	}
	
	protected void willChangePhoto() {
		
	}
	
	protected void didChangePhoto() {
		int currentPhoto = this.galleryScrollView.indexCurrentPhoto();
		
		// Cambiamos el contador.
		String text = currentPhoto + 1 + " / " + this.numberOfPhotos;
		this.counterTextView.setText(text);
		
		// Comprobamos si estamos al principio o al final de la galeria.
		// Si es asi deshabilitamos el boton que corresponda.
		if (currentPhoto == 0)
			this.previousImageButton.setVisibility(INVISIBLE);
		else
			this.previousImageButton.setVisibility(VISIBLE);
			
		if (currentPhoto == this.numberOfPhotos - 1)
			this.nextImageButton.setVisibility(INVISIBLE);
		else
			this.nextImageButton.setVisibility(VISIBLE);			
	}
}

class GalleryScrollView extends HorizontalScrollView {
	
	public static class SavedState extends BaseSavedState {
		
		int currentPage;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentPage);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private SavedState(Parcel in) {
            super(in);
            currentPage = in.readInt();
        }
    }
	
	private static final String TAG = "GalleryScrollView";
	
	private GalleryViewDataSource dataSource;
	
	private int widthOfPage;
	private int numberOfPhotos;
	private int initialPosX;
	private int previousPosX;
	private int finalPosX;
	private int actualPhoto = -1;
	private boolean moved;
	private boolean canShowCounter = false;
	private boolean saveState = true;
	
	private Context context;
	private DisplayMetrics metrics = null;
	private String loadingMessage = null;
	private LinearLayout content;
	private Toast counter;
	
	private ImageView imageViewLeft;
	private ImageView imageViewCenter;
	private ImageView imageViewRight;

	public GalleryScrollView(Context context, DisplayMetrics metrics, int initialPhoto, int numberOfPhotos, GalleryViewDataSource dataSource) {
		super(context);
		
		this.context = context;
		this.widthOfPage = metrics.widthPixels;
		this.numberOfPhotos = numberOfPhotos;
		this.dataSource = dataSource;
		this.metrics = metrics;
		
		content = new LinearLayout(context);
		content.setLayoutParams(new LayoutParams(metrics.widthPixels * numberOfPhotos, LayoutParams.FILL_PARENT));
			
		for (int i = 0; i < numberOfPhotos; i++)
            content.addView(makeLoadingView(), i);
		
		imageViewLeft   = this.makeImageView();
		imageViewCenter = this.makeImageView();
		imageViewRight  = this.makeImageView();
		
		this.addView(content);
	}
	
	
	private ImageView makeImageView() {
		ImageView imageView = new ImageView(context);
		imageView.setAdjustViewBounds(false);
		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageView.setLayoutParams(new LayoutParams(metrics.widthPixels, LayoutParams.FILL_PARENT));
		imageView.setPadding(4, 0, 4, 0);            
		
		return imageView;
	}

	
	private View makeLoadingView() {
		LinearLayout view = new LinearLayout(context);
		view.setFocusable(false);
		view.setOrientation(LinearLayout.VERTICAL);
		view.setGravity(Gravity.CENTER);
		view.setLayoutParams(new LayoutParams(metrics.widthPixels, LayoutParams.FILL_PARENT));
				
		ProgressBar spinner = new ProgressBar(context);
		view.addView(spinner, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		if (this.loadingMessage != null) {
			TextView label = new TextView(context);
			label.setText(this.loadingMessage);
			label.setTextSize(16);
			label.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			label.setPadding(0, 8, 0, 0);
			view.addView(label, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
		
		return view;
	}
	
	public int getNumberOfPhotos() {
		return this.numberOfPhotos;
	}
	
	public int indexCurrentPhoto() {
		return this.actualPhoto;
	}
	
	public void scrollToPhoto(int indexOfPhoto, boolean animated) {
		if (indexOfPhoto < 0)
			indexOfPhoto = 0;
		else if (indexOfPhoto >= numberOfPhotos)
			indexOfPhoto = numberOfPhotos - 1;
		
		if (this.actualPhoto == indexOfPhoto)
			return;
		
		this.willChangeImage();
		
		this.actualPhoto = indexOfPhoto;
		this.requestImage(actualPhoto - 1);
		this.requestImage(actualPhoto);
		this.requestImage(actualPhoto + 1);

		if (!animated)
			this.scrollTo(this.actualPhoto * widthOfPage, 0);
		else
			this.smoothScrollTo(this.actualPhoto * widthOfPage, 0);
		
		this.didChangeImage();
	}

	// TODO
	protected void willChangeImage() {
		if (this.counter != null)
			this.counter.cancel();
	}
	
	// TODO
	protected void didChangeImage() {
		if (this.canShowCounter) {			
			CharSequence text = this.actualPhoto + " / " + this.numberOfPhotos;
			int duration = Toast.LENGTH_SHORT;

			counter = Toast.makeText(context, text, duration);
			counter.show();
		}
		
		((GalleryView)this.getParent()).didChangePhoto();
	}
	
	public void imageAtIndexDidLoad(Drawable image, int index) {
		if (index < 0 || index >= numberOfPhotos)
			return;
		
		this.content.removeViewAt(index);
		this.insertImageView(image, index);
	}
	
	
	private void insertImageView(Drawable image, int index) {
		if (index < 0 || index >= numberOfPhotos)
			return;
		
		if (index == this.actualPhoto - 1) {
			int i = this.content.indexOfChild(imageViewLeft);
			if (i > -1) {
				this.content.removeView(imageViewLeft);
				this.content.addView(this.makeLoadingView(), i);
			}
			
			imageViewLeft.setImageDrawable(image);
			this.content.addView(imageViewLeft, index);
		}
		else if (index == this.actualPhoto) {
			int i = this.content.indexOfChild(imageViewCenter);
			if (i > -1) {
				this.content.removeView(imageViewCenter);
				this.content.addView(this.makeLoadingView(), i);
			}
			
			imageViewCenter.setImageDrawable(image);
			this.content.addView(imageViewCenter, index);
		}
		else if (index == this.actualPhoto + 1) {
			int i = this.content.indexOfChild(imageViewRight);
			if (i > -1) {
				this.content.removeView(imageViewRight);
				this.content.addView(this.makeLoadingView(), i);
			}
			
			imageViewRight.setImageDrawable(image);
			this.content.addView(imageViewRight, index);
		}
	}
	
	private void requestImage(int image) {
		if (image < 0 || image >= numberOfPhotos)
			return;
		
		if (dataSource != null) {
			Drawable photo = dataSource.photoAtIndex(image);
			
			if (photo == null)
				dataSource.loadPhotoAtIndex(image);
			else {
				this.content.removeViewAt(image);
				this.insertImageView(photo, image);
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_DPAD_CENTER:
				return true;
		}
		
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_UP:
				this.scrollToPhoto(actualPhoto - 1, true);
				return true;
				
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_DPAD_DOWN:
				this.scrollToPhoto(actualPhoto + 1, true);
				return true;
				
			// TODO
			case KeyEvent.KEYCODE_DPAD_CENTER:
				GalleryView view = (GalleryView)this.getParent();
				view.showHUD(!view.isHUDVisible());
				return true;
		}
		
		return false;
	}


	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				this.initialPosX = (int)ev.getX();
				this.previousPosX = this.initialPosX;
				break;
				
			case MotionEvent.ACTION_MOVE:
				int offset = this.previousPosX - (int)ev.getX();
				if (Math.abs(offset) < 10)
					return true;
				
				this.scrollTo(this.getScrollX() + offset, 0);
				this.moved = true;
				this.previousPosX = (int)ev.getX();
				break;
				
			case MotionEvent.ACTION_UP:
				this.finalPosX = (int)ev.getX();
				if (moved) {			
					moved = false;
							
					if (this.initialPosX > this.finalPosX)
						this.scrollToPhoto(actualPhoto + 1, true);
					else if (this.initialPosX < this.finalPosX)
						this.scrollToPhoto(actualPhoto - 1, true);
				}
				else {
					GalleryView view = (GalleryView)this.getParent();
					view.showHUD(!view.isHUDVisible());
				}
				break;
		}
		
		return true;
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable parcelable) {		
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        
        SavedState state = (SavedState)parcelable;
		super.onRestoreInstanceState(state.getSuperState());
		
		this.scrollToPhoto(state.currentPage, true);
	}

	@Override
	protected Parcelable onSaveInstanceState() {		
		Parcelable parcelable = super.onSaveInstanceState();
		
		if (saveState) {
			SavedState state = new SavedState(parcelable);
			state.currentPage = this.actualPhoto;
			
			return state;
		}
		
		return parcelable;
	}
}