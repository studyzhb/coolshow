package com.coolshow.app.utils;

import com.coolshow.app.db.CoolShowDB;
import com.coolshow.app.model.City;
import com.coolshow.app.model.Country;
import com.coolshow.app.model.Province;

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
	
	
	
	
	
	
	
	
	
	
}
