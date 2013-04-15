package com.dashwire.configurator.utils;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.dashwire.configurator.R;
import com.dashwire.configurator.events.Feature;

public class FeatureDetailsGatherer {

	private static final String TAG = FeatureDetailsGatherer.class.getCanonicalName();

	public Feature gatherFeatureDetails(Context context, Feature feature){
		try {
			JSONObject featureJSON = getFeatureDataFromJSONFile(context, feature.getFeatureId());
			if (featureJSON != null)
			{
				feature.setFeatureName(featureJSON.getString("featureName"));
				feature.setFeatureData(featureJSON.getJSONArray("featureData").toString());
				Log.v(TAG, "featureName = " + feature.getFeatureName());
				Log.v(TAG, "featureData = " + feature.getFeatureData());
			}
		} catch (JSONException e) {
			Log.e(TAG, "JSONException : " + e.getMessage());
		}
		return feature;
	}

	private JSONObject getFeatureDataFromJSONFile(Context context, String featureId){
		try {
			Log.v(TAG, "context = " + context.getPackageName());

			//InputStream inputStream = context.getAssets().open("json/featureConfigData.json");
			InputStream inputStream = context.getResources().openRawResource(R.raw.json_file);
			String jsonString = convertStreamToString(inputStream);
			try {
				JSONObject root = new JSONObject(jsonString);
				Log.v(TAG, "root = " + root.toString());
				return root.getJSONObject(featureId.toLowerCase());
			} catch (JSONException e) {
				Log.e(TAG, "JSONException in loadFeatureConfigDataMocks = " + e.getMessage());
			}
		} catch (Exception e) {
			Log.e(TAG, "IOException in loadFeatureConfigDataMocks = " + e.getMessage());
		}
		return null;
	}

	private String convertStreamToString(java.io.InputStream is) {
		try {
			return new java.util.Scanner(is).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}
}
