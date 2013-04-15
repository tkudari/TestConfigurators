package com.dashwire.features.testing.screenshot;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dashwire.features.testing.utils.FeatureUtils;

public class ScreenShotProcess {

	private static final String TAG = "ScreenShotProcess";

	private static boolean waitTillScreenShotProcessCompletes(Context context)
	{
		if ((getScreenShotActionStatus(context) != null))
		{
			if ("COMPLETED".equalsIgnoreCase(getScreenShotActionStatus(context)))
			{
				return true;
			}
			else if ("STARTED".equalsIgnoreCase(getScreenShotActionStatus(context)))
			{
				Log.v(TAG, "********ScreenShot in progress*********");
				try
				{
					Thread.sleep(2000);
				} catch (InterruptedException e)
				{
				}
				waitTillScreenShotProcessCompletes(context);
			}
				
		} else
		{
			Log.e(TAG,"ScreenShot Process NOT STARTED");
		}
		return false;
	}
	
	private static void startScreenShotAction(Context context, String screenToTest)
	{
		Log.v(TAG, "startScreenShotAction");
		Intent screenShotRequestIntent = new Intent("com.dashwire.take.screenshot");
		screenShotRequestIntent.putExtra("screenAction", screenToTest);
		screenShotRequestIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		context.sendBroadcast(screenShotRequestIntent);
	}
	
	public static void takeScreenShot(Context context, String screenToTest, String featureName)
	{
		Log.v(TAG, "takeScreenShot");
		startScreenShotAction(context, screenToTest);
		setScreenShotActionStarted(context, featureName);
		waitTillScreenShotProcessCompletes(context);
	}
	
	private static void setScreenShotActionStarted(Context context, String featureName)
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = settings.edit();
		editor.putString("screenShotAction", "STARTED");
		editor.putString("screenShotFeatureName", featureName);
		editor.commit();
	}
	
	protected static void onScreenShotSuccess(Context context)
	{
		Log.v(TAG, "onScreenShotSuccess");
		String fromPath = Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + "Screenshots";
		String toPath = fromPath + File.separator + getScreenShotFeature(context);
		try
		{
			Log.v(TAG, "fromPath = " + fromPath);
			Log.v(TAG, "toPath = " + toPath);
			FeatureUtils.moveFiles(fromPath, toPath);
		} catch (IOException e)
		{
			Log.e(TAG, "IOException - " + e.getMessage());
		}
	}
	
	private static String getScreenShotFeature(Context context)
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getString("screenShotFeatureName", null);
	}
	
	private static String getScreenShotActionStatus(Context context)
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getString("screenShotAction", null);
	}

}
