package es.leafsoft.cineol.activities;

import java.util.ArrayList;

import com.flurry.android.FlurryAgent;

import es.leafsoft.cineol.CINeolFacade;

import bit.ly.webservice.BitlyManager;
import bit.ly.webservice.BitlyManagerDelegateAndroid;

import net.cineol.database.CINeolDatabase;
import net.cineol.utils.CINeolUtils;
import net.cineol.webservice.CINeolManager;

import es.leafsoft.cineol.R;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
 
public class CineolActivity extends TabActivity {

	private static final String TAG = "CineolActivity";
	
	// TODO -- Put your API key.
	//private static final String kAPI_Key_CINeol = "YOUR_API_KEY_HERE";
	//private static final String kAPI_Key_Bitly = "YOUR_API_KEY_HERE";
	//private static final String kLogin_Bitly = "YOUR_LOGIN_HERE";
	
	private static final String kAPI_Key_CINeol = "euADFz";
	private static final String kAPI_Key_Bitly = "R_f54477226a082837fa84d7bba4615994";
	private static final String kLogin_Bitly = "davsurf";

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.main);
        
        // Añadimos al CINeol Manager nuestra API Key.
        CINeolManager.setAPIKey(kAPI_Key_CINeol);
        
        //Inicializamos la Facade.
        CINeolFacade.initFacade(this);
        
        // Inicializamos la base de datos.
        CINeolDatabase.initDatabase(this);
        
        
        BitlyManager.initBitlyManager(kAPI_Key_Bitly, kLogin_Bitly,
									  BitlyManager.VERSION_201, 
									  BitlyManager.RESPONSE_XML_FORMAT,
									  new BitlyManagerDelegateAndroid());
                
        TabSpec tabSpec;
        TabHost tabHost = this.getTabHost();

        /*
        Drawable icon_news 		= null;
        Drawable icon_premiers 	= null;
        Drawable icon_cards		= null;

        int systemVersion = Build.VERSION.SDK_INT;
        if (systemVersion <= Build.VERSION_CODES.DONUT) {
        	icon_news	 	= this.getResources().getDrawable(R.drawable.tab_icon_news_donut);
        	icon_premiers 	= this.getResources().getDrawable(R.drawable.tab_icon_premiers_donut);
        	icon_cards 		= this.getResources().getDrawable(R.drawable.tab_icon_cards_donut);
        }
        else {
        	icon_news 		= this.getResources().getDrawable(R.drawable.tab_icon_news_eclair);
        	icon_premiers 	= this.getResources().getDrawable(R.drawable.tab_icon_premiers_eclair);
        	icon_cards 		= this.getResources().getDrawable(R.drawable.tab_icon_cards_eclair);
        }
        */
        
        Drawable icon_news	 	= this.getResources().getDrawable(R.drawable.ic_tab_news);
        Drawable icon_premiers 	= this.getResources().getDrawable(R.drawable.ic_tab_premiers);
        Drawable icon_cards 	= this.getResources().getDrawable(R.drawable.ic_tab_cards);
         
        // Creamos la pestaña de la Portada...
        tabSpec = tabHost.newTabSpec("Noticias");
        
        tabSpec.setIndicator("Noticias", icon_news);
        tabSpec.setContent(new Intent(this, NewsListActivity.class));
        tabHost.addTab(tabSpec);
        
        // Creamos la pestaña de los estrenos...
        tabSpec = tabHost.newTabSpec("Cartelera");
        tabSpec.setIndicator("Cartelera", icon_premiers);
        tabSpec.setContent(new Intent(this, PremiersActivity.class));
        tabHost.addTab(tabSpec);
        
        // Creamos la pestaña de las fichas...
        tabSpec = tabHost.newTabSpec("Fichas");
        tabSpec.setIndicator("Fichas", icon_cards);
        tabSpec.setContent(new Intent(this, CardsActivity.class));
        tabHost.addTab(tabSpec);
    }

	@Override
	protected void onStart() {
		super.onStart();
		
		FlurryAgent.onStartSession(this, CINeolFacade.API_Key_Flurry);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		FlurryAgent.onEndSession(this);
	}
}