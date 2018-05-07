package com.hireboss.android.roachlabs;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	HttpURLConnection connection;
	BufferedReader br = null;
	StringBuffer sb = null;
	String data=null;
	// constructor
	public JSONParser() {

	}

	public JSONObject getJSONFromUrl(String url) {

		// Making HTTP request
		try {
			// defaultHttpClient
			URL urls;
			urls = new URL(url);

			connection = (HttpURLConnection) urls.openConnection();
			connection.connect();
			InputStream is = connection.getInputStream();

			br = new BufferedReader(new InputStreamReader(is));
			//reading o/p line by line

			sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			data=sb.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		}
		

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(data);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}
}

