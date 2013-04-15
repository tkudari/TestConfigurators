package com.dashwire.ringtone;

import android.app.Activity;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Pair;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;

@SuppressWarnings({"UnusedDeclaration"})
@Implements(RingtoneManager.class)
public class DWRingtoneManager {

    public static Uri lastRingtoneUri = null;
    public static int lastType = -1;

    public void __constructor__(Context context) {

    }

    public void __constructor__(Activity activity) {

    }

    @Implementation
    public static void setActualDefaultRingtoneUri(Context context, int type, Uri ringtoneUri) {
        lastRingtoneUri = ringtoneUri;
        lastType = type;
    }

}
