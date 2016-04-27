package com.coolshow.app.activity;

import com.coolshow.app.BaseActivity;
import com.coolshow.app.R;
import com.coolshow.app.utils.HttpUtils;
import com.coolshow.app.utils.HttpUtils.HttpCallbackListener;
import com.coolshow.app.utils.ParseWeatherFromJson;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Administrator
 *
 */
public class WeatherActivity extends BaseActivity {
	private LinearLayout weatherInfoLayout;
	/**
	 * 显示城市名称
	 */
	private TextView cityNameText;
	/**
	 * 发布时间
	 */
	private TextView publishText;
	/**
	 * 天气状态
	 */
	private TextView weatherStateText;
	/**
	 * 气温1
	 */
	private TextView temp1Text;
	/**
	 * 气温2
	 */
	private TextView temp2Text;
	/**
	 * 当前日期
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
		currentDateText=(TextView)findViewById(R.id.current_date);
		String countryCode=getIntent().getStringExtra("country_code");
		if(!TextUtils.isEmpty(countryCode)){
			//获取县级代号
			publishText.setText("同步中。。。。。");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		}else{
			showWeather();
		}
		
	}
	/**
	 * 获取县区天气代号
	 * @param countryCode
	 */
	private void queryWeatherCode(String countryCode){
		String address="http://www.weather.com.cn/data/list3/city"+countryCode+".xml";
		queryFromServer(address,"countrycode");
	}
	/**
	 * 从天气代号获取信息
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode){
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFromServer(address,"weatherinfo");
	}
	
	private void queryFromServer(final String address,final String type){
		HttpUtils.sendRequestWithWeather(address, new HttpCallbackListener() {
			/**
			 * 180101|101180101格式左边是城市代号右边是对应的天气代号
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
						publishText.setText("同步失败");
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
		publishText.setText("今天"+spf.getString("publish_time", "")+"发布");
		currentDateText.setText(spf.getString("current_date",""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
	}
	
	
	
	
	
	
	
	
	
	
}
