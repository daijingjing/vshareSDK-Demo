package com.example.vsharesdk_demo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.temobi.sx.sdk.vshare.activity.UserPortalActivity;
import com.temobi.sx.sdk.vshare.activity.VideoPlayActivity;
import com.temobi.sx.sdk.vshare.utils.PrefUtils;
import com.temobi.sx.sdk.vshare.widget.VideoListView;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class VideoListActivity extends Activity {

	RequestQueue requestQueue;
	VideoListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_video);
		requestQueue = Volley.newRequestQueue(this);

		listview = new VideoListView(this, requestQueue) {
			@Override
			protected void onPlayVideo(String videoId) {
				Intent intent = new Intent(VideoListActivity.this, VideoPlayActivity.class);
				intent.putExtra("VideoId", videoId);
				VideoListActivity.this.startActivity(intent);
			}

			@Override
			protected void onShareVideo(String videoId, String posterUrl, String shortUrl) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(shortUrl));
				startActivity(intent);
			}
			
			@Override
			protected void onClickMyAvatar() {
				Toast.makeText(getApplicationContext(), "onClickMyAvatar", Toast.LENGTH_SHORT).show();
				UserPortalActivity.start(getContext(), null);
			}

			@Override
			protected void onClickAvatar(String userId) {
				Toast.makeText(getApplicationContext(), "onClickAvatar", Toast.LENGTH_SHORT).show();
				UserPortalActivity.start(getContext(), userId);
			}
			
			@Override
			protected void onClickCommentUserAvatar(String userId) {
				Toast.makeText(getApplicationContext(), "onClickCommentUserAvatar", Toast.LENGTH_SHORT).show();
				UserPortalActivity.start(getContext(), userId);
			}

			@Override
			protected void onClickSupportUserAvatar(String userId) {
				Toast.makeText(getApplicationContext(), "onClickSupportUserAvatar", Toast.LENGTH_SHORT).show();
				UserPortalActivity.start(getContext(), userId);
			}
		};
//		listview.load("20150401");// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		listview.load(null);
		
		addContentView(listview, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.topic_video, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		if (item.getItemId() == R.id.action_reload) {
			listview.load(null);// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		}

		return super.onMenuItemSelected(featureId, item);
	}
}
