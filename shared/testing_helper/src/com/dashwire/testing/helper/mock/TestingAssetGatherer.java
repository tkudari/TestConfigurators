package com.dashwire.testing.helper.mock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class TestingAssetGatherer {

	private static final String TAG = "TestingAssetGatherer";

	private static JSONArray featureJSONArray = null;
	private static int retryCount = 0;

	public JSONArray gatherFeatureConfigs(Context context, String feature)
	{
		if (featureJSONArray == null)
		{
			Log.v(TAG,"Calling getFeatureConfigFromAsset");
			featureJSONArray = getFeatureConfigFromAsset(context, feature);
		}
		return featureJSONArray;
	}

	public static String getGatheredAsset(Context context)
	{
		Log.v(TAG,"getGatheredAsset context = " + context.getPackageName());
		SharedPreferences settings = context.getSharedPreferences("AssetGatherer", Context.MODE_PRIVATE);
		return settings.getString("gatheredAssetString", null);
	}

	public JSONArray getFeatureConfigFromAsset(Context context, String feature)
	{
		retryCount++;
		if ((getGatheredAsset(context) != null))
		{
			try
			{
				JSONObject gatheredAssetJSON = new JSONObject(getGatheredAsset(context));
				Log.v(TAG, "Checking " + feature + " available in gatheredAsset");
				if (gatheredAssetJSON.has(feature))
				{
					featureJSONArray = gatheredAssetJSON.getJSONArray(feature);
					//Log.v(TAG,"featureJSONArray = " + featureJSONArray.toString());
				}else
				{
					Log.e(TAG,feature + " is not available in gatheredAsset.");
				}
				return featureJSONArray;
			} catch (JSONException e)
			{
				Log.e(TAG, "JSONException in gatherFeatureConfigs = " + e.getMessage());
			}
		} else
		{
			Log.v(TAG, "********Assets are not gathered yet*********");
			if (retryCount <= 30 )
			{
				try
				{
					Thread.sleep(2000);
				} catch (InterruptedException e)
				{
				}
				getFeatureConfigFromAsset(context, feature);
			}
		}
		return featureJSONArray;
	}
	
	public static void startAssetGatherer(Context context)
	{
		Intent assetExtractionRequest = new Intent("com.dashwire.asset.gatherer.intent.action.START_EXTRACTION");
		context.startService(assetExtractionRequest);
	}
}
