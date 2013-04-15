package com.dashwire.wallpaper;


import java.io.File;

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
import android.net.Uri;

import com.dashwire.configurator.ConfigurationRequestReceiver;
import com.dashwire.configurator.ConfiguratorConstants;
import com.dashwire.wallpaper.WallpaperConfigurator;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.util.Transcript;

@RunWith(RobolectricTestRunner.class)
public class WallpaperConfiguratorTest {
	
	private WallpaperConfigurator wallpaperIntentService;
	private Context context;
	private Activity activity;
    public Transcript transcript;
    private ContextWrapper contextWrapper;


	
	@Before
	public void setUp() throws Exception
	{
		wallpaperIntentService = new WallpaperConfigurator();
		activity = new Activity();
		context = activity.getApplicationContext();
        contextWrapper = new ContextWrapper(activity);
        transcript = new Transcript();
	}

	@Test
	public void invalidFileUriShouldCauseFailureInSetFileWallpaper() 
	{
		Uri fileUri = Uri.fromFile(new File("a/non-existent/file"));
		JSONObject obj = new JSONObject();
		try {
			obj.put("uri", fileUri);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONArray jArray = new JSONArray().put(obj);
		wallpaperIntentService.injectFeatureJSONArray(jArray);
		wallpaperIntentService.onConfigFeatureWrapper();
		
		Assert.assertEquals(wallpaperIntentService.getLastRecordedException("setFileWallpaper"), "java.lang.NullPointerException");
	}
	
	@Test
	public void invalidResourceUriShouldCauseFailureInSetResourceWallpaper()
	{
		Uri resourceUri = Uri.withAppendedPath(Uri.parse("android.resource:"), "/some/path/to/resource");
		JSONObject obj = new JSONObject();
		try {
			obj.put("uri", resourceUri);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONArray jArray = new JSONArray().put(obj);
		wallpaperIntentService.injectFeatureJSONArray(jArray);
		wallpaperIntentService.onConfigFeatureWrapper();
		
		Assert.assertEquals(wallpaperIntentService.getLastRecordedException("setResourceWallpaper"), "java.lang.NullPointerException");
	}
	
	
	@Test
	public void configResultShouldBeSentForFileUri() 
	{
		ConfigurationRequestReceiver receiver = broadcastReceiver("ConfigResultTestForFileUri");
        contextWrapper.registerReceiver(receiver, intentFilter(ConfiguratorConstants.INTENT_FEATURE_CONFIGURATION_RESULT));
        
		Uri fileUri = Uri.fromFile(new File("a/non-existent/file"));
		JSONObject obj = new JSONObject();
		try {
			obj.put("uri", fileUri);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONArray jArray = new JSONArray().put(obj);
		wallpaperIntentService.injectFeatureJSONArray(jArray);
		wallpaperIntentService.onConfigFeatureWrapper();
		
        transcript.assertEventsSoFar("ConfigResultTestForFileUri notified of " + ConfiguratorConstants.INTENT_FEATURE_CONFIGURATION_RESULT);
	}
	
	@Test
	public void configResultShouldBeSentForResourceUri() 
	{
		ConfigurationRequestReceiver receiver = broadcastReceiver("ConfigResultTestForResourceUri");
        contextWrapper.registerReceiver(receiver, intentFilter(ConfiguratorConstants.INTENT_FEATURE_CONFIGURATION_RESULT));
        
		Uri resourceUri = Uri.withAppendedPath(Uri.parse("android.resource:"), "/some/path/to/resource");
		JSONObject obj = new JSONObject();
		try {
			obj.put("uri", resourceUri);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONArray jArray = new JSONArray().put(obj);
		wallpaperIntentService.injectFeatureJSONArray(jArray);
		wallpaperIntentService.onConfigFeatureWrapper();
		
        transcript.assertEventsSoFar("ConfigResultTestForResourceUri notified of " + ConfiguratorConstants.INTENT_FEATURE_CONFIGURATION_RESULT);
	}
	
	@Test
	public void configResultShouldBeSentForAnyOtherUriScheme()
	{
        ConfigurationRequestReceiver receiver = broadcastReceiver("ConfigResultTest");
        contextWrapper.registerReceiver(receiver, intentFilter(ConfiguratorConstants.INTENT_FEATURE_CONFIGURATION_RESULT));

		Uri testUri = Uri.withAppendedPath(Uri.parse("some.generic.scheme:"), "/some/path/to/resource");
		JSONObject obj = new JSONObject();
		try {
			obj.put("uri", testUri);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONArray jArray = new JSONArray().put(obj);
		wallpaperIntentService.injectFeatureJSONArray(jArray);
		wallpaperIntentService.onConfigFeatureWrapper();
		
        transcript.assertEventsSoFar("ConfigResultTest notified of " + ConfiguratorConstants.INTENT_FEATURE_CONFIGURATION_RESULT);
		
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
