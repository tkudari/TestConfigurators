package com.dashwire.nfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dashwire.nfc.events.NFCFeatureResultEvent;
import com.dashwire.nfc.utils.BusProvider;

public class ConfigurationResultReceiver extends BroadcastReceiver {

	private static final String TAG = "ConfigurationResultReceiver";

	@Override
	public final void onReceive(Context context, Intent intent) {
		Log.v(TAG, "Inside FeatureConfigurationResultReceiver");

		String featureId = intent.hasExtra("featureId") ? intent.getExtras().getString("featureId") : null;
		
		if (featureId != null) {
			Log.v(TAG,"featureId = " + featureId);
			NFCFeatureResultEvent nfcFeatureResult = new NFCFeatureResultEvent(intent);	
			Log.v(TAG,"Publishing NFCFeatureResultEvent");
			Log.v(TAG,"featureData = " + nfcFeatureResult.getFeatureData());
			Log.v(TAG,"featureResult = " + nfcFeatureResult.featureResult);
			BusProvider.getInstance().post(nfcFeatureResult);
		} else {
			Log.e(TAG, "FeatureId Missing in Configuration Result");
		}
	}
}
