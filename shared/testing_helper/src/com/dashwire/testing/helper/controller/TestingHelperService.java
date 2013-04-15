package com.dashwire.testing.helper.controller;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dashwire.testing.helper.mock.TestingConfigDataMocks;
import com.dashwire.testing.helper.mock.TestingAssetGatherer;
import com.dashwire.testing.helper.screenshot.ScreenShotProcess;
import com.dashwire.testing.helper.screenshot.ScreenShotRequest;

public class TestingHelperService extends Service {

	private static final String TAG = "TestingHelperService";
	private final IBinder binder = new TestingHelperBinder();
	private Context context;
	private Context testContext;
	private static JSONArray featureJSONArray = null;
	private static int retryCount = 0;
	private LinkedList<ScreenShotRequest> screenShotQueue;

	public class TestingHelperBinder extends Binder {
		public TestingHelperService getService() {
			return TestingHelperService.this;
		}
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();
		context = getApplicationContext();
		screenShotQueue = new LinkedList<ScreenShotRequest>();
		TestingAssetGatherer.startAssetGatherer(context);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, TAG + " on start command");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public JSONArray getAssetForFeature(Context testContext, String feature) {
		Log.v(TAG, "Gathering Configs for Feature : " + feature);
		JSONArray featureJSONArray = TestingConfigDataMocks.loadFeatureConfigDataMocks(testContext, feature);
		if (featureJSONArray != null && featureJSONArray.toString().length()>0)
		{
			return featureJSONArray;
		}else
		{
			return getFeatureConfigFromAsset(context, feature);
		}
	}

	public void takeScreenShot(String screenToTest, String featureName) {
		ScreenShotRequest ssr = new ScreenShotRequest(screenToTest, featureName);
		screenShotQueue.add(ssr);
		processScreenShotQueue();
		waitTillScreenShotProcessCompletes(context);
	}

	public JSONArray getFeatureConfigFromAsset(Context context, String feature) {
		retryCount++;
		if ((TestingAssetGatherer.getGatheredAsset(context) != null)) {
			try {
				JSONObject gatheredAssetJSON = new JSONObject(TestingAssetGatherer.getGatheredAsset(context));
				Log.v(TAG, "Checking " + feature + " available in gatheredAsset");
				if (gatheredAssetJSON.has(feature)) {
					featureJSONArray = gatheredAssetJSON.getJSONArray(feature);
				} else {
					Log.e(TAG, feature + " is not available in gatheredAsset.");
				}
				return featureJSONArray;
			} catch (JSONException e) {
				Log.e(TAG, "JSONException in gatherFeatureConfigs = " + e.getMessage());
			}
		} else {
			Log.v(TAG, "********Assets are not gathered yet*********");
			if (retryCount <= 30) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
				getFeatureConfigFromAsset(context, feature);
			}
		}
		return featureJSONArray;
	}

	public void processScreenShotQueue() {
		if (!screenShotQueue.isEmpty()) {
			Log.v(TAG, "screenshot status = " + ScreenShotProcess.getScreenShotActionStatus(context));
			if (ScreenShotProcess.getScreenShotActionStatus(context) == null
					|| "COMPLETED".equalsIgnoreCase(ScreenShotProcess.getScreenShotActionStatus(context))) {
				ScreenShotRequest ssr = screenShotQueue.poll();
				Log.v(TAG, "takeScreenShot : " + ssr.screenToTake);
				ScreenShotProcess.createExternalStoragePathFile(context);
				startScreenShotAction(context, ssr.screenToTake);
				ScreenShotProcess.setScreenShotActionStarted(context, ssr.featureName);
			} else {
				Log.v(TAG, "Screen shot process not ready wait for 2 secs and check again....");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				processScreenShotQueue();
			}
		} else {
			return;
		}
		processScreenShotQueue();
	}

	private static boolean waitTillScreenShotProcessCompletes(Context context) {
		if ((ScreenShotProcess.getScreenShotActionStatus(context) != null)) {
			if ("COMPLETED".equalsIgnoreCase(ScreenShotProcess.getScreenShotActionStatus(context))) {
				return true;
			} else if ("STARTED".equalsIgnoreCase(ScreenShotProcess.getScreenShotActionStatus(context))) {
				Log.v(TAG, "********ScreenShot in progress*********");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
				waitTillScreenShotProcessCompletes(context);
			}

		} else {
			Log.e(TAG, "ScreenShot Process NOT STARTED");
		}
		return false;
	}

	public void startScreenShotAction(Context context, String screenToTest) {
		Log.v(TAG, "startScreenShotAction : " + screenToTest);
		Intent screenShotRequestIntent = new Intent("com.dashwire.take.screenshot");
		screenShotRequestIntent.putExtra("screenAction", screenToTest);
		screenShotRequestIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		context.sendBroadcast(screenShotRequestIntent);
	}

	public static void setScreenShotActionInit(Context context) {
		Log.v(TAG, "setScreenShotActionInit : " + context.getPackageName());
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = settings.edit();
		editor.putString("screenShotAction", "INIT");
		editor.commit();
	}
}
