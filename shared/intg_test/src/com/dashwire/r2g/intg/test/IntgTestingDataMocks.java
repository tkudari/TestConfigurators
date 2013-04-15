package com.dashwire.r2g.intg.test;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class IntgTestingDataMocks {

	private static final String TAG = "IntgTestingDataMocks";

	public static JSONArray getTestingConfigFromDataMocks(Context context, String feature) {
		try {
			Log.v(TAG, "context = " + context.getPackageName());

			InputStream inputStream = context.getAssets().open("json/TestingDataMocks.json");
			String jsonString = convertStreamToString(inputStream);
			try {
				JSONObject root = new JSONObject(jsonString);
				return root.getJSONArray(feature.toLowerCase());
			} catch (JSONException e) {
				Log.e(TAG, "JSONException in TestingDataMocks = " + e.getMessage());
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException in TestingDataMocks = " + e.getMessage());
		}
		return null;
	}

	private static String convertStreamToString(java.io.InputStream is) {
		try {
			return new java.util.Scanner(is).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}
}
