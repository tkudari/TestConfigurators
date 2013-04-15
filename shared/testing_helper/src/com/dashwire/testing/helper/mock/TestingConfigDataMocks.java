package com.dashwire.testing.helper.mock;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class TestingConfigDataMocks {

	private static final String TAG = "FeatureConfigDataMocks";

	public static JSONArray loadFeatureConfigDataMocks(Context context, String feature) {
		try {
			Log.v(TAG, "context = " + context.getPackageName());

			InputStream inputStream = context.getAssets().open("json/TestingDataMocks.json");
			String jsonString = convertStreamToString(inputStream);
			try {
				JSONObject root = new JSONObject(jsonString);
				return root.getJSONArray(feature);
			} catch (JSONException e) {
				Log.e(TAG, "JSONException in loadFeatureConfigDataMocks = " + e.getMessage());
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException in loadFeatureConfigDataMocks = " + e.getMessage());
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
