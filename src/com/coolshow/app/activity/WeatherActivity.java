package com.coolshow.app.activity;

import com.coolshow.app.BaseActivity;
import com.coolshow.app.R;
import com.coolshow.app.utils.HttpUtils;
import com.coolshow.app.utils.HttpUtils.HttpCallbackListener;
import com.coolshow.app.utils.ParseWeatherFromJson;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
//import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

/**
 * @author Administrator
 *
 */
public class WeatherActivity extends BaseActivity implements OnClickListener,SensorEventListener,OnRefreshListener{
	private Button changeCitybtn,refreshWeatherbtn;
	private LinearLayout weatherInfoLayout;
	private SensorManager sensorManager;
	private Vibrator vibrator;
//	private SwipeRefreshLayout mSwipeLayout;
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
		
//		mSwipeLayout=(SwipeRefreshLayout) findViewById(R.id.pull_to_refresh_weather);
//		mSwipeLayout.setOnRefreshListener(this);
//		mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,android.R.color.holo_orange_light, android.R.color.holo_red_light);
				
				
		changeCitybtn=(Button)findViewById(R.id.change_city);
		refreshWeatherbtn=(Button)findViewById(R.id.refresh_weather);
		currentDateText=(TextView)findViewById(R.id.current_date);
		changeCitybtn.setOnClickListener(this);
		refreshWeatherbtn.setOnClickListener(this);
		//��ȡ���ٶȴ�����
		sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
		vibrator=(Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		
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
			updateWeather();
			break;
		}
		
	}
	void updateWeather(){
		publishText.setText("ͬ���С�������");
		SharedPreferences spf=PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode=spf.getString("weather_code", "");
		if(!TextUtils.isEmpty(weatherCode)){
			queryWeatherInfo(weatherCode);
		}
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		if(Math.abs(event.values[0])>15||Math.abs(event.values[1])>15||Math.abs(event.values[2])>15){
			vibrator.vibrate(500);
			updateWeather();
		}
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		
	}
	@Override
	public void onRefresh() {
		updateWeather();
	}
	
	
	
	
	
	
	
}
