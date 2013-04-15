package com.dashwire.testing.helper.verifier;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

public class RingtoneVerifier {
	
	public enum ringToneType {
		 CALL, SMS;
	}

	
/***
 * Returns the device's default ringtone title	
 * @param context
 * @param ringType	The ringtone type as specified on testingHelper.ringToneType (CALL or SMS);
 * @return String containing default ringtone title.
 */
	
	public static String getDefaultRingTone(Context context, ringToneType ringType){
		String theRingTone = "";
		Uri rtUri = null;
		
		if (ringType == ringToneType.CALL){
			
			rtUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
			Log.d("QA test", "Get rintone");
			
		}else if (ringType == ringToneType.SMS){
			
			rtUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
			Log.d("QA test", "Get notification");
			
		}
		
		Ringtone ringTone=RingtoneManager.getRingtone(context, rtUri);
		theRingTone = ringTone.getTitle(context);
		ringTone.play();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ringTone.stop();
		Log.d("QA test", "Default  Title = " + theRingTone);
		return theRingTone;
		
	}
	
/***
 * Returns a Cursor object with device's ringtones info.  DOES NOT use same info as asset.json
 * 	
 * @param context
 * @param callOrSms
 * @return
 */
	private static Cursor getDeviceRingTones(Context context, ringToneType callOrSms){
		
		RingtoneManager ringtMngr = new RingtoneManager(context);
		
		if (callOrSms == ringToneType.CALL)
			ringtMngr.setType(RingtoneManager.TYPE_RINGTONE);
		else
			ringtMngr.setType(RingtoneManager.TYPE_NOTIFICATION);
		
		Cursor ringCurson = ringtMngr.getCursor();	
		ringCurson.moveToFirst();
		
		while (ringCurson.moveToNext()){
			int i = ringCurson.getPosition();
			Ringtone rt = ringtMngr.getRingtone(i);
			Log.d("QA test", "Ringtone title [" + i +"] =" + rt.getTitle(context));
			
			Log.d("QA test", "Rintone Uri [" + i +"] =" + ringtMngr.getRingtoneUri(i).getPath());
		}
			
		return ringCurson;
		
	}	

}
