package es.leafsoft.cineol.activities;

import com.flurry.android.FlurryAgent;

import es.leafsoft.cineol.CINeolFacade;

import es.leafsoft.cineol.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayerActivity extends Activity {

	static private final String TAG = "VideoPlayerActivity";
    static private String url = null;
	
    private VideoView videoView;
    
    static public void play(Context context, String url) {
    	VideoPlayerActivity.url = url;
    	    	
        final Intent intent = new Intent(context, VideoPlayerActivity.class);
        context.startActivity(intent);        
    }
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.video_player_view);
        this.videoView = (VideoView)this.findViewById(R.id.VideoPlayerView);
        
        if (url != null) {
	        videoView.setVideoURI(Uri.parse(url));
	        videoView.setMediaController(new MediaController(this));
	        videoView.requestFocus();
        }
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
