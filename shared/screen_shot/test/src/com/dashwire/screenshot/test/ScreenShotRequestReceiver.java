package com.dashwire.screenshot.test;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenShotRequestReceiver extends BroadcastReceiver {

	private static final String TAG = "ScreenShotRequestReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "onReceive of ScreenShotRequestReceiver");
		String action = intent.getAction();
		String screen_action = intent.getStringExtra("screenAction");
		if (screen_action != null) {
			Log.v(TAG,"Screen Action = " + screen_action);
			Intent screenIntent = new Intent(screen_action);
			if (screen_action.equalsIgnoreCase("android.intent.action.MAIN"))
			{
				screenIntent.addCategory(Intent.CATEGORY_HOME);
				screenIntent.addCategory(Intent.CATEGORY_MONKEY);
			}
			screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(screenIntent);
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}

		Log.v(TAG, "onReceive of ScreenShotRequestReceiver - action : " + intent.getAction());

		if (action.equals("com.dashwire.take.screenshot")) {
			context.startInstrumentation(new ComponentName("com.dashwire.screenshot.test", "android.test.InstrumentationTestRunner"), null, null);
		} else {
			Log.v(TAG, "Invalid action");
		}
	}
}
