package es.cineol.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;
import es.leafsoft.cineol.R;

public class RatingDialog {

	public static interface RatingDialogDelegate {
		public void ratingDialogDidClickOnPositiveButton(RatingDialog dialog, float rating);
	}
	
	private Context context;
	private String	dialogTitle = "Rate it!";
	private String	label		= "";
	private String	positiveTitleButton = "Ok";
	private String	negativeTitleButton = "Cancel";
	private RatingDialogDelegate delegate = null;
	
	public RatingDialog(Context context) {
		this.context = context;
	}
	
	public void setTitle(String title) {
		this.dialogTitle = title;
	}
	
	public void setPositiveTitleButton(String title) {
		this.positiveTitleButton = title;
	}

	public void setNegativeTitleButton(String title) {
		this.negativeTitleButton = title;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setDelegate(RatingDialogDelegate delegate) {
		this.delegate = delegate;
	}
	
	public void show() {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.my_cineol_rating_dialog, null);

		final TextView number_rating = ((TextView)layout.findViewById(R.id.number_rating));
		number_rating.setText(label + "0");
		final RatingBar star_rating = ((RatingBar) layout.findViewById(R.id.star_rating));
		star_rating.setRating(0);
		
		OnRatingBarChangeListener onRatingBarChangeListener = new RatingBar.OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				int r = (int)(rating * 2);
				number_rating.setText(label + String.valueOf(r));
			}};
		
		OnTouchListener onTouchListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int r = (int)(star_rating.getRating() * 2);
				number_rating.setText(label + String.valueOf(r));
				
				return false;
			}};
		
		star_rating.setOnRatingBarChangeListener(onRatingBarChangeListener);
		star_rating.setOnTouchListener(onTouchListener);
		
		builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.dialog_icon_star);
		builder.setTitle(dialogTitle);
		builder.setView(layout);
		builder.setNegativeButton(negativeTitleButton, null);
		builder.setPositiveButton(positiveTitleButton, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   if (delegate != null)
	        		   delegate.ratingDialogDidClickOnPositiveButton(RatingDialog.this, star_rating.getRating());
	        	   dialog.dismiss();
	           }});
		
		alertDialog = builder.create();		
		alertDialog.show();
	}
}
