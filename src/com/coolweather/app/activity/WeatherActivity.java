package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.LogUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends  Activity implements OnClickListener{

	private LinearLayout weatherInfoLayout;
	//用于显示城市名
	private TextView cityNameTV;
	//用于显示发布时间
	private TextView publishTV;
	//用于显示天气描述信息
	private TextView weatherDespTV;
	//用于显示气温1、2
	private TextView temp1TV;
	private TextView temp2Tv;
	//用于显示当前日期
	private TextView currentDateTV;
	private static final String TAG = "WActivity";
	
	//切换城市按钮
	private Button switchCityBtn;
	//更新天气的按钮
	private Button refreshWeatherBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//加载布局
		setContentView(R.layout.activity_weather);
		//初始化组件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameTV = (TextView) findViewById(R.id.city_name);
		publishTV = (TextView) findViewById(R.id.publish_text);
		weatherDespTV = (TextView) findViewById(R.id.weather_desp);
		temp1TV = (TextView) findViewById(R.id.temp1);
		temp2Tv = (TextView) findViewById(R.id.temp2);
		currentDateTV = (TextView) findViewById(R.id.current_date);
		
		/*
		 * 实例化组件并添加监听
		 */
		switchCityBtn = (Button) findViewById(R.id.switch_city);
		refreshWeatherBtn = (Button) findViewById(R.id.switch_weather);
		switchCityBtn.setOnClickListener(this);
		refreshWeatherBtn.setOnClickListener(this);
		
		String countyCode = getIntent().getStringExtra("county_code");
//		LogUtil.info(TAG+"53", countyCode);
		if(!TextUtils.isEmpty(countyCode)){
			publishTV.setText("同步中...");
//			LogUtil.info(TAG+"56", countyCode);
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameTV.setVisibility(View.INVISIBLE);
			queryWeatherCode( countyCode );
		}else{
			//没有县级代号是就显示本地天气
			showWeather();
		}
	}
	/*
	 * 查询县级代号所对应的天气代号
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn"
				+ "/data/list3/city" + countyCode + ".xml";
		LogUtil.info(TAG+"71", address);
		querryFromServer( address,"countyCode" );
	}
	/*
	 * 查询天气代号所对应的天气信息
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn"
				+ "/data/cityinfo/" + weatherCode + ".html";   //101230103
		LogUtil.info(TAG+"80", address);
		querryFromServer( address,"weatherCode" );
	}
	/*
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 */
	private void querryFromServer(final String address,final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				LogUtil.info(TAG+"91", type);
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						LogUtil.info(TAG, response);
						//从服务器返回的数据中解析出天气代号
						String[] array = response.split("\\|");
						if(array!=null&&array.length==2){
							String weatherCode = array[1];
							LogUtil.info(TAG, weatherCode);
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					//处理服务器返回的天气信息
					LogUtil.info(TAG+"105", type);
					LogUtil.info(TAG+"106", response);
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						publishTV.setText("同步失败");
					}
					
				});
			}
		});		
	}
	/*
	 * 从 SharedPreferences文件中读取存储的天气信息，并显示到界面上
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameTV.setText(prefs.getString("city_name", ""));
		temp1TV.setText(prefs.getString("temp1", ""));
		temp2Tv.setText(prefs.getString("temp2", ""));
		weatherDespTV.setText(prefs.getString("weather_desp", ""));
		publishTV.setText("今天"+prefs.getString("publish_time", "") +"发布");
		currentDateTV.setText(prefs.getString("current_date", ""));
		LogUtil.info(TAG+"158", prefs.getString("current_date", ""));
		
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameTV.setVisibility(View.VISIBLE);	
		
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);  //startService()非startActivity()
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.switch_weather:
			publishTV.setText("同步中...");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				LogUtil.info(TAG+"178", weatherCode);
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}
}
















