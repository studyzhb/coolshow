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
	 * ʡ �� �� �б�
	 */
	private List<Province> provinces;
	private List<City> cities;
	private List<Country> countries;
	// ��ǰѡ��ļ����־λ
	private int currentLevel;
	// ѡ�е�����
	private Province selectedProvince;
	private City selectedCtity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences spf=PreferenceManager.getDefaultSharedPreferences(this);
		//����ѡ������־λ
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
		 * ѡ���ؼ��б���ת���¸�����ҳ��
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
	 * �����ݿ��ѯȫ�����е�ʡ��
	 * �״λ��ȴӷ��������ã��ٴ洢�����ݿ�
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
			titleView.setText("�й�");
			currentLevel=LEVEL_PROVINCE;
		}else{
			queryFromServer(null, "province");
		}
	}

	/**
	 * �����ݿ��ѯĳ��ʡ�µ�������
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
	 * �����ݿ��ѯĳ�����µ���������
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
	 * ȥ��������ѯ����
	 * @param code ʡ���У��ش���
	 * @param type ʡ���У�����������
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
					//ͨ��runOnUiThread()�����ص����̴߳����߼�
					//��һ���ã����˽�
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
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT);
					}
				});
			}
		});
	}
	/**
	 * ��ʾ�Ի���
	 */
	private void showProgressDialog(){
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("���ڼ��ء�������");
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
