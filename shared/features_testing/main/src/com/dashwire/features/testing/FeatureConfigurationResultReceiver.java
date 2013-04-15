package com.dashwire.features.testing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dashwire.features.testing.events.FeatureTesting;
import com.dashwire.features.testing.events.FeatureTestingFailedEvent;
import com.dashwire.features.testing.events.FeatureTestingSuccessEvent;
import com.dashwire.features.testing.utils.BusProvider;

public class FeatureConfigurationResultReceiver extends BroadcastReceiver {

    private static final String TAG = "FeatureConfigurationResultReceiver";

    @Override
    public final void onReceive(Context context, Intent intent) 
    {
    	Log.v(TAG,"Inside FeatureConfigurationResultReceiver");
        String action = intent.getAction();
        if (action.equals("com.dashwire.feature.intent.CONFIGURATION_RESULT")) 
        {
        	String featureId = intent.getStringExtra("featureId");
         	String featureName = intent.getStringExtra("featureName");
        	String featureData = intent.getStringExtra("featureData");
        	String featureResult = intent.getStringExtra("result");
        	
        	if (featureId != null)
        	{
        		FeatureTesting feature = new FeatureTesting(featureId, featureName, featureData, null, null);
        		if (featureResult.equalsIgnoreCase("success"))
        		{
        			BusProvider.getInstance().post(new FeatureTestingSuccessEvent(feature));
        		}else if (featureResult.equalsIgnoreCase("failed"))
        		{
        			BusProvider.getInstance().post(new FeatureTestingFailedEvent(feature, intent.getStringExtra("reason")));
        		}
        	} else
        	{
        		Log.e(TAG,"Data missing");
        	}
        }
    }
}
