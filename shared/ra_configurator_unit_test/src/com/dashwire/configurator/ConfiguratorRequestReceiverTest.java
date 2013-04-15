package com.dashwire.configurator;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

import java.util.List;

import android.app.IntentService;
import android.os.Bundle;
import com.dashwire.base.debug.DashLogger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowContextWrapper;
import com.xtremelabs.robolectric.util.Transcript;
import com.xtremelabs.robolectric.shadows.ShadowLog;
import com.xtremelabs.robolectric.shadows.ShadowLog.LogItem;

@RunWith(RobolectricTestRunner.class)
public class ConfiguratorRequestReceiverTest {
    public Transcript transcript;
    private ContextWrapper contextWrapper;
    private JSONObject featureWallpaper;


    public static class MyTestService extends IntentService {

        MyTestService() {
            super("stubby");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            // Don't care
        }
    }

    @Before
    public void setUp() throws Exception {
        transcript = new Transcript();
        contextWrapper = new ContextWrapper(new Activity());

        JSONObject bonsaiWallpaper = new JSONObject();
        bonsaiWallpaper.put("uri", "android.resource://com.lge.launcher2/drawable/wallpaper_10");
        bonsaiWallpaper.put("page", 0);
        bonsaiWallpaper.put("src", "/branded/lg/74568098/devices/daejang/wallpaper/wallpaper_10.jpg");

        JSONArray wallpaperArray = new JSONArray();
        wallpaperArray.put(bonsaiWallpaper);

        featureWallpaper = new JSONObject();
        featureWallpaper.put("featureId", "Wallpaper01");
        featureWallpaper.put("featureName", "Wallpaper");
        featureWallpaper.put("featureData", wallpaperArray.toString());

        DashLogger.setDebugMode(false);

    }

    @Test
    public void registerReceiver_shouldRegisterForAllIntentFilterActions() throws Exception {
        ConfigurationRequestReceiver receiver = broadcastReceiver("Janar");
        contextWrapper.registerReceiver(receiver, intentFilter(ConfiguratorConstants.INTENT_FEATURE_CONFIGURE));

        Intent intent = new Intent(ConfiguratorConstants.INTENT_FEATURE_CONFIGURE);
        intent.putExtra("featureId", featureWallpaper.getString("featureId"));
        intent.putExtra("featureName", featureWallpaper.getString("featureName"));
        intent.putExtra("featureData", featureWallpaper.getString("featureData"));
        contextWrapper.sendBroadcast(intent);
        transcript.assertEventsSoFar("Janar notified of " + ConfiguratorConstants.INTENT_FEATURE_CONFIGURE);

        contextWrapper.sendBroadcast(new Intent("womp"));
        transcript.assertNoEventsSoFar();

        for (LogItem i : ShadowLog.getLogs()) {
            System.out.println(i.msg);
        }

    }


    @Test
    public void sendBroadcast_shouldSendIntentToEveryInterestedReceiver() throws Exception {
        ConfigurationRequestReceiver larryReceiver = broadcastReceiver("Larry");
        contextWrapper.registerReceiver(larryReceiver, intentFilter("foo", "baz"));

        ConfigurationRequestReceiver bobReceiver = broadcastReceiver("Bob");
        contextWrapper.registerReceiver(bobReceiver, intentFilter("foo"));

        contextWrapper.sendBroadcast(new Intent("foo"));
        transcript.assertEventsSoFar("Larry notified of foo", "Bob notified of foo");

        contextWrapper.sendBroadcast(new Intent("womp"));
        transcript.assertNoEventsSoFar();

        contextWrapper.sendBroadcast(new Intent("baz"));
        transcript.assertEventsSoFar("Larry notified of baz");
    }

    @Test
    public void unregisterReceiver_shouldUnregisterReceiver() throws Exception {
        ConfigurationRequestReceiver receiver = broadcastReceiver("Larry");

        contextWrapper.registerReceiver(receiver, intentFilter("foo", "baz"));
        contextWrapper.unregisterReceiver(receiver);

        contextWrapper.sendBroadcast(new Intent("foo"));
        transcript.assertNoEventsSoFar();
    }

    @Test(expected = IllegalArgumentException.class)
    public void unregisterReceiver_shouldThrowExceptionWhenReceiverIsNotRegistered() throws Exception {
        contextWrapper.unregisterReceiver(new AppWidgetProvider());
    }

