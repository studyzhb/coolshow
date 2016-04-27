package com.coolshow.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import com.coolshow.app.db.CoolShowDB;
import com.coolshow.app.model.City;
import com.coolshow.app.model.Country;
import com.coolshow.app.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class ParseWeatherFromJson {
	/**
	 * 解析返回的省级数据 代号|地区格式的
	 * 01|北京,02|上海,03|天津,04|重庆,05|黑龙江,06|吉林,07|辽宁,08|内蒙古,09|河北,10|山西,11|陕西,12|山东,13|新疆,14|西藏,15|青海,16|甘肃,17|宁夏,
	 * 18|河南,19|江苏,20|湖北,21|浙江,22|安徽,23|福建,24|江西,25|湖南,26|贵州,27|四川,28|广东,29|云南,30|广西,31|海南,32|香港,33|澳门,34|台湾
	 */
	public synchronized static boolean handleProvincesResponse(CoolShowDB coolshowDB,String response){
		if(!TextUtils.isEmpty(response)){
			//提取出各个省份
			String[] allProvinces=response.split(",");
			if(allProvinces!=null&&allProvinces.length>0){
				for(String p:allProvinces){
					Province province=new Province();
					//各个省的代号与名字分开
					String[] pmes=p.split("\\|");
					province.setProvinceName(pmes[1]); 
					province.setProvinceCode(pmes[0]);
					//存储
					coolshowDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 1801|郑州,1802|安阳,1803|新乡,1804|许昌,1805|平顶山,1806|信阳,1807|南阳,1808|开封,1809|洛阳
	 * 市级数据
	 */
	public synchronized static boolean handleCitiesResponse(CoolShowDB coolshowDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities=response.split(",");
			if(allCities!=null&&allCities.length>0){
				for(String c:allCities){
					String[] cmes=c.split("\\|");
					City city=new City();
					city.setCityName(cmes[1]);
					city.setCityCode(cmes[0]);
					city.setProvinceId(provinceId);
					coolshowDB.saveCity(city);
				}
				return true;
			}
		}
		
		return false;
	}
	
	
	
	/**
	 * 县级数据
	 * @param coolshowDB 数据操作类
	 * @param response	地址返回的信息
	 * @param provinceId 市ID
	 * @return
	 */
	public synchronized static boolean handleCountryResponse(CoolShowDB coolshowDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCountries=response.split(",");
			if(allCountries!=null&&allCountries.length>0){
				for(String cts:allCountries){
					String[] ctmes=cts.split("\\|");
					Country country=new Country();
					country.setCountryName(ctmes[1]);
					country.setCountryCode(ctmes[0]);
					country.setCityId(cityId);
					coolshowDB.saveCountry(country);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析服务器返回的JSON数据，并存储到本地
	 * {"weatherinfo":{"city":"閮戝窞","cityid":"101180101","temp1":"5鈩�","temp2":"22鈩�",
	 * "weather":"鏅�","img1":"n0.gif","img2":"d0.gif","ptime":"18:00"}}
	 */
	public static void handleWeatherResponse(Context context,String response){
		try{
			JSONObject json=new JSONObject(response);
			JSONObject weatherInfo=json.getJSONObject("weatherinfo");
			String cityName=weatherInfo.getString("city");
			String weatherCode=weatherInfo.getString("cityid");
			String temp1=weatherInfo.getString("temp1");
			String temp2=weatherInfo.getString("temp2");
			String weatherState=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherState, publishTime);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 返回的天气信息存储到sharedPreferences文件中
	 * 先获取sharedPreferences对象通过PreferenceManager获得
	 * 调用sharedPreferences.edit()获得sharedPreferences.Edit对象
	 * 在此对象中添加数据，然后提交
	 */
	public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherState,String publishTime){
		//格式化日期格式
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_state", weatherState);
		editor.putString("publish_time",publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		
		editor.commit();
	}
	
	
	
	
	
	
	
	
	
}
