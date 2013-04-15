package com.dashwire.features.testing.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dashwire.features.testing.events.FeatureTesting;
import com.dashwire.features.testing.events.FeatureTestingFailedEvent;
import com.dashwire.features.testing.events.FeatureTestingRequestEvent;
import com.dashwire.features.testing.events.FeatureTestingSuccessEvent;
import com.dashwire.features.testing.utils.BusProvider;
import com.dashwire.features.testing.utils.FeatureUtils;
import com.squareup.otto.Subscribe;

public class FeaturesTestingControllerService extends Service {

	private static final String TAG = "FeaturesTestingControllerService";
	private final IBinder binder = new FeaturesTestingControllerBinder();
	private Context context;
	private String featuresTestingControllerStatus;
	private LinkedList<FeatureTesting> newFeaturesQueue;
	private LinkedList<FeatureTesting> successFeaturesQueue;
	private LinkedList<FeatureTesting> failedFeaturesQueue;

	private Map<String, String> featureScreenToTestMapping = new HashMap<String, String>();

	public class FeaturesTestingControllerBinder extends Binder {
		public FeaturesTestingControllerService getService()
		{
			return FeaturesTestingControllerService.this;
		}
	}

	@Override
	public void onCreate()
	{
		Log.v(TAG, "onCreate");
		super.onCreate();
		context = getApplicationContext();
		init();
		BusProvider.getInstance().register(this);
		Log.v(TAG, "BusProvider is registered");
	}

	@Override
	public void onDestroy()
	{
		Log.v(TAG, "onDestroy of " + TAG + "called");
		super.onDestroy();
		BusProvider.getInstance().unregister(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.v(TAG, TAG + " on start command");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return binder;
	}

	private void init()
	{
		featuresTestingControllerStatus = "INIT";
		newFeaturesQueue = new LinkedList<FeatureTesting>();
		successFeaturesQueue = new LinkedList<FeatureTesting>();
		failedFeaturesQueue = new LinkedList<FeatureTesting>();
	}

	public void requestFeatureConfig(FeatureTestingRequestEvent event)
	{
		Log.v(TAG, "requestFeatureConfig");
		newFeaturesQueue.add(event);
	}
	
	public void testFeature(String featureId, String featureName, String featureData, String screenToTest) {
	 	FeatureTestingRequestEvent featureTestingRequest;
    	if (screenToTest != null)
    	{
    		featureTestingRequest = new FeatureTestingRequestEvent(featureId, featureName, featureData, screenToTest, null);
    	}else
    	{
    		featureTestingRequest = new FeatureTestingRequestEvent(featureId, featureName, featureData, null, null);
    	}
    	newFeaturesQueue.add(featureTestingRequest);
    	startFeaturesTestingProcess();
	}

	public String getFeatureControllerStatus()
	{
		return featuresTestingControllerStatus;
	}

//	public void reset()
//	{
//		init();
//	}

	@Subscribe
	public void onFeatureTestingRequest(FeatureTestingRequestEvent event)
	{
		Log.v(TAG, "onFeatureTestingRequest");
		newFeaturesQueue.add(event);
		startFeaturesTestingProcess();
	}

	public void processFeaturesTestingQueue()
	{
		Log.v(TAG, "processFeaturesQueue newFeaturesQueue size = " + newFeaturesQueue.size());
		if (!newFeaturesQueue.isEmpty())
		{
			featuresTestingControllerStatus = "IN_PROGRESS";
			FeatureTesting feature = newFeaturesQueue.poll();
			featureScreenToTestMapping.put(feature.featureName, feature.screenToTest);

			Intent featureConfigurationRequestIntent = new Intent("com.dashwire.feature.intent.CONFIGURE");
			Log.v(TAG, "FeatureId = " + feature.featureId);
			Log.v(TAG, "featureName = " + feature.featureName);
			Log.v(TAG, "featureData = " + feature.featureData);
			Log.v(TAG, "screenToTest = " + feature.screenToTest);
			featureConfigurationRequestIntent.putExtra("featureId", feature.featureId);
			featureConfigurationRequestIntent.putExtra("featureName", feature.featureName);
			featureConfigurationRequestIntent.putExtra("featureData", feature.featureData);
			featureConfigurationRequestIntent.putExtra("HTCSenseLevel", FeatureUtils.getAndroidOSProperty(context, "ro.build.sense.version"));
			
			featureConfigurationRequestIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			context.sendBroadcast(featureConfigurationRequestIntent, "com.dashwire.config.permission.CONFIG");
			Log.v(TAG, "Broadcasted feature.intent.configure");
		} else
		{
			featuresTestingControllerStatus = "TEST_RUN_COMPLETED";
		}
	}

	public void startFeaturesTestingProcess()
	{
		if ("INIT".equalsIgnoreCase(featuresTestingControllerStatus))
		{
			processFeaturesTestingQueue();
		}
	}
	
	public void finishTestRun()
	{
		featuresTestingControllerStatus = "TEST_RUN_VALIDATED";
		init();
	}

	public int getNewFeaturesQueueLength()
	{
		if (!newFeaturesQueue.isEmpty())
		{
			Log.v(TAG,"newFeaturesQueue length = " + newFeaturesQueue.size());
			return newFeaturesQueue.size();
		} else
		{
			return 0;
		}
	}

	public int getSuccessFeaturesQueueLength()
	{
		if (!successFeaturesQueue.isEmpty())
		{
			Log.v(TAG,"successFeaturesQueue length = " + successFeaturesQueue.size());
			return successFeaturesQueue.size();
		} else
		{
			return 0;
		}
	}

	public int getFailedFeaturesQueueLength()
	{
		if (!failedFeaturesQueue.isEmpty())
		{
			Log.v(TAG,"FailedQueue length = " + failedFeaturesQueue.size());
			return failedFeaturesQueue.size();
		} else
		{
			return 0;
		}
	}

	@Subscribe
	public void onFeatureTestingSuccess(FeatureTestingSuccessEvent event)
	{
		Log.v(TAG, "onFeatureTestingSuccess");
		successFeaturesQueue.add(event);
		processFeaturesTestingQueue();
	}

	@Subscribe
	public void onFeatureTestingFailed(FeatureTestingFailedEvent event)
	{
		Log.v(TAG, "onFeatureTestingFailed");
		failedFeaturesQueue.add(event);
		processFeaturesTestingQueue();
	}

	public void waitTillFeaturesTestingControllerComplete() {
		if ("TEST_RUN_COMPLETED".equalsIgnoreCase(featuresTestingControllerStatus)) {
			return;
		} else {
			Log.v(TAG, "********Features Testing Controller is not yet Completed*********");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			Log.v(TAG, "checking again");
			waitTillFeaturesTestingControllerComplete();
		}
	}

	public boolean isFeaturesTestingControllerReady() {
		if ("INIT".equalsIgnoreCase(featuresTestingControllerStatus)) {
			return true;
		} else if ("TEST_RUN_VALIDATED".equalsIgnoreCase(featuresTestingControllerStatus)) {
			init();
			isFeaturesTestingControllerReady();
		}else{
			Log.e(TAG, "********Features Testing Controller status is " + featuresTestingControllerStatus + " and not yet Ready*******");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			isFeaturesTestingControllerReady();
		}
		return false;
	}

}
