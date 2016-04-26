package com.coolshow.app.db;

import java.util.ArrayList;
import java.util.List;

import com.coolshow.app.model.City;
import com.coolshow.app.model.Country;
import com.coolshow.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 对数据的增删查改进行管理
 * 
 * @author Administrator
 *
 */
public class CoolShowDB {
	// 数据库名字
	public static final String DB_NAME = "";
	// 版本
	public static final int VERSION = 1;
	// 单例模式在内部实例化对象
	private static CoolShowDB coolshowDB;
	private SQLiteDatabase db;

	// 私有化构造方法
	private CoolShowDB(Context context) {
		CoolShowOpenHelper dbHelper = new CoolShowOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	// 获取实例，保证全局只有一个对象产生
	public synchronized static CoolShowDB getInstance(Context context) {
		if (coolshowDB == null) {
			coolshowDB = new CoolShowDB(context);
		}
		return coolshowDB;
	}

	// 存储省一级的数据
	public void saveProvince(Province province) {
		if (null != province) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("province", null, values);
		}
	}

	// 获取数据库中省一级哦数据
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor=db.query("province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province p=new Province();
				p.setId(cursor.getInt(cursor.getColumnIndex("id")));
				p.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				p.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(p);
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}
	// 存储市一级的数据
	public void saveCity(City city) {
		if (null != city) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("city", null, values);
		}
	}
	// 获取数据库某省下的市一级哦数据
	public List<City> loadCities(int provinceId){
		List<City> list=new ArrayList<City>();
		Cursor cursor=db.query("city", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city=new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}

	// 存储县一级的数据
	public void saveCountry(Country country) {
		if (null != country) {
			ContentValues values = new ContentValues();
			values.put("country_name", country.getCountryName());
			values.put("country_code", country.getCountryCode());
			values.put("city_id", country.getCityId());
			db.insert("country", null, values);
		}
	}

	// 获取数据库中某城市县一级哦数据
	public List<Country> loadCountries(int cityId){
		List<Country> list=new ArrayList<Country>();
		Cursor cursor=db.query("country", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Country country=new Country();
				country.setId(cursor.getInt(cursor.getColumnIndex("id")));
				country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
				country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
				country.setCityId(cityId);
				list.add(country);
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}
	
}
