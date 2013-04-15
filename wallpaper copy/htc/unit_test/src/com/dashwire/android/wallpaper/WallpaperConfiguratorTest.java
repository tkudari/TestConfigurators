package com.dashwire.android.wallpaper;

import com.dashwire.wallpaper.WallpaperConfigurator;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.Test;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import android.content.Intent;

import com.xtremelabs.robolectric.Robolectric;

import java.util.List;


@RunWith(RobolectricTestRunner.class)
public class WallpaperConfiguratorTest {
    @Test
    public void testOnConfigFeatureWithValidJSONSenseFourPlus() {
        WallpaperConfigurator svc = new WallpaperConfigurator();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureName", "blah2");
        i.putExtra("featureData", "[{\"uri\" : \"file:///somewhere\"}]");
        i.putExtra("isFirstLaunch", true);
        i.putExtra("HTCSenseLevel", "4+");
        svc.onHandleIntent(i);

        // Assert it made the Intent call to store it
        // Assert that onSuccess was called or it will stick on the UI

        Intent result = Robolectric.shadowOf(svc).getBroadcastIntents().get(0);
        assertEquals("com.htc.home.personalize.ACTION_READY2GO", result.getAction()); // Proxy for successful storage action

        result = Robolectric.shadowOf(svc).getBroadcastIntents().get(1);
        assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", result.getAction()); // Proxy for successful onSuccess call
        assertEquals("success", result.getStringExtra("result"));
    }

    @Test
    public void testOnConfigFeatureWithIncorrectJSONSenseFourPlus() {
        WallpaperConfigurator svc = new WallpaperConfigurator();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureName", "blah2");
        i.putExtra("featureData", "[{\"boo\" : \"file:///something\"}]");
        i.putExtra("isFirstLaunch", true);
        i.putExtra("HTCSenseLevel", "4+");
        svc.onHandleIntent(i);

        // Assert that onSuccess was called or it will stick on the UI
        // Since this is the first intent in the queue it means nothing else got called as expected

        Intent result = Robolectric.shadowOf(svc).getBroadcastIntents().get(0);
        assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", result.getAction()); // Proxy for successful onSuccess call
        assertEquals("failed", result.getStringExtra("result"));
    }



    @Test
    public void testOnConfigFeatureWithValidJSONSenseFiveOhFirstBoot() {
        WallpaperConfigurator svc = new WallpaperConfigurator();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureName", "blah2");
        i.putExtra("featureData", "[{\"uri\" : \"file:///system/customize/resource/wallpaper1.jpg\"}]");
        i.putExtra("isFirstLaunch", true);
        i.putExtra("HTCSenseLevel", "5.0");
        svc.onHandleIntent(i);

        // Assert it made the Intent call to store it
        // Assert that onSuccess was called or it will stick on the UI

        Intent result = Robolectric.shadowOf(svc).getBroadcastIntents().get(0);
        assertEquals("com.htc.home.personalize.ACTION_CONFIG_FROM_AURORA2", result.getAction()); // Proxy for successful storage action


        result = Robolectric.shadowOf(svc).getBroadcastIntents().get(1);
        assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", result.getAction()); // Proxy for successful onSuccess call
        assertEquals("success", result.getStringExtra("result"));
    }

    @Test
    public void testOnConfigFeatureWithIncorrectJSONSenseFiveOhFirstBoot() {
        WallpaperConfigurator svc = new WallpaperConfigurator();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureName", "blah2");
        i.putExtra("featureData", "[{\"boo\" : \"file:///system/customize/resource/wallpaper1.jpg\"}]");
        i.putExtra("isFirstLaunch", true);
        i.putExtra("HTCSenseLevel", "5.0");
        svc.onHandleIntent(i);

        // Assert that onSuccess was called or it will stick on the UI
        // Since this is the first intent in the queue it means nothing else got called as expected

        Intent result = Robolectric.shadowOf(svc).getBroadcastIntents().get(0);
        assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", result.getAction()); // Proxy for successful onSuccess call
        assertEquals("failed", result.getStringExtra("result"));
    }

    @Test
    public void testOnConfigFeatureWithValidJSONSenseFiveOhNotFirstBoot() {
        WallpaperConfigurator svc = new WallpaperConfigurator();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureName", "blah2");
        i.putExtra("featureData", "[{\"uri\" : \"file:///system/customize/resource/wallpaper1.jpg\"}]");
        i.putExtra("isFirstLaunch", false);
        i.putExtra("HTCSenseLevel", "5.0");
        svc.onHandleIntent(i);

        // Assert it made the Intent call to store it
        // Assert that onSuccess was called or it will stick on the UI

        Intent result = Robolectric.shadowOf(svc).getBroadcastIntents().get(0);
        assertEquals("com.htc.home.personalize.ACTION_CONFIG_FROM_AURORA2", result.getAction()); // Proxy for successful storage action

        result = Robolectric.shadowOf(svc).getBroadcastIntents().get(1);
        assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", result.getAction()); // Proxy for successful onSuccess call
        assertEquals("success", result.getStringExtra("result"));
    }

    @Test
    public void testOnConfigFeatureWithIncorrectJSONSenseFiveOhNotFirstBoot() {
        WallpaperConfigurator svc = new WallpaperConfigurator();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureName", "blah2");
        i.putExtra("featureData", "[{\"boo\" : \"file:///system/customize/resource/wallpaper1.jpg\"}]");
        i.putExtra("isFirstLaunch", false);
        i.putExtra("HTCSenseLevel", "5.0");
        svc.onHandleIntent(i);

        // Assert that onSuccess was called or it will stick on the UI
        // Since this is the first intent in the queue it means nothing else got called as expected

        Intent result = Robolectric.shadowOf(svc).getBroadcastIntents().get(0);
        assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", result.getAction()); // Proxy for successful onSuccess call
        assertEquals("failed", result.getStringExtra("result"));
    }

    // This test should probably be moved to a separate test if we break out the intent model for Sense Five Oh
    @Test
    public void testHTCSenseFiveOhIntentData() {
        WallpaperConfigurator svc = new WallpaperConfigurator();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureName", "blah2");
        i.putExtra("featureData", "[{\"uri\" : \"file:///system/customize/resource/wallpaper1.jpg\"}]");
        i.putExtra("isFirstLaunch", true);
        i.putExtra("HTCSenseLevel", "5.0");
        svc.onHandleIntent(i);

        Intent result = Robolectric.shadowOf(svc).getBroadcastIntents().get(0);
        assertEquals("com.htc.home.personalize.ACTION_CONFIG_FROM_AURORA2", result.getAction());
        assertEquals(" ", result.getStringExtra("extra_wallpaper_displayname"));
        assertEquals("wallpaper1.jpg", result.getStringExtra("extra_wallpaperguid"));
        assertEquals(true, result.getBooleanExtra("extra_in_oobe", false));
    }
}
