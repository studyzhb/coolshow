package com.coolshow.app.activity;

import com.coolshow.app.BaseActivity;
import com.coolshow.app.R;
import com.coolshow.app.utils.HttpUtils;
import com.coolshow.app.utils.HttpUtils.HttpCallbackListener;
import com.coolshow.app.utils.ParseWeatherFromJson;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Administrator
 *
 */
public class WeatherActivity extends BaseActivity implements OnClickListener{
	private Button changeCitybtn,refreshWeatherbtn;
	private LinearLayout weatherInfoLayout;
	/**
	 * ��ʾ��������
	 */
	private TextView cityNameText;
	/**
	 * ����ʱ��
	 */
	private TextView publishText;
	/**
	 * ����״̬
	 */
	private TextView weatherStateText;
	/**
	 * ����1
	 */
	private TextView temp1Text;
	/**
	 * ����2
	 */
	private TextView temp2Text;
	/**
	 * ��ǰ����
	 */
	private TextView currentDateText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
		cityNameText=(TextView)findViewById(R.id.city_name);
		publishText=(TextView)findViewById(R.id.publish_text);
		weatherStateText=(TextView)findViewById(R.id.weather_state);
		temp1Text=(TextView)findViewById(R.id.temp1);
		temp2Text=(TextView)findViewById(R.id.temp2);
		changeCitybtn=(Button)findViewById(R.id.change_city);
		refreshWeatherbtn=(Button)findViewById(R.id.refresh_weather);
		currentDateText=(TextView)findViewById(R.id.current_date);
		changeCitybtn.setOnClickListener(this);
		refreshWeatherbtn.setOnClickListener(this);
		String countryCode=getIntent().getStringExtra("country_code");
		if(!TextUtils.isEmpty(countryCode)){
			//��ȡ�ؼ�����
			publishText.setText("ͬ���С���������");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		}else{
			showWeather();
		}
		
	}
	/**
	 * ��ȡ������������
	 * @param countryCode
	 */
	private void queryWeatherCode(String countryCode){
		String address="http://www.weather.com.cn/data/list3/city"+countryCode+".xml";
		queryFromServer(address,"countrycode");
	}
	/**
	 * ���������Ż�ȡ��Ϣ
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode){
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFromServer(address,"weatherinfo");
	}
	
	private void queryFromServer(final String address,final String type){
		HttpUtils.sendRequestWithWeather(address, new HttpCallbackListener() {
			/**
			 * 180101|101180101��ʽ����ǳ��д����ұ��Ƕ�Ӧ����������
			 */
			@Override
			public void onFinish(String response) {
				if("countrycode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						String[] wcode=response.split("\\|");
						if(wcode!=null&&wcode.length==2){
							String weatherCode=wcode[1];
							queryWeatherInfo(weatherCode);
						}
					}

				}else if("weatherinfo".equals(type)){
					ParseWeatherFromJson.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}
	
	private void showWeather(){
		SharedPreferences spf=PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(spf.getString("city_name", ""));
		temp1Text.setText(spf.getString("temp1", ""));
		temp2Text.setText(spf.getString("temp2", ""));
		weatherStateText.setText(spf.getString("weather_state", ""));
		publishText.setText("����"+spf.getString("publish_time", "")+"����");
		currentDateText.setText(spf.getString("current_date",""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.change_city:
			Intent intent=new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;

		case R.id.refresh_weather:
			publishText.setText("ͬ���С�������");
			SharedPreferences spf=PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode=spf.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		}
		
	}
	
	
	
	
	
	
	
	
	
	
}
