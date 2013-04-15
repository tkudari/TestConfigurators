package com.dashwire.testing.helper.ui;

import java.nio.charset.Charset;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;

import com.dashwire.testing.helper.R;

public class TestingNFCIntentActivity extends Activity {

	private static final String TAG = "TestProject";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_testing_nfc_intent_main);
		Intent activityIntent = getIntent();
		String dashWallConfig = activityIntent.getStringExtra("dashWallIntnet");
		Log.v(TAG,"dashWallConfig = " + dashWallConfig);
		try {
			NdefRecord[] records = { createNdefRecordAppJSON(dashWallConfig)};
			NdefMessage message = new NdefMessage(records);

			Intent intent = new Intent("android.nfc.action.NDEF_DISCOVERED");
			intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, new NdefMessage[] { message });
			intent.setType("application/json");
			startActivity(intent);

		} catch (ActivityNotFoundException e) {
			// handle exception, no broadcast recievers
			Log.e(TAG, "ActivityNotFoundException  : " + e.getMessage());
		} 
		finish();
	}

	private NdefRecord createNdefRecordAppJSON(String text) {

		Log.d(TAG, "createNdefRecord()");

		NdefRecord recordNFC = null;

		try {
			String mimeType = "application/json";
			byte[] mimeBytes = mimeType.getBytes(Charset.forName("UTF-8"));
			byte[] configBytes = text.getBytes(Charset.forName("UTF-8"));
			recordNFC = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], configBytes);

		} catch (Exception e) {

			Log.d(TAG, "Exception2" + e.toString());

		}
		return recordNFC;
	}

}
