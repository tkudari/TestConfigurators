package com.dashwire.configurator.controller;

import java.util.Iterator;
import java.util.Set;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dashwire.configurator.events.Feature;
import com.dashwire.configurator.utils.ConfiguratorUtils;
import com.dashwire.configurator.utils.FeatureDetailsGatherer;

public class ConfiguratorControllerService extends Service {

	private static final String TAG = "ConfiguratorControllerService";
	private final IBinder binder = new FeatureControllerBinder();
	private FeatureDetailsGatherer gatherer = null;

	public class FeatureControllerBinder extends Binder {
		public ConfiguratorControllerService getService() {
			return ConfiguratorControllerService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		gatherer = new FeatureDetailsGatherer();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy of " + TAG + "called");
		super.onDestroy();
	}

	public void setFeatureDetailsGatherer(FeatureDetailsGatherer fdg) {
		gatherer = fdg;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			Feature feature = new Feature(intent);
			Log.v(TAG, TAG + " on start command");
			Log.v(TAG, "featureId : " + feature.getFeatureId());
			Log.v(TAG, "featureName : " + feature.getFeatureName());
			Log.v(TAG, "featureData : " + feature.getFeatureData());

			if (feature.getFeatureId() != null) {

				if (feature.getFeatureName() == null && feature.getFeatureData() == null) {

					feature = gatherer.gatherFeatureDetails(getApplicationContext(), feature);
				}

				if (feature.getFeatureName() != null && feature.getFeatureData() != null) {
					getApplicationContext().startService(getFeatureConfiguratorIntentService(feature));
				} else {
					ConfiguratorUtils.broadcastConfigurationResult(getApplicationContext(), feature.getFeatureId(), feature.getFeatureName(),
							feature.getFeatureData(), "failed", "Invalid FeatureId");
				}
			} else {
				Log.e(TAG, "FeatureId missing");
				ConfiguratorUtils.broadcastConfigurationResult(getApplicationContext(), feature.getFeatureId(), feature.getFeatureName(),
						feature.getFeatureData(), "failed", "FeatureId missing");
			}
		} else {
			Log.v(TAG, "onStartCommand intent was null. Taking no action.");
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	private Intent getFeatureConfiguratorIntentService(Feature feature) {
		String intentAction = "com.dashwire.configure.intent." + feature.getFeatureName();
		Intent featureConfiguratorIntent = new Intent(intentAction);
		featureConfiguratorIntent.putExtras(feature.getIntent().getExtras());

		Log.v(TAG, "Feature values on getFeatureConfiguratorIntentService");

		Set<String> keys = featureConfiguratorIntent.getExtras().keySet();
		Iterator<String> iterator = keys.iterator();

		while (iterator.hasNext()) {
			String nextKey = iterator.next();
			Log.v(TAG, "Key = " + nextKey + " Value = " + featureConfiguratorIntent.getExtras().get(nextKey).toString());
		}

		return featureConfiguratorIntent;
	}
}
