package com.dashwire.features.testing.fctnl.test;

import java.lang.reflect.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.test.AndroidTestCase;
import android.test.ServiceTestCase;
import android.test.UiThreadTest;
import android.util.Log;

import com.dashwire.features.testing.controller.FeaturesTestingControllerService;
import com.dashwire.testing.helper.controller.TestingHelperService;

public class TestingAllRingtonesTest extends AndroidTestCase {

	private final String TAG = "FctnlTestLog";
	private FeaturesTestingControllerService featuresTestingControllerService;
	private TestingHelperService testingHelperService;
	private Context context;


	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, "Entering onServiceConnected.");
			if (featuresTestingControllerService != null) {
				Log.e(TAG, "featuresTestingControllerService not null before onServiceConnected");
			}
			featuresTestingControllerService = ((FeaturesTestingControllerService.FeaturesTestingControllerBinder) service).getService();
			if (featuresTestingControllerService != null) {
				Log.v(TAG, "featuresTestingControllerService connected");
			} else {
				Log.v(TAG, "Error on connecting featuresTestingControllerService");
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			Log.d(TAG, "Entering onServiceDisconnected.");
			if (featuresTestingControllerService != null) {
				featuresTestingControllerService = null;
			}
			Log.v(TAG, "featuresTestingControllerService disconnected");
		}
	};
	
	private ServiceConnection testingHelperServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, "Entering onServiceConnected.");
			if (testingHelperService != null) {
				Log.e(TAG, "testingHelperService not null before onServiceConnected");
			}
			testingHelperService = ((TestingHelperService.TestingHelperBinder) service).getService();
			if (testingHelperService != null) {
				Log.v(TAG, "testingHelperService connected");
			} else {
				Log.v(TAG, "Error on connecting testingHelperService");
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			Log.d(TAG, "Entering onServiceDisconnected.");
			if (testingHelperService != null) {
				testingHelperService = null;
			}
			Log.v(TAG, "testingHelperService disconnected");
		}
	};
	
	void doBindFeatureControllerService(Context context) {
		Log.v(TAG, "Entering doBindFeatureControllerService.");

		Intent featureControllerIntent = new Intent(context, FeaturesTestingControllerService.class);
		context.bindService(featureControllerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	void doBindTestingHelperService(Context context) {
		Log.v(TAG, "Entering doBindTestingHelperService.");

		Intent featureControllerIntent = new Intent(context, TestingHelperService.class);
		context.bindService(featureControllerIntent, testingHelperServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Before
	public void setUp() throws Exception {

		context = getContext();

		Log.v(TAG, "About to bind featuresTestingControllerService");
		doBindFeatureControllerService(context);
		doBindTestingHelperService(context);
		Thread.sleep(2000);
	}

	@UiThreadTest
	public void testAllWallpapers() {
		String featureAssetValue = "ringtones";
		String featureName = "Ringtone";
		String screenToTest = "android.settings.SOUND_SETTINGS";
		JSONArray featureConfigs = testingHelperService.getAssetForFeature(getTestContext(), featureAssetValue);
		testingFeatures(featureAssetValue, featureName, screenToTest, featureConfigs, "Positive");
	}

	@UiThreadTest
	public void testingFeatures(String featureAssetValue, String featureName, String screenToTest, JSONArray featureConfigs, String testType) {
		int totalFeaturesToTest = 0;
		if ("Negative".equalsIgnoreCase(testType))
		{
			totalFeaturesToTest = 1;
		}else
		{
			totalFeaturesToTest = featureConfigs.length();
		}
		try {
			if (featureConfigs != null && featuresTestingControllerService.isFeaturesTestingControllerReady()) {
				for (int i = 0; i < totalFeaturesToTest; i++) {
					String featureId;
					if (featureConfigs.getJSONObject(i).has("uri")) {
						featureId = featureConfigs.getJSONObject(i).getString("uri");
					} else {
						featureId = "NotYetDefined";
					}
					JSONArray featureDataJSONArray = new JSONArray();
					featureDataJSONArray.put(featureConfigs.getJSONObject(i));
					String featureData = featureDataJSONArray.toString();

					Log.v(TAG, "featureId = " + featureId);
					Log.v(TAG, "featureName = " + featureName);
					Log.v(TAG, "featureData = " + featureData);
					Log.v(TAG, "screenToTest = " + screenToTest);

					featuresTestingControllerService.testFeature(featureId, featureName, featureData, screenToTest);
				}

				Log.v(TAG, "Waiting Till features get configured");
				featuresTestingControllerService.waitTillFeaturesTestingControllerComplete();

				Log.v(TAG, "Asserting results");
				if ("Positive".equalsIgnoreCase(testType)) {
					Log.v(TAG, "Positive");
					Log.v(TAG, "Failed Queue Length = " + featuresTestingControllerService.getFailedFeaturesQueueLength());
					Log.v(TAG, "Success Queue Length = " + featuresTestingControllerService.getSuccessFeaturesQueueLength());
					assertTrue(featuresTestingControllerService.getFailedFeaturesQueueLength() == 0);
					assertTrue(featuresTestingControllerService.getSuccessFeaturesQueueLength() > 0);
				} else if ("Negative".equalsIgnoreCase(testType)) {
					Log.v(TAG, "Negative");
					Log.v(TAG, "Failed Queue Length = " + featuresTestingControllerService.getFailedFeaturesQueueLength());
					Log.v(TAG, "Success Queue Length = " + featuresTestingControllerService.getSuccessFeaturesQueueLength());
					assertTrue(featuresTestingControllerService.getFailedFeaturesQueueLength() > 0);
					assertTrue(featuresTestingControllerService.getSuccessFeaturesQueueLength() == 0);
				}
				
			} else
			{
				fail("Feature Configs are not gathered");
			}
		} catch (JSONException e) {
			Log.e(TAG, "JSONException in testingFeatures = " + e.getMessage());
		} finally {
			featuresTestingControllerService.finishTestRun();
		}
	}

	
	private Context getTestContext() {
		try {
			Method getTestContext = ServiceTestCase.class.getMethod("getTestContext");
			return (Context) getTestContext.invoke(this);
		} catch (final Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}
}
