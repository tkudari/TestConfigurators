package com.dashwire.homeItem;


import android.app.Activity;
import android.content.Context;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Implements(android.content.IntentFilter.class)
public class DWShadowIntentFilter {
	
	private static String lastAction;
		
		 
	@Implementation
	public void addAction(String action) {
		System.out.println("addAction called... setting action = " + action);
		lastAction = action;
	}

	
	public String getLastAction() {
		return lastAction;
	}	

}
