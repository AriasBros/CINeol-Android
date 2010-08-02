package es.cineol.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import es.leafsoft.cineol.R;

public class CommentDialog {

	public static interface CommentDialogDelegate {
		public void commentDialogDidClickOnPositiveButton(CommentDialog dialog, String comment);
	}
	
	private Context context;
	private String	dialogTitle 			= "Publish comment";
	private String	positiveTitleButton 	= "Ok";
	private String	negativeTitleButton 	= "Cancel";
	private String  comment					= "";
	private CommentDialogDelegate delegate 	= null;
	
	public CommentDialog(Context context) {
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
	
	public void setDelegate(CommentDialogDelegate delegate) {
		this.delegate = delegate;
	}
	
	public void show() {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.publish_comment_dialog, null);

		final TextView comment = ((TextView)layout.findViewById(R.id.comment));
		comment.setText(this.comment);
		
		builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.dialog_icon_star);
		builder.setTitle(dialogTitle);
		builder.setView(layout);
		builder.setNegativeButton(negativeTitleButton, null);
		builder.setPositiveButton(positiveTitleButton, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   if (delegate != null)
	        		   delegate.commentDialogDidClickOnPositiveButton(CommentDialog.this, comment.getText().toString());
	        	   dialog.dismiss();
	           }});
		
		alertDialog = builder.create();		
		alertDialog.show();
	}
}
