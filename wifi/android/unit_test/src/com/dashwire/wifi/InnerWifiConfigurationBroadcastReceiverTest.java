package com.dashwire.wifi;

import java.lang.Exception;
import java.lang.String;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;

import com.xtremelabs.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class InnerWifiConfigurationBroadcastReceiverTest {
    public static class ConfiguratorIntent extends Intent {

        public ConfiguratorIntent(String name) {
            super(name);
        }

        public static ConfiguratorIntent generateIntent(String featureName, JSONArray configArray) {
            if (featureName == null || configArray == null) {
                return null;
            }

            ConfiguratorIntent featureConfigurationRequestIntent = new ConfiguratorIntent("com.dashwire.feature.intent.CONFIGURE");
            featureConfigurationRequestIntent.putExtra("featureId", "NotDefinedYet");
            featureConfigurationRequestIntent.putExtra("featureName", featureName);
            featureConfigurationRequestIntent.putExtra("featureData", configArray.toString());
            featureConfigurationRequestIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            return featureConfigurationRequestIntent;
        }
    }

    @Test
    public void testOnReceive() {
        WifiConfigurator configurator = new WifiConfigurator();
        JSONArray configArray = null;
        try { configArray =new JSONArray("[{ \"ssid\" = \"ssid\", \"security\"=\"security\", \"key\"=\"key\" }]"); } catch (Exception ex) { Assert.fail(ex.toString()); }
        ConfiguratorIntent intent = ConfiguratorIntent.generateIntent("Wifi", configArray);
        configurator.onHandleIntent(intent);
    }

}
