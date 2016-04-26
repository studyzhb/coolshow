package com.coolshow.app;

import com.coolshow.app.utils.MyActivityCollector;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d("BaseActivity", getClass().getSimpleName());
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		MyActivityCollector.addActivity(this);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyActivityCollector.removeActivity(this);
	}
	
}
