package com.example.vsharesdk_demo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.temobi.sx.sdk.vshare.SDK;
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
	SDK sdkInstance = null;
	
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
				//intent.putExtra("TopicId", "20150401"); //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 主题ID
				
				startActivityForResult(intent, ACTIVITY_CODE_TO_RECORDER);
			}
		});
		
		// 打开视频列表
		findViewById(R.id.topic_video).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, VideoListActivity.class);
				startActivity(intent);
			}
		});
		
		
		findViewById(R.id.login).setEnabled(false);

		
		// 初始化SDK
		// 测试用APPID
		int channelId = 50000;
		String appId = "d05a9b1c41d44bae8c69c45c29deb1a9"; 
		String appPassword = "7d3857a563b64ec5b47443ca5215fc62";
		
		sdkInstance = new SDK(MainActivity.this, appId, appPassword, channelId) {
			@Override
			protected void onInvalidAppId() {
				Toast.makeText(MainActivity.this, "APPID无效", Toast.LENGTH_LONG).show();
			}
			@Override
			protected void onInvalidSession() {
				// 通过第三方验证接口获取验证码
				Toast.makeText(MainActivity.this, "会话无效，请登录", Toast.LENGTH_LONG).show();
				findViewById(R.id.login).setEnabled(true);
			}
			@Override
			protected void onSucceeded(String userId) {
				MainActivity.this.onLoginSucceed(userId);
			}
			@Override
			protected void onFaild() {
				Toast.makeText(MainActivity.this, "启动失败！", Toast.LENGTH_LONG).show();
			}
		};
	}
	
	private synchronized void doLogin() {
		
		findViewById(R.id.login).setEnabled(false);
		
		EditText edt_mobile = (EditText)findViewById(R.id.login_mobile);
		EditText edt_code = (EditText)findViewById(R.id.login_code);

		sdkInstance.login(edt_mobile.getText().toString(), edt_code.getText().toString());
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
