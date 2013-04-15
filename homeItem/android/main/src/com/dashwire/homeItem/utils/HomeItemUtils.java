package com.dashwire.homeItem.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dashwire.base.debug.DashLogger;

public class HomeItemUtils {

	private static final String TAG = HomeItemUtils.class.getCanonicalName();

	
	 public static String buildWidgetsAndShortcutsConfig( JSONArray configArray ) {
	        JSONArray array = new JSONArray();
	        try
	        {
	            for ( int i = 0; i < configArray.length(); i++ )
	            {
	                JSONObject item = configArray.getJSONObject( i );

	                String packageName = extractPackage( item.getString( "id" ) );
	                String providerName = extractProvider( item.getString( "id" ) );

	                int containerId = -100;
	                if ( item.has( "container_id" ) )
	                {
	                    containerId = item.getInt( "container_id" );
	                }

	                String category = item.getString( "category" );
	                if ( category.equalsIgnoreCase( "widgets" ) || category.equalsIgnoreCase( "widget"))
	                {
	                    category = "widget";
	                } else if ( category.equalsIgnoreCase( "shortcuts" ) || category.equalsIgnoreCase( "shortcut") )
	                {
	                    category = "shortcut";
	                } else
	                {
	                    DashLogger.d( TAG, "Invalid class for widget: " + category );
	                    category = null;
	                }

	                JSONObject object = new JSONObject();
	                object.put( "type", category );
	                object.put( "title", item.getString( "title" ) );
	                object.put( "packageName", packageName );
	                object.put( "className", providerName );
	                object.put( "container", containerId );
	                object.put( "screen", item.getInt( "screen" ) );
	                object.put( "x", item.getInt( "x" ) );
	                object.put( "y", item.getInt( "y" ) );
	                object.put( "rows", item.getInt( "rows" ) );
	                object.put( "cols", item.getInt( "cols" ) );

	                array.put( object );
	            }

	        } catch ( Exception je )
	        {
	            je.printStackTrace();
	        }
	        return array.toString();
	    }

	 
	   public static String extractPackage( String widgetId )
	    {
	        String packageName = widgetId.substring( 0, widgetId.lastIndexOf( "/" ) );
	        return packageName;
	    }

	    public static String extractProvider( String widgetId )
	    {
	        String providerClassName = widgetId.substring( widgetId.lastIndexOf( "/" ) + 1 );
	        String providerName = "";
	        String packageName = extractPackage( widgetId );
	        if ( ".".equalsIgnoreCase( providerClassName.substring( 0, 1 ) ) )
	        {
	            providerName = packageName + providerClassName;
	        } else
	        {
	            providerName = providerClassName;
	        }
	        return providerName;
	    }
	
}
