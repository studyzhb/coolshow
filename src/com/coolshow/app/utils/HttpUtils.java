package com.coolshow.app.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
	/**
	 * @param path
	 * �������Ӻ�ʱ�����ڵ���һ���̹߳��� �ص�����̫���ף�Ϊʲô����ֱ�ӷ��������أ������Ǹ���ȫ��(���Ŵ���Ų����ǲ����)
	 * ����Ҫ������ѧϰ ,�����õ�����ڲ��࣬��������final����
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
						// �����ݽ��л������
						// �ֽ���ת���ַ���
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
