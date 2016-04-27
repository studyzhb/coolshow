package com.coolshow.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author
 *建立本地数据用来存放地区商品信息活动
 */
public class CoolShowOpenHelper extends SQLiteOpenHelper {
	//省表SQL语句
	public static final String CREATE_PROVINCE="create table province("+"id integer primary key autoincrement,"+"province_name text,"+"province_code text)";
	//市表语句
	public static final String CREATE_CITY="create table city("+"id integer primary key autoincrement,"+"city_name text,"+"city_code text,"+"province_id integer)";
	//县表语句
	public static final String CREATE_COUNTRY="create table country("+"id integer primary key autoincrement,"+"country_name text,"+"country_code text,"+"city_id integer)";
	
	
	public CoolShowOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTRY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
