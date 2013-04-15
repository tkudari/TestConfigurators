package com.dashwire.homeItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.dashwire.configurator.ConfiguratorBaseIntentService;
import com.dashwire.homeItem.utils.HomeItemUtils;

public class HomeItemConfigurator  extends ConfiguratorBaseIntentService {

	private static final String TAG = "HomeItemConfigurator";
	
    private BroadcastReceiver mWidgetResultReceiver = null;
    private boolean hasFailure = false;
    

	@Override
	protected void onConfigFeature() {
		Log.v(TAG, "featureDataJSONArray : " + mFeatureDataJSONArray.toString());
		
		String configForAPI = HomeItemUtils.buildWidgetsAndShortcutsConfig( mFeatureDataJSONArray );
		IntentFilter widgetResultFilter = new IntentFilter();
		widgetResultFilter.addAction("com.dashwire.launcher.CONFIGURE_HOME_ITEMS_RESULT");
        
        
		
		mWidgetResultReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive( Context widgetResultContext, Intent widgetResultIntent )
            {
                Log.v( TAG, "Homescreen result receiver called." );
                Log.v( TAG, "Homescreen result action = " + widgetResultIntent.getAction() );
                Log.v( TAG, "Homescreen result boolean = " + widgetResultIntent.getBooleanExtra( "success", false ) );
                Log.v( TAG, "Homescreen result json blob = " + widgetResultIntent.getStringExtra( "homescreen" ) );
                Log.v(TAG, "Homescreen result json blob length = " + (widgetResultIntent.getStringExtra("homescreen")).length());
                String failureReason = "DefaultFailureReason";

                String jsonString = widgetResultIntent.getStringExtra( "homescreen" );

                if ( jsonString != null )
                {
                    try
                    {
                        JSONArray widgetResultJSONArray = new JSONArray( jsonString );
                        for ( int i = 0; i < widgetResultJSONArray.length(); i++ )
                        {
                            JSONObject item = widgetResultJSONArray.getJSONObject( i );
                            Log.d( TAG, "Homscreen Result JSON Item = " + item.toString() );
                            String packageName = "", className = "", errorMessage = "";
                            int errorCode = 0;
                            if ( item.has( "packageName" ) )
                                packageName = item.getString( "packageName" );
                            if ( item.has( "className" ) )
                                className = item.getString( "className" );
                            if ( item.has( "errorCode" ) )
                                errorCode = item.getInt( "errorCode" );
                            if ( item.has( "errorMessage" ) )
                                errorMessage = item.getString( "errorMessage" );
                            if ( item.getInt( "success" ) != 0 )
                            {
                                hasFailure = true;
                                Log.d( TAG, "Widget/Shortcut Item success is not 0" );
                                Log.d( TAG, "Package Name: " + packageName );
                                Log.d( TAG, "Class Name: " + className );
                                Log.d( TAG, "Error Message: " + errorMessage );
                                failureReason = errorMessage;
                                Log.d( TAG, "Error Code: " + errorCode );
                                Log.d( TAG, "Item success = " + item.getInt( "success" ) );
                            }
                        }
                    } catch ( JSONException jse )
                    {
                        hasFailure = true;
                        Log.d( TAG, "JSON Exception processing widget results: " + jse.getMessage() );
                    }
                } else
                {
                    Log.d( TAG, "Response homescreen object is null." );
                    hasFailure = true;
                }

				if (hasFailure) {					
					onFailure(failureReason);					
				} else {
					onSuccess();
				}

                getApplicationContext().unregisterReceiver( mWidgetResultReceiver );
                                
                mWidgetResultReceiver = null;
            }
        };
		
        
        getApplicationContext().registerReceiver( mWidgetResultReceiver, widgetResultFilter );
		

        Intent homescreenIntent = new Intent( "com.dashwire.launcher.CONFIGURE_HOME_ITEMS" );
        homescreenIntent.putExtra( "version", "1.0" );
        homescreenIntent.putExtra( "homescreen", configForAPI );
        Log.v( TAG, "Broadcasting Homescreen Intent" );
        Log.v( TAG, "Homescreen Action = " + homescreenIntent.getAction() );
        Log.v( TAG, "Homescreen version = " + homescreenIntent.getStringExtra( "version" ) );
        Log.v( TAG, "Homescreen json blob = " + homescreenIntent.getStringExtra( "homescreen" ) );
        Log.v( TAG, "Homescreen json blob length = " + ( homescreenIntent.getStringExtra( "homescreen" ) ).length() );
        
        //TODO: enable permission here:
//        mContext.sendBroadcast( homescreenIntent, "com.dashwire.homescreen.permission.CONFIGURE_HOMESCREEN" );
        
        getApplicationContext().sendBroadcast( homescreenIntent );
	}
	
	public void injectFeatureJSONArray(JSONArray jsonArray) 
	{
		mFeatureDataJSONArray = jsonArray;
	}
	
	
}
