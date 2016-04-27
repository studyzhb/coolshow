package com.coolshow.app.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
	/**
	 * @param path
	 * 网络连接耗时操作在单独一个线程工作 回调不是太明白，为什么不能直接返回数据呢，可能是更安全吧(跟着大神脚步总是不会错)
	 * 还需要更深入学习 ,由于用到多出内部类，将参数用final修饰
	 */
	public static void sendRequestWithWeather(final String path, final HttpCallbackListener httpCallback) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection httpClient = null;
				try {
					URL url=new URL(path);
					httpClient =(HttpURLConnection) url.openConnection();
					httpClient.setRequestMethod("GET");
					httpClient.setConnectTimeout(8000);
					httpClient.setReadTimeout(8000);
					InputStream is = httpClient.getInputStream();
						// 对数据进行缓冲操作
						// 字节流转换字符流
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
						StringBuilder result=new StringBuilder();
						String line;
						while((line=br.readLine())!=null){
							result.append(line);
						}
						if (httpCallback != null) {
							httpCallback.onFinish(result.toString());
						}
				} catch (Exception e) {
					if (httpCallback != null) {
						httpCallback.onError(e);
					}
				} finally {

						if (httpClient!= null) {
							httpClient.disconnect();
						}
				}
			}
		}).start();
	}

	public interface HttpCallbackListener {
		void onFinish(String response);

		void onError(Exception e);
	}
}
