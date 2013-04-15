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

public class TestingDashWallAllWidget extends AndroidTestCase {

	private final String TAG = "FctnlTestLog";
	private FeaturesTestingControllerService featuresTestingControllerService;
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
	
	void doBindFeatureControllerService(Context context) {
		Log.v(TAG, "Entering doBindFeatureControllerService.");

		Intent featureControllerIntent = new Intent(context, FeaturesTestingControllerService.class);
		context.bindService(featureControllerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Before
	public void setUp() throws Exception {

		context = getContext();

		Log.v(TAG, "About to bind featuresTestingControllerService");
		doBindFeatureControllerService(context);
		Thread.sleep(2000);
	}
	
	@UiThreadTest
	public void testDashwallAllWidget() {
		try{
		
		if(featuresTestingControllerService.isFeaturesTestingControllerReady())
		{
			featuresTestingControllerService.testFeature("dashwall_51", null, null, null);
			featuresTestingControllerService.testFeature("dashwall_52", null, null, null);
			featuresTestingControllerService.testFeature("dashwall_53", null, null, null);
		}

		featuresTestingControllerService.waitTillFeaturesTestingControllerComplete();
		
		assertTrue(featuresTestingControllerService.getFailedFeaturesQueueLength() == 0);
		assertTrue(featuresTestingControllerService.getSuccessFeaturesQueueLength() >= 2);
		} finally {
			featuresTestingControllerService.finishTestRun();
		}
	}
}
