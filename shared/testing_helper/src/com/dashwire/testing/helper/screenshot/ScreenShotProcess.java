package com.dashwire.testing.helper.screenshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import com.dashwire.testing.helper.utils.TestingHelperUtils;

public class ScreenShotProcess {

	private static final String TAG = "ScreenShotProcess";
	
	public static void setScreenShotActionStarted(Context context, String featureName)
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
			TestingHelperUtils.moveFiles(fromPath, toPath);
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
	
	public static String getScreenShotActionStatus(Context context)
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getString("screenShotAction", null);
	}
	
	public static void createExternalStoragePathFile(Context context) {
		try {
			final String screenShotsPath = "device.external.storage.screenshot=" + Environment.getExternalStorageDirectory() + File.separator + "Pictures"
					+ File.separator + "Screenshots";
			String screenShotsPathFile = "screenshotspath.properties";

			Log.v(TAG, "ScreenShotsPath = " + screenShotsPath);
			Log.v(TAG, "ScreenShotsPathFile = " + screenShotsPathFile);

			/*
			 * We have to use the openFileOutput()-method the ActivityContext
			 * provides, to protect your file from others and This is done for
			 * security-reasons. We chose MODE_WORLD_READABLE, because we have
			 * nothing to hide in our file
			 */
			FileOutputStream fOut;

			fOut = context.openFileOutput(screenShotsPathFile, context.MODE_WORLD_READABLE);

			OutputStreamWriter osw = new OutputStreamWriter(fOut);

			// Write the string to the file
			osw.write(screenShotsPath);

			/*
			 * ensure that everything is really written out and close
			 */
			osw.flush();
			osw.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "createExternalStoragePathFile FileNotFoundException : " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "createExternalStoragePathFile IOException : " + e.getMessage());
		}
	}

}
