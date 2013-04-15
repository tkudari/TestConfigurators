package com.dashwire.configurator;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.dashwire.base.debug.DashLogger;

public class ConfigurationRequestReceiver extends BroadcastReceiver
{

	private static final String TAG = ConfigurationRequestReceiver.class.getCanonicalName();

	@Override
	public void onReceive(Context context, Intent intent)
	{
        DashLogger.d(TAG, "onReceive called with intent " + intent.toString());
        Intent configuratorControllerIntent = new Intent("com.dashwire.configurator.controller.service");
        configuratorControllerIntent.putExtras(intent.getExtras());
        context.startService(configuratorControllerIntent);
    }
}
