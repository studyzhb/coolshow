package com.coolshow.app.utils;

import com.coolshow.app.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class TitleLayout extends LinearLayout {

	public TitleLayout(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.title, this);
		Button btn = (Button) findViewById(R.id.title_btn);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyActivityCollector.finishAllActivity();

			}
		});
	}

}
