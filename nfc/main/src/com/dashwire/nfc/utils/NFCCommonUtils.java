package com.dashwire.nfc.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class NFCCommonUtils {
	
	private static final String TAG = "NFCCommonUtils";

	public static boolean getNFCLock(Context context)
	{
		Log.v(TAG,"NFCLock context = " + context.getPackageName());
		SharedPreferences settings = context.getSharedPreferences("NFC", Context.MODE_PRIVATE);
		return settings.getBoolean("NFCLock", false);
	}
	
	public static void setNFCLock(Context context, boolean lock)
	{
		Log.v(TAG,"NFCLock context package = " + context.getPackageName());
		SharedPreferences settings = context.getSharedPreferences("NFC", Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putBoolean("NFCLock", lock);
		editor.commit();
	}

}
