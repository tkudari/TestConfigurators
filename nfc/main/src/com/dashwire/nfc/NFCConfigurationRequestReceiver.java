package com.dashwire.nfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

public class NFCConfigurationRequestReceiver extends BroadcastReceiver {

    private static final String TAG = NFCConfigurationRequestReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive of " + TAG);
        String action = intent.getAction();

        Log.v(TAG, "onReceive of " + TAG + " - action : " + intent.getAction());

        if (action.equals("com.dashwire.feature.intent.CONFIGURE")) {


            String featureId = intent.hasExtra("featureId") ? intent.getExtras().getString("featureId") : null;
            String featureName = intent.hasExtra("featureName") ? intent.getExtras().getString("featureName") : null;
            String featureData = intent.hasExtra("featureData") ? intent.getExtras().getString("featureData") : null;

            Log.v(TAG, "featureId : " + featureId);
            Log.v(TAG, "featureName : " + featureName);
            Log.v(TAG, "featureData : " + featureData);

            if (featureId != null && featureName != null && featureData != null) {
                try {
                    String intentAction = getFeatureConfiguratorIntentAction(context, featureName);
                    Intent featureConfiguratorIntent = new Intent(intentAction);

                    Set<String> keys = intent.getExtras().keySet();
                    Iterator<String> iterator = keys.iterator();

                    while (iterator.hasNext()) {
                        String nextKey = iterator.next();
                        featureConfiguratorIntent.putExtra(nextKey, intent.getExtras().getString(nextKey));
                    }

                    //featureConfiguratorIntent.putExtra("featureId", featureId);
                    //featureConfiguratorIntent.putExtra("featureName", featureName);
                    //featureConfiguratorIntent.putExtra("featureData", featureData);
                    Log.v(TAG, "Context = " + context.getPackageName());
                    context.startService(featureConfiguratorIntent);
                    Log.v(TAG, "after Starting featureConfiguratorIntent");
                } catch (Exception e) {
                	Log.e(TAG, "Exception on starting Intent Service : " + e.getMessage());
                }
            } else {
            	Log.e(TAG, "Data missing");
            }
        } else {
        	Log.v(TAG, "Invalid action");
        }
    }
    
    private String getFeatureConfiguratorIntentAction(Context context, String featureName)
    {
        String intentAction = "com.dashwire.configure.intent." + featureName;
        return intentAction;
    }
}
