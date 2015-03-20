package com.example.vsharesdk_demo;

import com.temobi.sx.sdk.vshare.player.VideoPlayer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;

public class VideoPlayActivity extends Activity {

	String videoId = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_play);
		
		videoId = getIntent().getStringExtra("VideoId");
		createVideoPlayer();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		videoId = intent.getStringExtra("VideoId");
		createVideoPlayer();
	}
	
	private void createVideoPlayer() {
		ViewGroup parent = (ViewGroup)findViewById(R.id.parent);
		parent.removeAllViews();
		
		VideoPlayer player = new VideoPlayer(this, videoId, true);
		parent.addView(player, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
