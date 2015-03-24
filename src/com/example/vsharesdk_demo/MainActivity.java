package com.example.vsharesdk_demo;

import org.apache.commons.lang.StringUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.temobi.sx.sdk.vshare.SDKInit;
import com.temobi.sx.sdk.vshare.player.VideoPlayer;
import com.temobi.sx.sdk.vshare.utils.PrefUtils;
import com.temobi.sx.sdk.vshare.widget.VideoSupportView;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int ACTIVITY_CODE_TO_RECORDER = 0x1234;
	private static final int source_id = 1;

	ProgressDialog loginDlg = null;
	RequestQueue requestQueue;
	String userId = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestQueue = Volley.newRequestQueue(this);

		setContentView(R.layout.activity_main);
		
		
		findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				doLogin();
			}
		});

		findViewById(R.id.recorder).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				double locLon = 0d;
				double locLat = 0d;
				
				Intent intent = new Intent(MainActivity.this, com.temobi.sx.sdk.vshare.recorder.RecorderActivity.class);
				intent.putExtra("locAddr", "山西省太原市");
				intent.putExtra("locLon", locLon); // double 类型
				intent.putExtra("locLat", locLat); // double 类型
//				intent.putExtra("TopicId", "test"); //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 主题ID
				
				startActivityForResult(intent, ACTIVITY_CODE_TO_RECORDER);
			}
		});
		
		findViewById(R.id.topic_video).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, VideoListActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private synchronized void doLogin() {
		
		findViewById(R.id.login).setEnabled(false);
		
		EditText edt_mobile = (EditText)findViewById(R.id.login_mobile);
		EditText edt_code = (EditText)findViewById(R.id.login_code);

		// 自动登录
		String savedUserId = PrefUtils.getUserId(this);
		if (edt_mobile.getText().length() == 0 && !StringUtils.isEmpty(savedUserId)) {
			MainActivity.this.onLoginSucceed(savedUserId);
			return;
		}
		

		loginDlg = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
		loginDlg.setMessage("正在启动...");
		loginDlg.show();
		
		SDKInit.login(getApplication(), edt_mobile.getText().toString(), edt_code.getText().toString(), source_id, new SDKInit.SDKInitCallback() {
			
			@Override
			public void onLoginSucceed(String userId) {
				MainActivity.this.onLoginSucceed(userId);
			}
			
			@Override
			public void onLoginError() {
				findViewById(R.id.login).setEnabled(true);
				loginDlg.dismiss();
				Toast.makeText(MainActivity.this, "启动失败，请检查网络设置后重试", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSessionInvalid() {
			}

			@Override
			public void onSessionValid(String arg0) {
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
				
				VideoSupportView videoSupport = new VideoSupportView(this, requestQueue);
				((ViewGroup)findViewById(R.id.video_support)).addView(videoSupport, ViewGroup.LayoutParams.MATCH_PARENT, 
						ViewGroup.LayoutParams.MATCH_PARENT);
				
				videoSupport.load(videoId, MainActivity.this.userId);

				
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

	private void onLoginSucceed(String userId) {
		MainActivity.this.userId = userId;
		findViewById(R.id.login).setVisibility(View.GONE);
		findViewById(R.id.work).setVisibility(View.VISIBLE);
		if (loginDlg != null)
			loginDlg.dismiss();
		
		Log.i("LOGIN", String.format("UserKey:%s", PrefUtils.getUserKey(MainActivity.this)));
		Log.i("LOGIN", String.format("UserId:%s", PrefUtils.getUserId(MainActivity.this)));
	}

}
