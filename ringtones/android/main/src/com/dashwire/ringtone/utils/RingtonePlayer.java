package com.dashwire.ringtone.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

public class RingtonePlayer {
	
	private static final String TAG = "RingtonePlayer";

	public static void playRingtone(Context context, String featureData)
	{
		try
		{
			Log.v(TAG,"featureData = " + featureData);
			JSONArray ringtoneArray = new JSONArray(featureData);
			JSONObject ringtone = (JSONObject) ringtoneArray.get(0);
			Ringtone ringTone = RingtoneManager.getRingtone(context,Uri.parse(ringtone.getString("uri")));
			ringTone.play();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Log.e(TAG,"InterruptedException : " + e.getMessage());
			}
			ringTone.stop();
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

}
