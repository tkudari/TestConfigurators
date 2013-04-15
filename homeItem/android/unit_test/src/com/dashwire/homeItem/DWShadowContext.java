package com.dashwire.homeItem;

import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;
import com.xtremelabs.robolectric.internal.RealObject;


@Implements(android.content.Context.class)
public class DWShadowContext {
	
	 @RealObject private Context realContext;
	
	 public void __constructor__(Context context) {

	    }

	    public void __constructor__(Activity activity) {

	    }
	
	private static HashMap<String, BroadcastReceiver> registeredReceivers = new HashMap<String, BroadcastReceiver>();
	
	@Implementation
	public Intent registerReceiver (BroadcastReceiver receiver, DWShadowIntentFilter filter) {
		registeredReceivers.put(filter.getLastAction(), receiver);
		return null;
	}
	
	@Implementation 
	public BroadcastReceiver getRegisteredReceiver (String action) {
		return registeredReceivers.get(action);
	}
}
