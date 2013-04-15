package com.dashwire.base.device;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

public class DeviceInfo {
	private static final String TAG = DeviceInfo.class.getCanonicalName();
	private static Context context;
	public static boolean isAutomatedTestEnvironment = false;

	public DeviceInfo(Context context)
	{
		this.context = context;
		if (getOSVersion().equalsIgnoreCase("0")) {
			isAutomatedTestEnvironment = true;
		}
	}

    public static boolean isConfigurator3000Installed( Context context )
    {
        try
        {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo( "com.dashwire.configurator3000", 0 );
            return ( info.flags & ApplicationInfo.FLAG_SYSTEM ) != 0;
        } catch ( PackageManager.NameNotFoundException e )
        {
            return false;
        }
    }

	public static String getOSVersion() {
		Integer osVersion = Integer.valueOf(Build.VERSION.SDK_INT);

		if (isAutomatedTestEnvironment) {
			return "16";
		} else {
			return osVersion.toString();
		}
	}

	public static boolean isAutomatedEnvironment() {
		if (getOSVersion().equalsIgnoreCase("0")) {
			isAutomatedTestEnvironment = true;
		}
		return isAutomatedTestEnvironment;
	}

	public static void setContext(Context c) {
		context = c;		
	}
	
}
