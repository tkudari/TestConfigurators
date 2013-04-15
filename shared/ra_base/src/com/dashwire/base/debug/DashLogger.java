package com.dashwire.base.debug;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import com.dashwire.base.device.DeviceInfo;


public class DashLogger {

    private static Context mContext;

    private static boolean debugModeChecked = false;
    private static boolean debugMode = false;

    //final private static boolean debuggable = false;

    public static void setDebugMode(boolean debugMode) {
        DashLogger.debugMode = debugMode;
        DashLogger.debugModeChecked = true;
    }

    public static boolean isDebugMode() {

    		if(!debugModeChecked) {
            //check conditions that trigger whether in debug mode
            if (mContext != null &&
		            ( DeviceInfo.isConfigurator3000Installed(mContext) ||
		              (mContext.getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0)) {
                debugMode = true;
		        }
		        else {
                debugMode = false;
            }
            debugModeChecked = true;
        }
        return debugMode;
    }

    // in decreasing order of priority:

    public static void e( String TAG, String message ) {
        Log.e( TAG, message );
        addAutomatedTestingEnvironmentLog(TAG, message);
    }

    public static void e( String TAG, String message, Throwable e ) {
    	Log.e( TAG, message, e );
        addAutomatedTestingEnvironmentLog(TAG, message);
    }

    public static void w( String TAG, String message ) {
        Log.w( TAG, message );
        addAutomatedTestingEnvironmentLog(TAG, message);
    }

    public static void i( String TAG, String message ) {
    	Log.i( TAG, message );
        addAutomatedTestingEnvironmentLog(TAG, message);
    }

    public static void d( String TAG, String message ) {
        if ( isDebugMode() ) {
            Log.d( TAG, message );
            addAutomatedTestingEnvironmentLog(TAG, message);
        }
    }

    public static void v( String TAG, String message ) {
        if ( isDebugMode() ) {
            Log.v( TAG, message );
            addAutomatedTestingEnvironmentLog(TAG, message);
        }
    }

    public static void setContext( Context context ) {
        mContext = context;
    }

    private static void addAutomatedTestingEnvironmentLog(String TAG, String message) {
        if (DeviceInfo.isAutomatedEnvironment()) {
            System.out.println(TAG + " Message : " + message);
        }
    }
}
