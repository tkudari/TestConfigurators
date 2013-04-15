package com.dashwire.ringtone;

import android.app.Activity;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;
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
@Implements(MediaStore.Audio.Media.class)
public class DWShadowMediaStoreAudioMedia {

    public static Uri ContentUriForPath = null;
    public static String lastContentUriForPath = null;

    public static Uri getContentUriForPath(String path) {
        lastContentUriForPath = path;
        return ContentUriForPath;
    }
}
