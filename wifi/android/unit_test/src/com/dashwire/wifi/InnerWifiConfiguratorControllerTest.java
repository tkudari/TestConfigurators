package com.dashwire.wifi;

import java.lang.Exception;
import java.util.List;

import android.content.*;
import android.net.wifi.WifiManager;
import android.os.Looper;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;

import com.dashwire.wifi.WifiConfigurator.InnerWifiConfiguratorController;
import com.dashwire.wifi.WifiConfigurator.WifiConfigurationItem;


@RunWith(CustomWifiTestRunner.class)
public class InnerWifiConfiguratorControllerTest {

    Activity mBaseActivity;
    Context mBaseContext;
    WifiConfigurator mWifiConfigurator;


    @Before
    public void setup() throws Exception {
        mBaseActivity = new Activity();
        mBaseContext = mBaseActivity.getApplicationContext();
        mWifiConfigurator = new WifiConfigurator();
    }

    @Test
    public void testConstructor() {
        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(mBaseContext, mWifiConfigurator, false);

        // This is all we currently have access to test
        // But other callbacks are getting set that we need to make sure happen
        Assert.assertEquals(mBaseContext, controller.getContext());
        Assert.assertEquals(mWifiConfigurator, controller.getWifiConfigurator());
    }

    @Test
    public void testNofifySuccess() {
        WifiConfigurator wifi = new WifiConfigurator();
        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(mBaseContext, wifi, false);
        controller.notifySuccess();

        ShadowService shadowService = Robolectric.shadowOf(wifi);
        List<Intent> intentList = shadowService.getBroadcastIntents();

        Assert.assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", intentList.get(0).getAction());
        Assert.assertEquals("success", intentList.get(0).getStringExtra("result"));
    }

    @Test
    public void testNotifyFailure() {
        WifiConfigurator wifi = new WifiConfigurator();
        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(mBaseContext, wifi, false);
        controller.notifyFailure("because");

        ShadowService shadowService = Robolectric.shadowOf(wifi);
        List<Intent> intentList = shadowService.getBroadcastIntents();

        Assert.assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", intentList.get(0).getAction());
        Assert.assertEquals("failed", intentList.get(0).getStringExtra("result"));
    }

    @Test
    public void testMarshallItemsValidJson() throws Exception {
        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(null, null, false);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj1 = new JSONObject("{ \"ssid\" = \"ssid1\", \"security\"=\"security1\", \"key\"=\"key1\" }");
        JSONObject jsonObj2 = new JSONObject("{ \"ssid\" = \"ssid2\", \"security\"=\"security2\", \"key\"=\"key2\" }");
        jsonArray.put(jsonObj1);
        jsonArray.put(jsonObj2);

        List<WifiConfigurationItem> wifiConfigurationItemList = controller.marshalItems(jsonArray);

        Assert.assertEquals("ssid1", wifiConfigurationItemList.get(0).SSID);
        Assert.assertEquals("ssid2", wifiConfigurationItemList.get(1).SSID);
    }

    @Test
    public void testMarshallItemsInvalidJson() throws Exception {
        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(null, null, false);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj1 = new JSONObject("{ \"ssid\" = \"ssid1\", \"security\"=\"security1\", \"ke\"=\"key1\" }");
        jsonArray.put(jsonObj1);

        List<WifiConfigurationItem> wifiConfigurationItemList = controller.marshalItems(jsonArray);

        Assert.assertTrue(wifiConfigurationItemList.isEmpty());
    }

    @Test
    public void testIsWifiEnabled() {

        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(mBaseContext, null, false);

        WifiManager wifiManager = (WifiManager) mBaseContext.getSystemService( Context.WIFI_SERVICE );
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(wifiManager);

        // Enable it
        shadowWifiManager.setWifiEnabled(true);
        Assert.assertTrue(controller.isWifiEnabled());

        // Disable it
        shadowWifiManager.setWifiEnabled(false);
        Assert.assertFalse(controller.isWifiEnabled());
    }



    @Test
    public void testStartConfigurationWifiEnabled() throws Exception {
        // This is kind of a mega test but I don't know how to break it up
        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(mBaseContext, mWifiConfigurator, false);

        // Give us some valid input to process
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj1 = new JSONObject("{ \"ssid\" = \"ssid1\", \"security\"=\"security1\", \"key\"=\"key1\" }");
        JSONObject jsonObj2 = new JSONObject("{ \"ssid\" = \"ssid2\", \"security\"=\"security2\", \"key\"=\"key2\" }");
        jsonArray.put(jsonObj1);
        jsonArray.put(jsonObj2);

        // Set the wifi state to on
        WifiManager wifiManager = (WifiManager) mBaseContext.getSystemService( Context.WIFI_SERVICE );
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(wifiManager);
        shadowWifiManager.setWifiEnabled(true);

        controller.startConfiguration(jsonArray);

        // this should be the outcome if it goes down the doSetup path and doSetup was called with no errors
        ShadowService shadowService = Robolectric.shadowOf(mWifiConfigurator);
        List<Intent> intentList = shadowService.getBroadcastIntents();

        Assert.assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", intentList.get(0).getAction());
        Assert.assertEquals("success", intentList.get(0).getStringExtra("result"));
    }

