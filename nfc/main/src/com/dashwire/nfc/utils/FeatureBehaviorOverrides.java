package com.dashwire.nfc.utils;

import com.dashwire.nfc.R;
import com.dashwire.nfc.events.NFCFeature;

import android.content.Context;


public class FeatureBehaviorOverrides {
	private static final String NO_SPACE_WIDGET = "NO_SPACE_WIDGET";
    private static final String NO_SPACE_SHORTCUT = "NO_SPACE_SHORTCUT";


	public static boolean needsCustomErrorMessage(String featureName) {		
		if (featureName.equals("Widget") || featureName.equals("Shortcut")) {
			return true;
		}		
		return false;
	}

	public static String getCustomErrorMessage(Context context,String featureName, String failedReason) {
		if (failedReason.equals(NO_SPACE_WIDGET) || failedReason.equals(NO_SPACE_SHORTCUT)) {
			return context.getResources().getString(R.string.custom_error_msg_ws); 
		} 
		//return the default error message:
		return context.getString(R.string.failed_detail1) + " " + context.getString(NFCFeature.sNFCFeaturesNames.get(featureName))
				+ "\n" + failedReason;
		}	
}
