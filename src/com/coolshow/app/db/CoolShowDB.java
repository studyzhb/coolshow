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
 * �����ݵ���ɾ��Ľ��й���
 * 
 * @author Administrator
 *
 */
public class CoolShowDB {
	// ���ݿ�����
	public static final String DB_NAME = "";
	// �汾
	public static final int VERSION = 1;
	// ����ģʽ���ڲ�ʵ��������
	private static CoolShowDB coolshowDB;
	private SQLiteDatabase db;

	// ˽�л����췽��
	private CoolShowDB(Context context) {
		CoolShowOpenHelper dbHelper = new CoolShowOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	// ��ȡʵ������֤ȫ��ֻ��һ���������
	public synchronized static CoolShowDB getInstance(Context context) {
		if (coolshowDB == null) {
			coolshowDB = new CoolShowDB(context);
		}
		return coolshowDB;
	}

	// �洢ʡһ��������
	public void saveProvince(Province province) {
		if (null != province) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("province", null, values);
		}
	}

	// ��ȡ���ݿ���ʡһ��Ŷ����
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
	// �洢��һ��������
	public void saveCity(City city) {
		if (null != city) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("city", null, values);
		}
	}
	// ��ȡ���ݿ�ĳʡ�µ���һ��Ŷ����
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

	// �洢��һ��������
	public void saveCountry(Country country) {
		if (null != country) {
			ContentValues values = new ContentValues();
			values.put("country_name", country.getCountryName());
			values.put("country_code", country.getCountryCode());
			values.put("city_id", country.getCityId());
			db.insert("country", null, values);
		}
	}

	// ��ȡ���ݿ���ĳ������һ��Ŷ����
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
