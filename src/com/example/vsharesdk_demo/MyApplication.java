package com.example.vsharesdk_demo;

import android.app.Application;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler.getInstance().init(getApplicationContext());
	}

}