    @Test
    public void testStartConfigurationWifiNotEnabledAndTimeoutSetOnceAlready() throws Exception {
        // This is kind of a mega test but I don't know how to break it up
        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(mBaseContext, mWifiConfigurator, true);

        // Give us some valid input to process
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj1 = new JSONObject("{ \"ssid\" = \"ssid1\", \"security\"=\"security1\", \"key\"=\"key1\" }");
        JSONObject jsonObj2 = new JSONObject("{ \"ssid\" = \"ssid2\", \"security\"=\"security2\", \"key\"=\"key2\" }");
        jsonArray.put(jsonObj1);
        jsonArray.put(jsonObj2);

        // Set the wifi state to on
        WifiManager wifiManager = (WifiManager) mBaseContext.getSystemService( Context.WIFI_SERVICE );
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(wifiManager);
        shadowWifiManager.setWifiEnabled(false);

        controller.startConfiguration(jsonArray);

        // this should be the outcome if it goes down the doSetup path and doSetup was called with no errors
        ShadowService shadowService = Robolectric.shadowOf(mWifiConfigurator);
        List<Intent> intentList = shadowService.getBroadcastIntents();

        Assert.assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", intentList.get(0).getAction());
        Assert.assertEquals("failed", intentList.get(0).getStringExtra("result"));
    }


    @Test
    public void testStartConfigurationWifiNotEnabledAndTimeoutNotSet() throws Exception {
        // This is kind of a mega test but I don't know how to break it up
        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(mBaseContext, mWifiConfigurator, false);

        // Give us some valid input to process
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj1 = new JSONObject("{ \"ssid\" = \"ssid1\", \"security\"=\"security1\", \"key\"=\"key1\" }");
        JSONObject jsonObj2 = new JSONObject("{ \"ssid\" = \"ssid2\", \"security\"=\"security2\", \"key\"=\"key2\" }");
        jsonArray.put(jsonObj1);
        jsonArray.put(jsonObj2);

        // Set the wifi state to on
        WifiManager wifiManager = (WifiManager) mBaseContext.getSystemService( Context.WIFI_SERVICE );
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(wifiManager);
        shadowWifiManager.setWifiEnabled(false);

        controller.startConfiguration(jsonArray);

        // Handler gets set
        ShadowLooper shadowLooper = Robolectric.shadowOf(Looper.getMainLooper());
        Assert.assertTrue(shadowLooper.getScheduler().size() == 1); // Should be something enqueued. Cannot figure out how to verify it yet. May have to modify source to make it happen. Or run it and detect side effects

        // timeout boolean already set
        Assert.assertTrue(controller.getAlreadySetRetryTimeoutOnce());

        // receiver registered
        ShadowApplication shadowApplication = Robolectric.shadowOf(mBaseActivity.getApplication());
        List<BroadcastReceiver> broadcastReceiverList = shadowApplication.getReceiversForIntent(new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION));
        Assert.assertTrue(broadcastReceiverList.get(0) instanceof WifiConfigurator.InnerWifiConfigurationBroadcastReceiver);

