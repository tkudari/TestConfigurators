package com.dashwire.homeItem;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;


import android.content.IntentFilter;
import android.content.res.Resources.Theme;
import android.util.Log;

import com.dashwire.configurator.ConfigurationRequestReceiver;
import com.dashwire.configurator.ConfiguratorConstants;
import com.dashwire.homeItem.HomeItemConfigurator;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowContext;
import com.xtremelabs.robolectric.shadows.ShadowIntentFilter;
import com.xtremelabs.robolectric.util.Transcript;

@RunWith(CustomHomeItemTestRunner.class)
public class HomeItemConfiguratorTest {
	
	private HomeItemConfigurator homeItemIntentService;
	private Activity activity;
    public Transcript transcript;
    private ContextWrapper contextWrapper ;
	DWShadowIntentFilter shadowIntentFilter ;
	IntentFilter filter = new IntentFilter();


	
	@Before
	public void setUp() throws Exception
	{
		homeItemIntentService = new HomeItemConfigurator();
		activity = new Activity();
        transcript = new Transcript();
        contextWrapper = new ContextWrapper(activity);
		shadowIntentFilter = Robolectric.shadowOf_(filter);

	}

	@Test
	public void homeItemConfiguratorAddsResultFilter() {
		JSONArray testArray = createTestArray();		
		
		homeItemIntentService.injectFeatureJSONArray(testArray);
		homeItemIntentService.onConfigFeature();
		
        Assert.assertEquals("com.dashwire.launcher.CONFIGURE_HOME_ITEMS_RESULT", shadowIntentFilter.getLastAction());		
	}
	
	@Test
	public void homeItemConfiguratorRegistersReceiver() {
		JSONArray testArray = createTestArray();
		
		
		
		ConfigurationRequestReceiver receiver = broadcastReceiver("WidgetResultReceiver");
        contextWrapper.registerReceiver(receiver, intentFilter("com.dashwire.launcher.CONFIGURE_HOME_ITEMS"));
        
        homeItemIntentService.injectFeatureJSONArray(testArray);
		homeItemIntentService.onConfigFeature();
		//TODO: in progress:
//        transcript.assertEventsSoFar("WidgetResultReceiver notified of " + "com.dashwire.launcher.CONFIGURE_HOME_ITEMS");		
		
	}
	
	private JSONArray createTestArray() {
		JSONArray testArray = new JSONArray();
		JSONObject obj = new JSONObject();
		try {
		obj.put("id", "com.google.android.apps.maps/com.google.googlenav.appwidget.traffic.TrafficAppWidget");
		obj.put("screen", 0);
		obj.put("x", 0);
		obj.put("y", 0);
		obj.put("rows", 2);
		obj.put("cols", 2);
		obj.put("container_id", 1);
		obj.put("category", "widgets");
		obj.put("title", "TestTitle");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		testArray.put(obj);
		return testArray;
	}

	
	
	
	
	 private ConfigurationRequestReceiver broadcastReceiver(final String name) {
	        return new ConfigurationRequestReceiver() {
	            @Override
	            public void onReceive(Context context, Intent intent) {
	                transcript.add(name + " notified of " + intent.getAction());
	            }
	        };
	    }

	 private IntentFilter intentFilter(String... actions) {
	        IntentFilter intentFilter = new IntentFilter();
	        for (String action : actions) {
	        	intentFilter.addAction(action);
	        }
	        return intentFilter;
	    }
	 
	 
}
