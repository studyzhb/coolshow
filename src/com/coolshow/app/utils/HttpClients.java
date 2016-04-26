package com.coolshow.app.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
public class HttpClients {
	public static String sendMessageWithHttpClient(String path){
		String backResult = null;
		HttpClient httpClient;
		try{
			httpClient=new DefaultHttpClient();
			HttpPost httpPost=new HttpPost(path);
			HttpResponse httpResponse=httpClient.execute(httpPost);
			if(httpResponse.getStatusLine().getStatusCode()==200){
			backResult=EntityUtils.toString(httpResponse.getEntity(), "utf-8") ;
			System.out.println(backResult);
			return backResult;
//				return changeString(backResult);
			}
			else{
				Log.i("clients","ÍøÂç´íÎó");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return backResult;
	}

	private static List<Map<String,Object>> changeString(String str) {
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		
		try {
			JSONObject json=new JSONObject(str);
//			int count=json.getInt("total");
//			map.put("count", count);
//			list.add(map);
			JSONArray tngoes=json.getJSONArray("tngou");
			for(int i=0;i<tngoes.length();i++){
				Map<String,Object> map=new HashMap<String, Object>();
				JSONObject single=tngoes.getJSONObject(i);
				String image=single.getString("img");
				String title=single.getString("title");
				map.put("image", image);
				map.put("title", title);		
				list.add(map);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
		
	}
	public static void main(String[] args) {
			String str=HttpClients.sendMessageWithHttpClient("www.weather.com.cn/data/cityinfo/101180101.html");
			System.out.println(str);
	}
}
