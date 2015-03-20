package com.example.vsharesdk_demo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.temobi.sx.sdk.vshare.widget.TopicVideoListView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.RelativeLayout.LayoutParams;

public class TopicVideoActivity extends Activity {

	RequestQueue requestQueue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_video);
		requestQueue = Volley.newRequestQueue(this);

		TopicVideoListView view = new TopicVideoListView(this, requestQueue);
		view.load("46df2f30c2d747ec80b833366fae3032");
		addContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.topic_video, menu);
		return true;
	}

}
