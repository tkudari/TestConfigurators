package com.dashwire.configurator;

import org.json.JSONArray;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.dashwire.configurator.utils.ConfiguratorUtils;

public abstract class ConfiguratorBaseIntentService extends IntentService {

    public static final String TAG = ConfiguratorBaseIntentService.class.getCanonicalName();

    private String featureId;
    private String featureName;
    protected String featureData;
    protected JSONArray mFeatureDataJSONArray;
    private Intent mLastIntent;

    public ConfiguratorBaseIntentService() {
        super(TAG);
    }

    @Override
    public final void onHandleIntent(Intent intent) {
        try {
            mLastIntent = intent;
            String action = intent.getAction();
            Log.v(TAG, "onHandleIntent : " + action);

            // We intend to silently eat actions we don't understand
            if (action != null && action.contains("com.dashwire.configure.intent.")) {
                this.featureId = intent.getStringExtra("featureId");
                this.featureName = intent.getStringExtra("featureName");
                this.featureData = intent.getStringExtra("featureData");
                this.mFeatureDataJSONArray = new JSONArray(this.featureData);

                Log.v(TAG, "featureId : " + featureId);
                Log.v(TAG, "featureName : " + featureName);
                Log.v(TAG, "featureData : " + featureData);
                Log.v(TAG, "featureDataArray : " + mFeatureDataJSONArray);

                //TODO: If the arguments are not valid, call onFailure.
                //TODO: It should have all arguments available as of this iteration. May relax requirements in future

                if (isFeatureIdValid() &&
                    isFeatureNameValid() &&
                    isFeatureDataJSONArrayValid()) {
                    onConfigFeature();
                } else {
                	Log.e(TAG,"Missing parameters");
                    onFailure("Missing parameters");
                }
            } else {
                Log.e(TAG, "Invalid action : " + action);
                onFailure("Invalid action");
            }
        } catch (Exception e) {
        	Log.e(TAG,"Exception : " + e.getMessage());
            onFailure(e.getMessage());
        }
    }

    private boolean isFeatureIdValid() {
        return featureId != null;
    }

    private boolean isFeatureNameValid() {
        return featureName != null;
    }

    private boolean isFeatureDataJSONArrayValid() {
        return mFeatureDataJSONArray.length() > 0;
    }

    protected abstract void onConfigFeature();

    protected void onFailure(String because) {
        Log.e(TAG, featureName + " failed because : " + because);
        ConfiguratorUtils.broadcastConfigurationResult(getApplicationContext(), featureId, featureName, featureData, "failed", because);
    }

    protected void onSuccess() {
    	Log.v(TAG, featureName + " Success : " + featureId);
        ConfiguratorUtils.broadcastConfigurationResult(getApplicationContext(), featureId, featureName, featureData, "success", null);
    }

    public Intent getLastIntent() {
        return mLastIntent;
    }

    public static Intent generateIntent(String type, JSONArray configArray) {
        Intent intent = new Intent("com.dashwire.feature.intent.CONFIGURE");
        intent.putExtra("featureId", "NotDefinedYet");
        intent.putExtra("featureName", type);
        intent.putExtra("featureData", configArray.toString());
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        return intent;
    }
}
