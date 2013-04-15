package com.dashwire.wifi;

import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;

import android.content.*;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;

import com.dashwire.wifi.WifiConfigurator.WifiConfigurationItem;
import com.dashwire.wifi.WifiConfigurator.InnerWifiConfigurator;


@RunWith(CustomWifiTestRunner.class)
public class InnerWifiConfiguratorTest {

    Activity mBaseActivity;
    Context mBaseContext;


    @Before
    public void setup() throws Exception {
        mBaseActivity = new Activity();
        mBaseContext = mBaseActivity.getApplicationContext();
    }


    @Test
    public void testGenerateSecurityLevelNoneConfig() {
        InnerWifiConfigurator configurator = new InnerWifiConfigurator(null);
        WifiConfiguration configuration = configurator.generateSecurityLevelNoneConfig("ssid1");
        Assert.assertEquals("\"ssid1\"", configuration.SSID);
        Assert.assertTrue(configuration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE));
    }

    @Test
    public void testGenerateSecurityLevelWEPConfig() {
        InnerWifiConfigurator configurator = new InnerWifiConfigurator(null);
        WifiConfiguration config = configurator.generateSecurityLevelWEPConfig("ssid1", "key1");
        Assert.assertEquals("\"ssid1\"", config.SSID);
        Assert.assertTrue(config.wepKeys[0].equals("key1"));
        Assert.assertTrue(config.wepTxKeyIndex == 0);
        Assert.assertTrue(config.priority == 40);
        Assert.assertTrue(config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE));
        Assert.assertTrue(config.allowedProtocols.get(WifiConfiguration.Protocol.RSN));
        Assert.assertTrue(config.allowedProtocols.get(WifiConfiguration.Protocol.WPA));
        Assert.assertTrue(config.allowedAuthAlgorithms.get(WifiConfiguration.AuthAlgorithm.OPEN));
        Assert.assertTrue(config.allowedAuthAlgorithms.get(WifiConfiguration.AuthAlgorithm.SHARED));
        Assert.assertTrue(config.allowedGroupCiphers.get(WifiConfiguration.GroupCipher.WEP40));
        Assert.assertTrue(config.allowedGroupCiphers.get(WifiConfiguration.GroupCipher.WEP104));
        Assert.assertTrue(config.allowedGroupCiphers.get(WifiConfiguration.GroupCipher.CCMP));
        Assert.assertTrue(config.allowedGroupCiphers.get(WifiConfiguration.GroupCipher.TKIP));
        Assert.assertTrue(config.allowedPairwiseCiphers.get(WifiConfiguration.PairwiseCipher.CCMP));
        Assert.assertTrue(config.allowedPairwiseCiphers.get(WifiConfiguration.PairwiseCipher.TKIP));
    }

    @Test
    public void testGenerateSecurityLevelWPAConfig() {
        InnerWifiConfigurator configurator = new InnerWifiConfigurator(null);
        WifiConfiguration config = configurator.generateSecurityLevelWPAConfig("ssid1", "key1");
        Assert.assertEquals("\"ssid1\"", config.SSID);
        Assert.assertEquals("\"key1\"", config.preSharedKey);
        Assert.assertTrue(config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK));
        Assert.assertTrue(config.allowedAuthAlgorithms.get(WifiConfiguration.AuthAlgorithm.OPEN));
    }


    @Test
    public void testSetupWifiWPA() {
        InnerWifiConfigurator configurator = new InnerWifiConfigurator(mBaseContext);
        WifiConfigurationItem item = new WifiConfigurationItem("ssid1", "anything but wep or open", "key1");
        Assert.assertTrue(configurator.setupWifi(item));

        // verify the network that was setup was WPA
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(Robolectric.getShadowApplication().getSystemService(Context.WIFI_SERVICE));
        List<WifiConfiguration> wifiConfigurations = shadowWifiManager.getConfiguredNetworks();
        WifiConfiguration config = wifiConfigurations.get(0);
        Assert.assertEquals("\"ssid1\"", config.SSID);
        Assert.assertEquals("\"key1\"", config.preSharedKey);
        Assert.assertTrue(config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK));
    }

    @Test
    public void testSetupWifiWEP() {
        InnerWifiConfigurator configurator = new InnerWifiConfigurator(mBaseContext);
        WifiConfigurationItem item = new WifiConfigurationItem("ssid1", "wep", "key1");
        Assert.assertTrue(configurator.setupWifi(item));

        // verify the network that was setup was WPA
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(Robolectric.getShadowApplication().getSystemService(Context.WIFI_SERVICE));
        List<WifiConfiguration> wifiConfigurations = shadowWifiManager.getConfiguredNetworks();
        WifiConfiguration config = wifiConfigurations.get(0);
        Assert.assertEquals("\"ssid1\"", config.SSID);
        Assert.assertTrue(config.wepKeys[0].equals("key1"));
    }


    @Test
    public void testSetupWifiOpen() {
        InnerWifiConfigurator configurator = new InnerWifiConfigurator(mBaseContext);
        WifiConfigurationItem item = new WifiConfigurationItem("ssid1", "none", "key1");
        Assert.assertTrue(configurator.setupWifi(item));

        // verify the network that was setup was WPA
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(Robolectric.getShadowApplication().getSystemService(Context.WIFI_SERVICE));
        List<WifiConfiguration> wifiConfigurations = shadowWifiManager.getConfiguredNetworks();
        WifiConfiguration config = wifiConfigurations.get(0);
        Assert.assertEquals("\"ssid1\"", config.SSID);
        Assert.assertTrue(config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE));
        Assert.assertFalse(config.allowedProtocols.get(WifiConfiguration.Protocol.RSN)); // This is to distinguish it from WEP setup
    }

    @Test
    public void testConfiguredNetworkGetsRemoved() {
        InnerWifiConfigurator configurator = new InnerWifiConfigurator(mBaseContext);
        WifiConfigurationItem item = new WifiConfigurationItem("ssid1", "wpa", "key1");
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(Robolectric.getShadowApplication().getSystemService(Context.WIFI_SERVICE));
        WifiConfiguration configuredNetwork = new WifiConfiguration();
        configuredNetwork.SSID = "\"ssid1\"";
        configuredNetwork.preSharedKey = "ThisShouldChange";
        shadowWifiManager.addNetwork(configuredNetwork);

        // Assert we have things set up correctly
        List<WifiConfiguration> wifiConfigurations;
        wifiConfigurations = shadowWifiManager.getConfiguredNetworks();
        Assert.assertEquals("ThisShouldChange", wifiConfigurations.get(0).preSharedKey);

        boolean result = configurator.setupWifi(item);

        Assert.assertTrue(result);
        wifiConfigurations = shadowWifiManager.getConfiguredNetworks();
        Assert.assertEquals("\"key1\"", wifiConfigurations.get(0).preSharedKey);
        Assert.assertEquals(1, wifiConfigurations.size());
    }

    @Test
    public void testConfigureWifiWithFailingToAddNetwork() {
        InnerWifiConfigurator configurator = new InnerWifiConfigurator(mBaseContext);
        WifiConfigurationItem item = new WifiConfigurationItem("ssid1", "none", "key1");
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(Robolectric.getShadowApplication().getSystemService(Context.WIFI_SERVICE));
        shadowWifiManager.setNextNetworkAddToFail();
        Assert.assertFalse(configurator.setupWifi(item));
    }

    @Test
    public void testConfigureWifiWithNetworkInScanResults() {
        InnerWifiConfigurator configurator = new InnerWifiConfigurator(mBaseContext);
        WifiConfigurationItem item = new WifiConfigurationItem("ssid1", "wpa", "key1");
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(Robolectric.getShadowApplication().getSystemService(Context.WIFI_SERVICE));
        List<ScanResult> scanResults = new ArrayList<ScanResult>();
        ScanResult result = ShadowScanResult.newInstance("ssid1", "bssid whatever", "caps whatever", 0, 0);
        scanResults.add(result);
        shadowWifiManager.setScanResults(scanResults);

        Assert.assertTrue(configurator.setupWifi(item));
        Assert.assertTrue(shadowWifiManager.getLastEnabledNetwork().second);
        Assert.assertEquals("\"key1\"", shadowWifiManager.getConfiguredNetworks().get(0).preSharedKey);

    }
}
