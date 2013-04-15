package com.dashwire.base.debug;

import com.dashwire.base.device.DeviceInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;

public class OverridePreferences {

    public static final String PREFERENCES_NAME = "configurator3000";
    private Context context;

    public OverridePreferences(Context context) {
        this.context = context;
    }

    public String getPreference(String key, String defaultValue) {
        SharedPreferences preferences = getPreferences(context);
        if (preferences != null) {
            return preferences.getString(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences preferences = getPreferences(context);
        if (preferences != null) {
            return preferences.getString(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static long getStringAsLong(Context context, String key, long defaultValue) {
        String value = getString(context, key, String.valueOf(defaultValue));
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = getPreferences(context);
        if (preferences != null) {
            return preferences.getBoolean(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static SharedPreferences getPreferences(Context context) {
        try {
        	if(DeviceInfo.isConfigurator3000Installed(context)) {
                Context configurator3000Context = context.createPackageContext("com.dashwire.configurator3000", 0);
                return configurator3000Context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_WORLD_READABLE | Context.MODE_MULTI_PROCESS);
            }
        } catch (NameNotFoundException e) {
            // configurator 3000 not installed, ignore
        }
        return null;
    }
}
