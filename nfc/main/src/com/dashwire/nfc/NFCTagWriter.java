package com.dashwire.nfc;

import java.io.IOException;
import java.nio.charset.Charset;

import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.util.Log;

public class NFCTagWriter
{
	 private static final String TAG = NFCTagWriter.class.getSimpleName();

	    public void writeTag(Tag tag, String tagText) {
	        MifareUltralight ultralight = MifareUltralight.get(tag);
	        try {
	            ultralight.connect();
	            ultralight.writePage(4, "abcd".getBytes(Charset.forName("US-ASCII")));
	            ultralight.writePage(5, "efgh".getBytes(Charset.forName("US-ASCII")));
	            ultralight.writePage(6, "ijkl".getBytes(Charset.forName("US-ASCII")));
	            ultralight.writePage(7, "mnop".getBytes(Charset.forName("US-ASCII")));
	        } catch (IOException e) {
	            Log.e(TAG, "IOException while closing MifareUltralight...", e);
	        } finally {
	            try {
	                ultralight.close();
	            } catch (IOException e) {
	                Log.e(TAG, "IOException while closing MifareUltralight...", e);
	            }
	        }
	    }

	    public String readTag(Tag tag) {
	        MifareUltralight mifare = MifareUltralight.get(tag);
	        try {
	            mifare.connect();
	            byte[] payload = mifare.readPages(4);
	            return new String(payload, Charset.forName("US-ASCII"));
	        } catch (IOException e) {
	            Log.e(TAG, "IOException while writing MifareUltralight message...", e);
	        } finally {
	            if (mifare != null) {
	               try {
	                   mifare.close();
	               }
	               catch (IOException e) {
	                   Log.e(TAG, "Error closing tag...", e);
	               }
	            }
	        }
	        return null;
	    }

}
