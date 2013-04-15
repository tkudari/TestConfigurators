package com.dashwire.wifi;


import java.lang.Exception;
import java.lang.String;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import com.dashwire.wifi.WifiConfigurator.WifiConfigurationItem;

@RunWith(RobolectricTestRunner.class)
public class WifiConfigurationItemTest {
    @Test
    public void testGenerateFromValidJSONObject() {
        String baseJSON = "{ \"ssid\" = \"ssid\", \"security\"=\"security\", \"key\"=\"key\" }";
        JSONObject jsonObj = null;
        try { jsonObj = new JSONObject(baseJSON); } catch (Exception ex) { Assert.fail(ex.toString()); }
        WifiConfigurationItem item =  WifiConfigurationItem.generateItem(jsonObj);
        Assert.assertEquals("ssid", item.SSID);
        Assert.assertEquals("security", item.Security);
        Assert.assertEquals("key", item.Key);
    }

    @Test
    public void testGenerateFromInvalidJSONObject() {
        String baseJSON = "{ \"ssid\" = \"ssid\", \"security\"=\"security\", \"ke\"=\"key\" }";
        JSONObject jsonObj = null;
        try { jsonObj = new JSONObject(baseJSON); } catch (Exception ex) { Assert.fail(ex.toString()); }
        WifiConfigurationItem item =  WifiConfigurationItem.generateItem(jsonObj);
        Assert.assertNull(item);
    }

    @Test
    public void testConstructor() {
        WifiConfigurationItem item = new WifiConfigurationItem("ssid", "security", "key");
        Assert.assertEquals("ssid", item.SSID);
        Assert.assertEquals("security", item.Security);
        Assert.assertEquals("key", item.Key);
    }

}
