package com.dashwire.features.testing.fctnl.test;

import java.lang.reflect.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.test.AndroidTestCase;
import android.test.ServiceTestCase;
import android.test.UiThreadTest;
import android.util.Log;

import com.dashwire.testing.helper.controller.TestingHelperService;

public class TestingNFCTest extends AndroidTestCase {

	private final String TAG = "TestingNFCTest";
	private TestingHelperService testingHelperService;
	private String testingFeatureId;
	private String testingFeatureName;
	private BroadcastReceiver configurartorRequestMockReceiver = null;
	private Context context;

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

	void doBindTestingHelperService(Context context) {
		Log.v(TAG, "Entering doBindTestingHelperService.");

		Intent featureControllerIntent = new Intent(context, TestingHelperService.class);
		context.bindService(featureControllerIntent, testingHelperServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Before
	public void setUp() throws Exception {

		context = getContext();

		Log.v(TAG, "About to bind TestingControllerService");
		doBindTestingHelperService(context);

		Thread.sleep(3000);
	}

	@UiThreadTest
	public void testToTransferDashWallNFCIntentToConfiguratorIntent() {
		try {
			configurartorRequestMockReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent configRequestIntent) {
					Log.v(TAG, "ConfigurartorRequestMockReceiver.");
					Log.v(TAG, "action = " + configRequestIntent.getAction());
					Log.v(TAG, "FeatureId = " + configRequestIntent.getStringExtra("featureId"));
					assertEquals(testingFeatureId, configRequestIntent.getStringExtra("featureId"));

					getContext().unregisterReceiver(configurartorRequestMockReceiver);
				}
			};

			getContext().registerReceiver(configurartorRequestMockReceiver, new IntentFilter("com.dashwire.feature.intent.CONFIGURE"));
			String featureAssetValue = "dashWallRingtone";
			JSONArray featureConfigs = testingHelperService.getAssetForFeature(getTestContext(), featureAssetValue);
			JSONObject featureJSON;
			featureJSON = featureConfigs.getJSONObject(0);

			testingFeatureId = featureJSON.getString("featureId");
			testingFeatureName = featureJSON.getString("featureName");
			Log.v(TAG,"testingFeatureId = " + testingFeatureId);
			Log.v(TAG,"testingFeatureName = " + testingFeatureName);
			Intent testNFCActivityIntent = new Intent("com.dashwire.testing.NFC");
			testNFCActivityIntent.putExtra("dashWallIntnet", featureConfigs.getJSONObject(0).toString());
			testNFCActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getContext().startActivity(testNFCActivityIntent);
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JSONException e) {
			Log.e(TAG, "JSONException : " + e.getMessage());
		}
		// }
		// catch (Exception e) {
		// Log.e(TAG, "Exception : " + e.getMessage());
		// }
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
