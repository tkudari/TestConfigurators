package com.dashwire.nfc.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;

public class NFCTriggerActivity extends Activity {

	private static final String TAG = "NFCConfigureActivity";

	@Override
	protected void onResume() {
		super.onResume();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			NdefMessage[] nfcTagMessage = getNdefMessages(getIntent());
			byte[] payload = nfcTagMessage[0].getRecords()[0].getPayload();
			Intent nfcControllerIntent = new Intent("com.dashwire.nfc.controller.service");
			nfcControllerIntent.putExtra("NFCTagData", new String(payload));
			if (isJSONValid(new String(payload)))
			{
				getApplicationContext().startService(nfcControllerIntent);
			}
		}
		finish();
	}

	NdefMessage[] getNdefMessages(Intent intent) {
		// Parse the intent
		NdefMessage[] msgs = null;
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		} else {
			Log.d(TAG, "Unknown intent.");
		}
		return msgs;
	}
	
	public boolean isJSONValid(String test)
	{
	    boolean valid = false;
	    try {
	        new JSONObject(test);
	        valid = true;
	    }
	    catch(JSONException ex) { 
	        valid = false;
	    }
	    return valid;
	}
}
