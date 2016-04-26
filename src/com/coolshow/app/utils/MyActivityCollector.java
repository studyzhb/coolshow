package com.coolshow.app.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class MyActivityCollector {
	private static List<Activity> list=new ArrayList<Activity>();
	
	public static void addActivity(Activity activity){
		list.add(activity);
	}
	public static void removeActivity(Activity activity){
		list.remove(activity);
	}
	public static void finishAllActivity(){
		for(Activity activity:list){
			activity.finish();
		}
	}

}
