package es.leafsoft.cineol.activities;

import com.flurry.android.FlurryAgent;

import es.leafsoft.cineol.CINeolFacade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import es.leafsoft.cineol.R;

public class AboutActivity extends Activity implements OnClickListener {
	
	protected ViewGroup backgroundView;
	
	protected ImageView webLink1;
	protected ImageView webLink2;
	protected ImageView webLink3;

	protected TextView creditTitle1;
	protected TextView creditTitle2;
	protected TextView creditTitle3;
	protected TextView creditTitle4;
	
	protected TextView creditSubtitle1;
	protected TextView creditSubtitle2;
	protected TextView creditSubtitle3;
	protected TextView creditSubtitle4;

	protected TextView copyrightLine1;
	protected TextView copyrightLine2;
	protected TextView copyrightLine3;
	
	public static void open(Context context) {
		Intent intent = new Intent(context, AboutActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
								  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(this.onRequestAboutLayout());
		
		this.onFindViews();
		this.onConfigureViews();
		this.onSetListeners();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		FlurryAgent.onStartSession(this, CINeolFacade.API_Key_Flurry);
		FlurryAgent.onEvent(CINeolFacade.FlurryEventShowAbout, null);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		FlurryAgent.onEndSession(this);
	}

	protected void onSetListeners() {
		this.backgroundView.setOnClickListener(this);
		this.webLink1.setOnClickListener(this);
		this.webLink2.setOnClickListener(this);
		this.webLink3.setOnClickListener(this);
	}

	protected void onConfigureViews() {
		this.backgroundView.setBackgroundResource(this.onRequestAboutBackground());
				
		this.creditTitle1.setText(this.onRequestCreditTitle(1));
		this.creditTitle2.setText(this.onRequestCreditTitle(2));
		this.creditTitle3.setText(this.onRequestCreditTitle(3));
		this.creditTitle4.setText(this.onRequestCreditTitle(4));

		this.creditSubtitle1.setText(this.onRequestCreditSubtitle(1));
		this.creditSubtitle2.setText(this.onRequestCreditSubtitle(2));
		this.creditSubtitle3.setText(this.onRequestCreditSubtitle(3));
		this.creditSubtitle4.setText(this.onRequestCreditSubtitle(4));
		
		this.copyrightLine1.setText(this.onRequestCopyrightLine1());
		this.copyrightLine2.setText(this.onRequestCopyrightLine2());
		this.copyrightLine3.setText(this.onRequestCopyrightLine3());
	}

	protected void onFindViews() {
		this.backgroundView = (ViewGroup) this.findViewById(R.id.background);
		
		this.webLink1 = (ImageView) this.findViewById(R.id.my_web_link);
		this.webLink2 = (ImageView) this.findViewById(R.id.web_link_2);
		this.webLink3 = (ImageView) this.findViewById(R.id.web_link_3);
		
		this.creditTitle1 = (TextView) this.findViewById(R.id.credit_title_1);
		this.creditTitle2 = (TextView) this.findViewById(R.id.credit_title_2);
		this.creditTitle3 = (TextView) this.findViewById(R.id.credit_title_3);
		this.creditTitle4 = (TextView) this.findViewById(R.id.credit_title_4);

		this.creditSubtitle1 = (TextView) this.findViewById(R.id.credit_subtitle_1);
		this.creditSubtitle2 = (TextView) this.findViewById(R.id.credit_subtitle_2);
		this.creditSubtitle3 = (TextView) this.findViewById(R.id.credit_subtitle_3);
		this.creditSubtitle4 = (TextView) this.findViewById(R.id.credit_subtitle_4);
		
		this.copyrightLine1 = (TextView) this.findViewById(R.id.copyright_line_1);
		this.copyrightLine2 = (TextView) this.findViewById(R.id.copyright_line_2);
		this.copyrightLine3 = (TextView) this.findViewById(R.id.copyright_line_3);
	}

	public void onClick(View view) {
		
		if (view == this.backgroundView)
			this.finish();
		else if (view == this.webLink1)
			this.onOpenWebLink(this.webLink1);
		else if (view == this.webLink2)
			this.onOpenWebLink(this.webLink2);
		else if (view == this.webLink3)
			this.onOpenWebLink(this.webLink3);
	}
	
	
	/*
	 * METHODS FOR OVERWRITE.
	 **************************/
	
	protected void onOpenWebLink(ImageView webLink) {
		
		String link = null;
		if (this.webLink1 == webLink) {
			FlurryAgent.onEvent(CINeolFacade.FlurryEventClickLeafsoftLink, null);
			link = this.getResources().getString(R.string.leafsoft_web_link);
		}
		else if (this.webLink2 == webLink) {
			FlurryAgent.onEvent(CINeolFacade.FlurryEventClickCINeolLink, null);
			link = this.getResources().getString(R.string.cineol_web_link);
		}
		else if (this.webLink3 == webLink) {
			FlurryAgent.onEvent(CINeolFacade.FlurryEventClickAndroidIconsLink, null);
			link = this.getResources().getString(R.string.androidicons_web_link);
		}
		
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));   
		this.startActivity(intent);
	}

	protected int onRequestAboutLayout() {
		return R.layout.about_preference;
	}
	
	protected int onRequestAboutBackground() {
		return R.drawable.about_background;
	}
	
	protected int onRequestCreditTitle(int line) {
		
		switch (line) {
			case 1:
				return R.string.developed_by;
	
			case 2:
				return R.string.main_icon_designed_by;
				
			case 3:
				return R.string.banner_designed_by;
				
			case 4:
				return R.string.thanks_to;
				
			default:
				return 0;
		}
	}
	
	protected String onRequestCreditSubtitle(int line) {
		switch (line) {
			case 1:
				return "David Arias Vázquez";

			case 2:
				return "Günther Beyer";
				
			case 3:
				return "Efrén Arias Vázquez";
				
			case 4:
				return "David Prieto Ucha\nSergio Cía García";
				
			default:
				return "";
		}
	}
	
	protected int onRequestCopyrightLine1() {
		return R.string.copyright_line_1;
	}
	
	protected int onRequestCopyrightLine2() {
		return R.string.copyright_line_2;
	}
	
	protected int onRequestCopyrightLine3() {
		return R.string.copyright_line_3;
	}
}