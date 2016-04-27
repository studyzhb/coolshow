package com.coolshow.app.utils;

import com.coolshow.app.db.CoolShowDB;
import com.coolshow.app.model.City;
import com.coolshow.app.model.Country;
import com.coolshow.app.model.Province;

import android.text.TextUtils;

public class ParseWeatherFromJson {
	/**
	 * �������ص�ʡ������ ����|������ʽ��
	 * 01|����,02|�Ϻ�,03|���,04|����,05|������,06|����,07|����,08|���ɹ�,09|�ӱ�,10|ɽ��,11|����,12|ɽ��,13|�½�,14|����,15|�ຣ,16|����,17|����,
	 * 18|����,19|����,20|����,21|�㽭,22|����,23|����,24|����,25|����,26|����,27|�Ĵ�,28|�㶫,29|����,30|����,31|����,32|���,33|����,34|̨��
	 */
	public synchronized static boolean handleProvincesResponse(CoolShowDB coolshowDB,String response){
		if(!TextUtils.isEmpty(response)){
			//��ȡ������ʡ��
			String[] allProvinces=response.split(",");
			if(allProvinces!=null&&allProvinces.length>0){
				for(String p:allProvinces){
					Province province=new Province();
					//����ʡ�Ĵ��������ַֿ�
					String[] pmes=p.split("\\|");
					province.setProvinceName(pmes[1]); 
					province.setProvinceCode(pmes[0]);
					//�洢
					coolshowDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 1801|֣��,1802|����,1803|����,1804|���,1805|ƽ��ɽ,1806|����,1807|����,1808|����,1809|����
	 * �м�����
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
	 * �ؼ�����
	 * @param coolshowDB ���ݲ�����
	 * @param response	��ַ���ص���Ϣ
	 * @param provinceId ��ID
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
