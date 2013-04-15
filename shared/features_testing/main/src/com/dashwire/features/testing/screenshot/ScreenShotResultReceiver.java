package com.dashwire.features.testing.screenshot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class ScreenShotResultReceiver extends BroadcastReceiver {

	private static final String TAG = "ScreenShotResultReceiver";

	@Override
	public final void onReceive(Context context, Intent intent) {
		Log.v(TAG, "Inside ScreenShotResultReceiver");

		String action = intent.getAction();
		if (action.equals("com.dashwire.screenshot.success")) {
			ScreenShotProcess.onScreenShotSuccess(context);
			setScreenShotActionCompleted(context);
		}
	}
	
	public static void setScreenShotActionCompleted(Context context)
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = settings.edit();
		editor.putString("screenShotAction", "COMPLETED");
		editor.commit();
	}
}
