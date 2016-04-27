package com.coolshow.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolshow.app.BaseActivity;
import com.coolshow.app.R;
import com.coolshow.app.db.CoolShowDB;
import com.coolshow.app.model.City;
import com.coolshow.app.model.Country;
import com.coolshow.app.model.Province;
import com.coolshow.app.utils.HttpUtils;
import com.coolshow.app.utils.ParseWeatherFromJson;
import com.coolshow.app.utils.HttpUtils.HttpCallbackListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends BaseActivity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;

	private ProgressDialog progressDialog;
	private TextView titleView;
	private ListView list_weather;
	private ArrayAdapter<String> adapter;
	private CoolShowDB coolshowDB;
	private List<String> datalist = new ArrayList<String>();
	/**
	 * 省 市 县 列表
	 */
	private List<Province> provinces;
	private List<City> cities;
	private List<Country> countries;
	// 当前选择的级别标志位
	private int currentLevel;
	// 选中的数据
	private Province selectedProvince;
	private City selectedCtity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences spf=PreferenceManager.getDefaultSharedPreferences(this);
		//城市选中与否标志位
		if(spf.getBoolean("city_selected", false)){
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
		}
		setContentView(R.layout.choose_area);
		list_weather = (ListView) findViewById(R.id.list_view_weather);
		titleView = (TextView) findViewById(R.id.title_weather);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datalist);
		list_weather.setAdapter(adapter);
		coolshowDB = CoolShowDB.getInstance(this);
		/**
		 * 选中县级列表跳转到下个天气页面
		 */
		list_weather.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinces.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCtity = cities.get(position);
					queryCountries();
				}else if(currentLevel == LEVEL_COUNTRY){
					String countryCode=countries.get(position).getCountryCode();
					Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("country_code", countryCode);
					startActivity(intent);
					finish();
				}
			}
		});
		queryProvinces();
	}

	/**
	 * 从数据库查询全国所有的省份
	 * 首次会先从服务器调用，再存储到数据库
	 */
	private void queryProvinces() {
		provinces=coolshowDB.loadProvinces();
		if(provinces.size()>0){
			datalist.clear();
			for(Province pms:provinces){
				datalist.add(pms.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			list_weather.setSelection(0);
			titleView.setText("中国");
			currentLevel=LEVEL_PROVINCE;
		}else{
			queryFromServer(null, "province");
		}
	}

	/**
	 * 从数据库查询某个省下的所有市
	 */

	private void queryCities() {
		cities=coolshowDB.loadCities(selectedProvince.getId());
		if(cities.size()>0){
			datalist.clear();
			for(City cmes:cities){
				datalist.add(cmes.getCityName());
			}
			adapter.notifyDataSetChanged();
			list_weather.setSelection(0);
			titleView.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * 从数据库查询某个市下的所有县区
	 */

	private void queryCountries() {
		countries=coolshowDB.loadCountries(selectedCtity.getId());
		if(countries.size()>0){
			datalist.clear();
			for(Country ctmes:countries){
				datalist.add(ctmes.getCountryName());
			}
			adapter.notifyDataSetChanged();
			list_weather.setSelection(0);
			titleView.setText(selectedCtity.getCityName());
			currentLevel=LEVEL_COUNTRY;
		}else{
			queryFromServer(selectedCtity.getCityCode(), "country");
		}
	}
	
	/**
	 * 去服务器查询数据
	 * @param code 省，市，县代号
	 * @param type 省，市，县哪种类型
	 */
	private void queryFromServer(final String code,final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtils.sendRequestWithWeather(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result=false;
				if("province".equals(type)){
					result=ParseWeatherFromJson.handleProvincesResponse(coolshowDB, response);
				}else if("city".equals(type)){
					result=ParseWeatherFromJson.handleCitiesResponse(coolshowDB, response, selectedProvince.getId());
				}else if("country".equals(type)){
					result=ParseWeatherFromJson.handleCountryResponse(coolshowDB, response, selectedCtity.getId());
				}
				if(result){
					//通过runOnUiThread()方法回到主线程处理逻辑
					//第一次用，不了解
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("country".equals(type)){
								queryCountries();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT);
					}
				});
			}
		});
	}
	/**
	 * 显示对话框
	 */
	private void showProgressDialog(){
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载。。。。");
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}
	/**
	 * lose
	 */
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	/*
	 * back
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel==LEVEL_COUNTRY){
			queryCities();
		}else if(currentLevel==LEVEL_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}
}