        // wifi enabled
        Assert.assertTrue(wifiManager.isWifiEnabled());
    }


    @Test
    public void testCleanupCallbacks() throws Exception {
        // This builds off of testStartConfigurationWifiNotEnabledAndTimeoutNotSet in that after this scenario gets run we should have callback so now we call
        // cleanupCallbacks and they should disappear.
        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(mBaseContext, mWifiConfigurator, false);

        // Give us some valid input to process
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj1 = new JSONObject("{ \"ssid\" = \"ssid1\", \"security\"=\"security1\", \"key\"=\"key1\" }");
        JSONObject jsonObj2 = new JSONObject("{ \"ssid\" = \"ssid2\", \"security\"=\"security2\", \"key\"=\"key2\" }");
        jsonArray.put(jsonObj1);
        jsonArray.put(jsonObj2);

        // Set the wifi state to on
        WifiManager wifiManager = (WifiManager) mBaseContext.getSystemService( Context.WIFI_SERVICE );
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(wifiManager);
        shadowWifiManager.setWifiEnabled(false);

        controller.startConfiguration(jsonArray);

        // Handler gets set
        ShadowLooper shadowLooper = Robolectric.shadowOf(Looper.getMainLooper());
        Assert.assertTrue(shadowLooper.getScheduler().size() == 1); // Should be something enqueued. Cannot figure out how to verify it yet. May have to modify source to make it happen. Or run it and detect side effects

        // receiver registered
        ShadowApplication shadowApplication = Robolectric.shadowOf(mBaseActivity.getApplication());
        List<BroadcastReceiver> broadcastReceiverList = shadowApplication.getReceiversForIntent(new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION));
        Assert.assertTrue(broadcastReceiverList.get(0) instanceof WifiConfigurator.InnerWifiConfigurationBroadcastReceiver);

        controller.cleanupCallbacks();

        // Assert the callbacks are gone now
        Assert.assertTrue(shadowLooper.getScheduler().size() == 0);
        Assert.assertTrue(shadowApplication.getReceiversForIntent(new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION)).size() == 0);
    }


    @Test
    public void testWifiItemFailsToConfigureAndSendsFailureNotification() throws Exception {
        // This is kind of a mega test but I don't know how to break it up
        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(mBaseContext, mWifiConfigurator, false);

        // Give us some valid input to process
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj1 = new JSONObject("{ \"ssid\" = \"ssid1\", \"security\"=\"security1\", \"key\"=\"key1\" }");
        JSONObject jsonObj2 = new JSONObject("{ \"ssid\" = \"ssid2\", \"security\"=\"security2\", \"key\"=\"key2\" }");
        jsonArray.put(jsonObj1);
        jsonArray.put(jsonObj2);

        // Set the wifi state to on
        WifiManager wifiManager = (WifiManager) mBaseContext.getSystemService( Context.WIFI_SERVICE );
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(wifiManager);
        shadowWifiManager.setWifiEnabled(true);
        shadowWifiManager.setSaveConfigurationReturnValue(false);

        controller.startConfiguration(jsonArray);

        // this should be the outcome if it goes down the doSetup path and doSetup was called with no errors
        ShadowService shadowService = Robolectric.shadowOf(mWifiConfigurator);
        List<Intent> intentList = shadowService.getBroadcastIntents();

        Assert.assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", intentList.get(0).getAction());
        Assert.assertEquals("failed", intentList.get(0).getStringExtra("result"));
    }

    @Test
    public void testUnregisterReceiver() {
        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(mBaseContext, mWifiConfigurator, false);
        controller.unregisterReceiver();
        List<ShadowLog.LogItem> logItems = ShadowLog.getLogs();
        Assert.assertEquals("mWifiReceiver was null. No action taken.", logItems.get(0).msg);
    }

    @Test
    public void testGetLastIntentWithTimeoutTag() throws Exception {
        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(mBaseContext, mWifiConfigurator, false);

        // Give us some valid input to process
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj1 = new JSONObject("{ \"ssid\" = \"ssid1\", \"security\"=\"security1\", \"key\"=\"key1\" }");
        JSONObject jsonObj2 = new JSONObject("{ \"ssid\" = \"ssid2\", \"security\"=\"security2\", \"key\"=\"key2\" }");
        jsonArray.put(jsonObj1);
        jsonArray.put(jsonObj2);


        Intent intent = new Intent("com.dashwire.configure.intent.Wifi");
        intent.putExtra("featureId", "WifiId");
        intent.putExtra("featureName", "WifiName");
        intent.putExtra("featureData", jsonArray.toString());
        mWifiConfigurator.onHandleIntent(intent);

        Intent result = controller.getLastIntentWithTimeoutTag(); // This is not the same controller that the mWifiConfigurator used but it has a reference to the wifi configurator we can use

        Assert.assertEquals("WifiId", result.getStringExtra("featureId"));
        Assert.assertTrue(result.getBooleanExtra("alreadySetRetryTimeoutOnce", false));
    }


    @Test
    public void testTimeoutCallback() throws Exception {
        // This is kind of a mega test but I don't know how to break it up
        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(mBaseContext, mWifiConfigurator, false);

        // Give us some valid input to process
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj1 = new JSONObject("{ \"ssid\" = \"ssid1\", \"security\"=\"security1\", \"key\"=\"key1\" }");
        JSONObject jsonObj2 = new JSONObject("{ \"ssid\" = \"ssid2\", \"security\"=\"security2\", \"key\"=\"key2\" }");
        jsonArray.put(jsonObj1);
        jsonArray.put(jsonObj2);

        // Set the wifi state to on
        WifiManager wifiManager = (WifiManager) mBaseContext.getSystemService( Context.WIFI_SERVICE );
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(wifiManager);
        shadowWifiManager.setWifiEnabled(false);

        controller.startConfiguration(jsonArray);

        // Handler gets set
        ShadowLooper shadowLooper = Robolectric.shadowOf(Looper.getMainLooper());
        Assert.assertTrue(shadowLooper.getScheduler().size() == 1); // Should be something enqueued. Cannot figure out how to verify it yet. May have to modify source to make it happen. Or run it and detect side effects

        // Handler gets called
        shadowLooper.getScheduler().runOneTask();

        // callbacks should be cleaned up
        // Assert the callbacks are gone now
        Assert.assertTrue(shadowLooper.getScheduler().size() == 0);
        Assert.assertTrue(Robolectric.getShadowApplication().getReceiversForIntent(new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION)).size() == 0);

        // failure notification should happen
        ShadowService shadowService = Robolectric.shadowOf(mWifiConfigurator);
        List<Intent> intentList = shadowService.getBroadcastIntents();

        Assert.assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", intentList.get(0).getAction());
        Assert.assertEquals("failed", intentList.get(0).getStringExtra("result"));
    }

    @Test
    public void testBroadcastReceiverWithWifiEnabled() throws Exception {
        // Disable wifi
        // Send the initial request
        // Enable Wifi (and sent wifi change event)
        // Check for intents

        // Set the wifi state to off
        WifiManager wifiManager = (WifiManager) mBaseContext.getSystemService( Context.WIFI_SERVICE );
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(wifiManager);
        shadowWifiManager.setWifiEnabled(false);

        // Give us some valid input to process
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj1 = new JSONObject("{ \"ssid\" = \"ssid1\", \"security\"=\"security1\", \"key\"=\"key1\" }");
        JSONObject jsonObj2 = new JSONObject("{ \"ssid\" = \"ssid2\", \"security\"=\"security2\", \"key\"=\"key2\" }");
        jsonArray.put(jsonObj1);
        jsonArray.put(jsonObj2);


        Intent intent = new Intent("com.dashwire.configure.intent.Wifi");
        intent.putExtra("featureId", "WifiId");
        intent.putExtra("featureName", "WifiName");
        intent.putExtra("featureData", jsonArray.toString());

        // Send the intent
        mWifiConfigurator.onHandleIntent(intent);

        // Turn on wifi
        shadowWifiManager.setWifiEnabled(true); // This should probably send the intent but.....it doesn't
        shadowWifiManager.setWifiStateEnabled();
        Robolectric.getShadowApplication().sendBroadcast(new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION));


        Intent startedServiceIntent = Robolectric.getShadowApplication().getNextStartedService();
        Assert.assertEquals("com.dashwire.configure.intent.Wifi", startedServiceIntent.getAction());
        Assert.assertTrue(startedServiceIntent.getBooleanExtra("alreadySetRetryTimeoutOnce", false));

        // Assert the callbacks are gone now
        Assert.assertTrue(Robolectric.shadowOf(mBaseActivity.getMainLooper()).getScheduler().size() == 0);
        Assert.assertTrue(Robolectric.getShadowApplication().getReceiversForIntent(new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION)).size() == 0);
    }

    @Test
    public void testBroadcastReceiverWithWifiDisabled() throws Exception {
        // Disable wifi
        // Send the initial request
        // Enable Wifi (and sent wifi change event)
        // Check for intents

        // Set the wifi state to off
        WifiManager wifiManager = (WifiManager) mBaseContext.getSystemService( Context.WIFI_SERVICE );
        DWShadowWifiManager shadowWifiManager = Robolectric.shadowOf_(wifiManager);
        shadowWifiManager.setWifiEnabled(false);

        // Give us some valid input to process
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj1 = new JSONObject("{ \"ssid\" = \"ssid1\", \"security\"=\"security1\", \"key\"=\"key1\" }");
        JSONObject jsonObj2 = new JSONObject("{ \"ssid\" = \"ssid2\", \"security\"=\"security2\", \"key\"=\"key2\" }");
        jsonArray.put(jsonObj1);
        jsonArray.put(jsonObj2);


        Intent intent = new Intent("com.dashwire.configure.intent.Wifi");
        intent.putExtra("featureId", "WifiId");
        intent.putExtra("featureName", "WifiName");
        intent.putExtra("featureData", jsonArray.toString());

        // Send the intent
        mWifiConfigurator.onHandleIntent(intent);

        // Keep wifi turned off.
        shadowWifiManager.setWifiEnabled(false); // This should probably send the intent but.....it doesn't
        shadowWifiManager.setWifiStateDisabled();
        Robolectric.getShadowApplication().sendBroadcast(new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION));


        Intent startedServiceIntent = Robolectric.getShadowApplication().getNextStartedService();
        Assert.assertNull(startedServiceIntent);

        // Assert the callbacks are gone now
        Assert.assertTrue(Robolectric.shadowOf(mBaseActivity.getMainLooper()).getScheduler().size() == 1);
        Assert.assertTrue(Robolectric.getShadowApplication().getReceiversForIntent(new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION)).size() == 1);
    }

}
