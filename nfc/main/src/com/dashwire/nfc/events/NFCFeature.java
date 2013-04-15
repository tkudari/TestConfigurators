package com.dashwire.nfc.events;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dashwire.nfc.R;

import android.content.Intent;
import android.util.Log;

public class NFCFeature {
	private String featureId;
	private String featureName;
	private String featureData;
	public int featureConfigStatus = IN_PROGRESS;
	public String featureResult;
	public String failedReason;
	private Intent intent;

	public static Map<String, Integer> sNFCFeaturesNames = new HashMap<String, Integer>();
	static {
		sNFCFeaturesNames.put("Wallpaper", R.string.wallpaper);
		sNFCFeaturesNames.put("Ringtone", R.string.ringtone);
		sNFCFeaturesNames.put("Notification", R.string.notification);
		sNFCFeaturesNames.put("Shortcut", R.string.shortcut);
		sNFCFeaturesNames.put("Widget", R.string.widget);
		sNFCFeaturesNames.put("InvalidFeature", R.string.invalidFeature);
		sNFCFeaturesNames = Collections.unmodifiableMap(sNFCFeaturesNames);
	}

	public static final int IN_PROGRESS = 0;
	public static final int SUCCESS = 1;
	public static final int FAILED = 2;
	private static final String TAG = "NFCFeature";

	public NFCFeature(Intent intent) {
		this.featureId = intent.hasExtra("featureId") ? intent.getExtras().getString("featureId") : null;
		this.featureName = intent.hasExtra("featureName") ? intent.getExtras().getString("featureName") : null;
		this.featureData = intent.hasExtra("featureData") ? intent.getExtras().getString("featureData") : null;
		this.featureResult = intent.hasExtra("result") ? intent.getExtras().getString("result") : null;
		this.failedReason = intent.hasExtra("reason") ? intent.getExtras().getString("reason") : null;
		if (this.featureResult != null) {
			if (this.featureResult.equalsIgnoreCase("Success")) {
				this.featureConfigStatus = SUCCESS;
			} else if (this.featureResult.equalsIgnoreCase("failed")) {
				this.featureConfigStatus = FAILED;
			}
		}
		this.intent = intent;
	}

	public NFCFeature(String nfcTagData) {
		try {
			JSONObject nfcFeatureJSON = new JSONObject(nfcTagData);
			this.featureId = nfcFeatureJSON.has("featureId") ? nfcFeatureJSON.getString("featureId") : null;
			this.featureName = nfcFeatureJSON.has("featureName") ? nfcFeatureJSON.getString("featureName") : null;
		} catch (JSONException e) {
			Log.e(TAG, "JSONException : " + e.getMessage());
		}
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
		if (this.intent.hasExtra("featureId")) {
			this.intent.removeExtra(featureId);
		}
		this.intent.putExtra("featureId", featureId);
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
		if (this.intent.hasExtra("featureName")) {
			this.intent.removeExtra(featureName);
		}
		this.intent.putExtra("featureName", featureName);
	}

	public void setFeatureData(String featureData) {
		this.featureData = featureData;
		if (this.intent.hasExtra("featureData")) {
			this.intent.removeExtra(featureData);
		}
		this.intent.putExtra("featureData", featureData);
	}

	public String getFeatureId() {
		return this.featureId;
	}

	public String getFeatureName() {
		// Calling correctFeatureName() to change the featureName to
		// "Notificaiton" if the type is "sms"
		correctFeatureName();
		return this.featureName;
	}

	public String getFeatureData() {
		return this.featureData;
	}

	public Intent getIntent() {
		return this.intent;
	}

	public boolean isValidNFCFeature() {
		if (this.featureId.startsWith("dashwall_")) {
			if (sNFCFeaturesNames.containsKey(this.featureName)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void correctFeatureName() {
		if (this.featureName != null) {
			if (this.featureName.equalsIgnoreCase("Ringtone") && this.featureData != null) {
				try {
					JSONArray featureDataArray = new JSONArray(this.featureData);
					JSONObject featureJSON = featureDataArray.getJSONObject(0);
					if (featureJSON.getString("type").equalsIgnoreCase("call")) {
						this.featureName = "Ringtone";
					} else if (featureJSON.getString("type").equalsIgnoreCase("sms")) {
						this.featureName = "Notification";
					}
				} catch (JSONException e) {
					Log.e(TAG, "JSONException : " + e.getMessage());
				}
			}
		}else
		{
			this.featureName = "InvalidFeature";
		}
	}
}
