package com.dashwire.configurator.utils;

import android.content.Context;
import android.content.Intent;

import com.dashwire.base.debug.DashLogger;
import com.dashwire.configurator.events.Feature;

public class ConfiguratorUtils
{
	private static final String TAG = ConfiguratorUtils.class.getCanonicalName();
	
	public static void broadcastConfigurationResult(Context context, String featureId, String featureName, String featureData, String result, String reason)
	{
		DashLogger.v(TAG, featureName + " result : " + result + " reason : " + reason);
		Intent configurationResultIntent = new Intent();
		configurationResultIntent.setAction(ConfiguratorConstants.INTENT_FEATURE_CONFIGURATION_RESULT);
		configurationResultIntent.putExtra("featureId", featureId);
		configurationResultIntent.putExtra("featureName", featureName);
		configurationResultIntent.putExtra("featureData", featureData);
		configurationResultIntent.putExtra("result", result);
		configurationResultIntent.putExtra("reason", reason);
		context.sendBroadcast(configurationResultIntent);
	}

}
