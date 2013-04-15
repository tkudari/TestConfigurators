package com.dashwire.testing.helper.mock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class AssetGathererResultReceiver extends BroadcastReceiver {

	private static final String TAG = "TestingAssetGatherer";

	private static String gatheredAssetString;

	@Override
	public final void onReceive(Context context, Intent intent)
	{
		Log.v(TAG, "Inside AssetGathererResultReceiver");
		String action = intent.getAction();
		if (action.equals("com.dashwire.asset.gatherer.intent.action.EXTRACTION_RESULT"))
		{
			gatheredAssetString = intent.getStringExtra("assetResult");
			if (gatheredAssetString != null)
			{
				storeGatheredAssetString(context, gatheredAssetString);
			}
		}
	}

	public static void storeGatheredAssetString(Context context, String gatheredAssetString)
	{
		Log.v(TAG,"storeGatheredAssetString context package = " + context.getPackageName());
		SharedPreferences settings = context.getSharedPreferences("AssetGatherer", Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putString("gatheredAssetString", gatheredAssetString);
		editor.commit();
	}
}