    @Test
    public void broadcastReceivers_shouldBeSharedAcrossContextsPerApplicationContext() throws Exception {
        ConfigurationRequestReceiver receiver = broadcastReceiver("Larry");

        new ContextWrapper(Robolectric.application).registerReceiver(receiver, intentFilter("foo", "baz"));
        new ContextWrapper(Robolectric.application).sendBroadcast(new Intent("foo"));
        Robolectric.application.sendBroadcast(new Intent("baz"));
        transcript.assertEventsSoFar("Larry notified of foo", "Larry notified of baz");

        new ContextWrapper(Robolectric.application).unregisterReceiver(receiver);
    }

    @Test
    public void broadcasts_shouldBeLogged() {
        Intent broadcastIntent = new Intent("foo");
        contextWrapper.sendBroadcast(broadcastIntent);

        List<Intent> broadcastIntents = shadowOf(contextWrapper).getBroadcastIntents();
        assertTrue(broadcastIntents.size() == 1);
        assertEquals(broadcastIntent, broadcastIntents.get(0));
    }


    @Test
    public void shouldReturnSameApplicationEveryTime() throws Exception {
        Activity activity = new Activity();
        assertThat(activity.getApplication(), sameInstance(activity.getApplication()));

        assertThat(activity.getApplication(), sameInstance(new Activity().getApplication()));
    }

    @Test
    public void shouldReturnSameApplicationContextEveryTime() throws Exception {
        Activity activity = new Activity();
        assertThat(activity.getApplicationContext(), sameInstance(activity.getApplicationContext()));

        assertThat(activity.getApplicationContext(), sameInstance(new Activity().getApplicationContext()));
    }

    @Test
    public void shouldReturnSameContentResolverEveryTime() throws Exception {
        Activity activity = new Activity();
        assertThat(activity.getContentResolver(), sameInstance(activity.getContentResolver()));

        assertThat(activity.getContentResolver(), sameInstance(new Activity().getContentResolver()));
    }

    @Test
    public void shouldReturnSameLocationManagerEveryTime() throws Exception {
        assertSameInstanceEveryTime(Context.LOCATION_SERVICE);
    }

    @Test
    public void shouldReturnSameWifiManagerEveryTime() throws Exception {
        assertSameInstanceEveryTime(Context.WIFI_SERVICE);
    }

    @Test
    public void shouldReturnSameAlarmServiceEveryTime() throws Exception {
        assertSameInstanceEveryTime(Context.ALARM_SERVICE);
    }

    @Test
    public void shouldReturnAContext() {
        assertThat(contextWrapper.getBaseContext(), notNullValue());
        ShadowContextWrapper shContextWrapper = Robolectric.shadowOf(contextWrapper);
        shContextWrapper.attachBaseContext(null);
        assertThat(contextWrapper.getBaseContext(), nullValue());

        Activity baseContext = new Activity();
        shContextWrapper.attachBaseContext(baseContext);
        assertThat(contextWrapper.getBaseContext(), sameInstance((Context) baseContext));
    }

    private void assertSameInstanceEveryTime(String serviceName) {
        Activity activity = new Activity();
        assertThat(activity.getSystemService(serviceName), sameInstance(activity.getSystemService(serviceName)));

        assertThat(activity.getSystemService(serviceName), sameInstance(new Activity().getSystemService(serviceName)));
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
        IntentFilter larryIntentFilter = new IntentFilter();
        for (String action : actions) {
            larryIntentFilter.addAction(action);
        }
        return larryIntentFilter;
    }


    String getMessageFromArray(LogItem[] messages, int position) {
        try {
            return messages[position].msg;
        } catch (Exception ex) {
            return "Log message was not found because " + ex.toString();
        }
    }


    // The security and filtering of this intent receiver are handled in the manifest.
    @Test
    public void testMarshallNewIntent() {
        ConfigurationRequestReceiver receiver = new ConfigurationRequestReceiver();
        MyTestService svc = new MyTestService();
        Intent intent = new Intent("some.short.namespace");
        intent.putExtra("Hello", "World");
        receiver.onReceive(svc.getApplicationContext(), intent);

        Intent i = Robolectric.getShadowApplication().getNextStartedService();

        assertEquals("com.dashwire.configurator.controller.service", i.getAction());
        // Using getExtras().getString b/c getStringExtra() doesn't seem to return results on robolectric mock atm
        Assert.assertEquals("World", i.getExtras().getString("Hello"));
    }
}
