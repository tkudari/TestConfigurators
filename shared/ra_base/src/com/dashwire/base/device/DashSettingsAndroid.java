package com.dashwire.base.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.base.debug.OverridePreferences;

/**
 * Author: tbostelmann
 */
public class DashSettingsAndroid implements DashSettings {
    private static final String TAG = DashSettingsAndroid.class.getCanonicalName();
    private static final String BASE_URI = "https://ready2go.att.com";
    protected Context context;

    public DashSettingsAndroid(Context context) {
        this.context = context;
    }

    public String getPhoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = telephonyManager.getLine1Number();
        if (OverridePreferences.getBoolean(context, "overridePhone", false)) {
            phoneNumber = OverridePreferences.getString(context, "overridePhoneNumber", phoneNumber);
        }
        return phoneNumber;
    }

    @Override
    public String getServerHostname() {
        String host = BASE_URI;
        if (OverridePreferences.getBoolean(context, PROPERTY_BOOLEAN_OVERRIDE_SERVER, false)) {
            host = OverridePreferences.getString(context, PROPERTY_STRING_OVERRID_SERVER_HOST, host);
        }
        return host;
    }

    @Override
    public String getTrackingUri() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString( DashSettings.PROPERTY_STRING_TRACKING_URI, null );
    }

    @Override
    public String getTrackingId() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getString( DashSettings.PROPERTY_STRING_TRACKING_ID, null );
    }

    @Override
    public String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    @Override
    public String getAndroidId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public String getClientVersion() {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo( context.getPackageName(), 0 );
            return packageInfo.versionName;
        } catch ( PackageManager.NameNotFoundException ex ) {
            DashLogger.e(TAG, ex.toString());
            return null;
        }
    }

    @Override
    public String getBuildRelease() {
        return Build.VERSION.RELEASE;
    }

    @Override
    public String getBuildIncremental() {
        return Build.VERSION.INCREMENTAL;
    }

    @Override
    public String getBuildSDK() {
        return Build.VERSION.SDK;
    }

    @Override
    public String getBuildDevice() {
        return Build.DEVICE;
    }

    @Override
    public String getBuildManufacturer() {
        return Build.MANUFACTURER;
    }
}
