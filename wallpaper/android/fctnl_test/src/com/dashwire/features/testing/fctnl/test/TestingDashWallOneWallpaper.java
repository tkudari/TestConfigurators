package com.dashwire.features.testing.fctnl.test;

import org.junit.Before;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.test.AndroidTestCase;
import android.test.UiThreadTest;
import android.util.Log;

import com.dashwire.features.testing.controller.FeaturesTestingControllerService;
import com.dashwire.testing.helper.controller.TestingHelperService;

public class TestingDashWallOneWallpaper extends AndroidTestCase {

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
	public void testDashwallOneWallpaper() {
		try {
			String screenToTest = Intent.ACTION_MAIN;
			featuresTestingControllerService.testFeature("dashwall_14", null, null, screenToTest);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			assertTrue(featuresTestingControllerService.getFailedFeaturesQueueLength() == 0);
			assertTrue(featuresTestingControllerService.getSuccessFeaturesQueueLength() > 0);
		} finally {
			featuresTestingControllerService.finishTestRun();
		}
	}
}
