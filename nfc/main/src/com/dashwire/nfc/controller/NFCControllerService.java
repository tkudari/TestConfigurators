package com.dashwire.nfc.controller;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.dashwire.nfc.events.NFCFeature;
import com.dashwire.nfc.events.NFCFeatureRequestEvent;
import com.dashwire.nfc.events.NFCFeatureResultEvent;
import com.dashwire.nfc.ui.ConfigurationStatusActivity;
import com.dashwire.nfc.utils.BusProvider;
import com.dashwire.nfc.utils.NFCCommonUtils;
import com.dashwire.nfc.utils.RingtonePlayer;
import com.squareup.otto.Subscribe;

public class NFCControllerService extends Service {

	private static final String TAG = "NFCControllerService";
	private final IBinder binder = new NFCControllerBinder();
	Handler delayHandler;

	public class NFCControllerBinder extends Binder {
		public NFCControllerService getService() {
			return NFCControllerService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		delayHandler = new Handler();
		NFCCommonUtils.setNFCLock(getApplicationContext(), false);
		BusProvider.getInstance().register(this);
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy of " + TAG + "called");
		BusProvider.getInstance().unregister(this);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG,"NFCControllerService Started");
		if (intent != null && intent.getStringExtra("NFCTagData") != null)
		{
			NFCFeatureRequestEvent nfcFeatureRequest = new NFCFeatureRequestEvent(intent.getStringExtra("NFCTagData"));
			if (nfcFeatureRequest.isValidNFCFeature() && NFCCommonUtils.getNFCLock(getApplicationContext()) == false)
			{			
				Intent configStatusActivityintent = new Intent(getApplicationContext(), ConfigurationStatusActivity.class);
				configStatusActivityintent.putExtra("featureName", nfcFeatureRequest.getFeatureName());
				configStatusActivityintent.putExtra("featureConfigStatus", 0);
				configStatusActivityintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(configStatusActivityintent);
				sendBroadcastToConfigurators(nfcFeatureRequest);
				NFCCommonUtils.setNFCLock(getApplicationContext(), true);
			}
		}else
		{
			Log.v(TAG, "onStartCommand intent or NFC Tag Data was null. Taking no action.");
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Subscribe
	public void onFeatureResult(NFCFeatureResultEvent event) {
		Log.v(TAG, "onFeatureResult");
		
		// Updating the ConfigStatusActivity if it is in Foreground
		if (event.featureConfigStatus == NFCFeature.SUCCESS)
		{
			delayHandler.postDelayed(new DelayedPlayer(event), 1000);
		}
		
		// Starting ConfigStatusActivity if it is in Background
		Intent configStatusActivityintent = new Intent(getApplicationContext(), ConfigurationStatusActivity.class);
		configStatusActivityintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		configStatusActivityintent.putExtras(event.getIntent().getExtras());
		getApplicationContext().startActivity(configStatusActivityintent);
		NFCCommonUtils.setNFCLock(getApplicationContext(), false);
	}

	protected void sendBroadcastToConfigurators(NFCFeature featureRequest) {
		Intent featureConfigurationRequestIntent = new Intent("com.dashwire.feature.intent.CONFIGURE");
		featureConfigurationRequestIntent.putExtra("featureId", featureRequest.getFeatureId());
		featureConfigurationRequestIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		featureConfigurationRequestIntent.putExtra("isFirstLaunch", false);
		getApplicationContext().sendBroadcast(featureConfigurationRequestIntent, "com.dashwire.config.permission.CONFIG");
		Log.v(TAG, "broadcasted com.dashwire.feature.intent.CONFIGURE : " + featureRequest.getFeatureId());
	}
	
	class DelayedPlayer implements Runnable {
		private NFCFeature featureEvent;

		public DelayedPlayer(NFCFeature event) {
			this.featureEvent = event;
		}

		public void run() {
			if (this.featureEvent.getFeatureName().equalsIgnoreCase("Ringtone") || this.featureEvent.getFeatureName().equalsIgnoreCase("Notification"))
			{
				RingtonePlayer.playRingtone(getApplicationContext(), this.featureEvent.getFeatureData());
			}
		}
	}
}
