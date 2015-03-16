package com.example.vsharesdk_demo;

import com.bumptech.glide.Glide;
import com.temobi.sx.sdk.vshare.player.VideoPlayer;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int ACTIVITY_CODE_TO_RECORDER = 0x1234;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.recorder).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				double locLon = 0d;
				double locLat = 0d;
				
				Intent intent = new Intent(MainActivity.this, com.temobi.sx.sdk.vshare.recorder.RecorderActivity.class);
				intent.putExtra("mobile", "13903510000");
				intent.putExtra("locAddr", "山西省太原市");
				intent.putExtra("locLon", locLon); // double 类型
				intent.putExtra("locLat", locLat); // double 类型
				
				startActivityForResult(intent, ACTIVITY_CODE_TO_RECORDER);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == ACTIVITY_CODE_TO_RECORDER) {
			switch(resultCode) {
			case RESULT_OK:
				
				final String videoId = data.getStringExtra("videoId");
				final String posterUrl = data.getStringExtra("posterURL");
				final String shareUrl = data.getStringExtra("shareURL"); 
				
				((TextView)findViewById(R.id.shortUrl)).setText(shareUrl);
				findViewById(R.id.shortUrl).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(((TextView)arg0).getText().toString()));
						startActivity(intent);
					}
				});
				
				RelativeLayout player = (RelativeLayout)findViewById(R.id.player);
				player.removeAllViews();
				
				ImageView poster = new ImageView(MainActivity.this);
				player.addView(poster, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

				Glide.with(MainActivity.this).load(posterUrl).centerCrop().into(poster);
				
				ImageView playBtn = new ImageView(MainActivity.this);
				playBtn.setImageResource(R.drawable.play);
				playBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						createPlayer(videoId);
					}
				});
				
				float density = Resources.getSystem().getDisplayMetrics().density;
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int)(80*density), (int)(80*density));
				lp.addRule(RelativeLayout.CENTER_IN_PARENT);
				player.addView(playBtn, lp);
				
				break;
			case RESULT_CANCELED:
				Toast.makeText(MainActivity.this, "取消录制视频", Toast.LENGTH_LONG).show();
				break;
			}
		}

	}
	
	private void createPlayer(String videoId) {
		RelativeLayout player = (RelativeLayout)findViewById(R.id.player);
		player.removeAllViews();
		VideoPlayer v = new VideoPlayer(MainActivity.this, videoId, true);
		player.addView(v, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

}
