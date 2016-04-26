package com.coolshow.app.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

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
				HttpClient httpClient = null;
				InputStream is = null;
				BufferedReader br = null;
				try {
					httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(path);
					HttpResponse httpResponse = httpClient.execute(httpPost);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						is = httpResponse.getEntity().getContent();
						// �����ݽ��л������
						// �ֽ���ת���ַ���
						br = new BufferedReader(new InputStreamReader(is));
						StringBuilder result=new StringBuilder();
						String line;
						while((line=br.readLine())!=null){
							result.append(line);
						}
						if (httpCallback != null) {
							httpCallback.onFinish(result.toString());
						}
					}
				} catch (Exception e) {
					if (httpCallback != null) {
						httpCallback.onError(e);
					}
				} finally {

					try {
						if (br != null) {
							br.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
