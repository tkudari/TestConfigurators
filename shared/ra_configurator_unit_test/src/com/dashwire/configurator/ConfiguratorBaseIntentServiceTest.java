package com.dashwire.configurator;

import static org.junit.Assert.*;

import java.util.List;

import android.util.Log;
import com.dashwire.base.debug.DashLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowLog;
import com.xtremelabs.robolectric.shadows.ShadowLog.LogItem;

@RunWith(RobolectricTestRunner.class)
public class ConfiguratorBaseIntentServiceTest {

    class MyTestService extends ConfiguratorBaseIntentService {
        @Override
        public void onConfigFeature() {
            if (throw_exception) {
                String s = null;
                s.charAt(0); // NPE
            } else {
                Log.v("TESTCODE", "onConfigFeature called");
            }
        }

        private boolean throw_exception = false;

        public MyTestService(boolean throwExceptionInGetApplicationContext) {
            throw_exception = throwExceptionInGetApplicationContext;
        }

        public MyTestService() {
            super();
        }
    }

    @Before
    public void setup() {
        DashLogger.setDebugMode(true);
    }

    String getMessageFromArray(LogItem[] messages, int position) {
        try {
            return messages[position].msg;
        } catch (Exception ex) {
            return "Log message was not found because " + ex.toString();
        }
    }

    @Test
    public void testOnHandleIntentWithInvalidAction() {
        MyTestService svc = new MyTestService();
        Intent i = new Intent("BogusAction");
        svc.onHandleIntent(i);
        List<LogItem> loglist = ShadowLog.getLogs();
        LogItem[] li_array = loglist.toArray(new LogItem[loglist.size()]);
        assertEquals("Invalid action : BogusAction", getMessageFromArray(li_array, 1));
    }

    @Test
    public void testOnHandleIntentWithBlankAction() {
        MyTestService svc = new MyTestService();
        Intent i = new Intent();
        svc.onHandleIntent(i);
        List<LogItem> loglist = ShadowLog.getLogs();
        LogItem[] li_array = loglist.toArray(new LogItem[loglist.size()]);
        assertEquals("Invalid action : null", getMessageFromArray(li_array, 1));
    }


    @Test
    public void testOnHandleIntentCallsOnConfigWhenValidParametersAvailable() {
        MyTestService svc = new MyTestService();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureName", "blah2");
        i.putExtra("featureData", "[1]");
        svc.onHandleIntent(i);

        List<LogItem> loglist = ShadowLog.getLogs();
        LogItem[] li_array = loglist.toArray(new LogItem[loglist.size()]);
        assertEquals("onConfigFeature called", getMessageFromArray(li_array, 5));
    }


    @Test
    public void testOnHandleIntentReportsFailureIfExceptionThrown() {
        MyTestService svc = new MyTestService(true);
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureName", "blah2");
        i.putExtra("featureData", "[1]");
        svc.onHandleIntent(i);

        List<Intent> intents = Robolectric.getShadowApplication().getBroadcastIntents();
        assertEquals("failed", intents.get(0).getExtras().getString("result"));
        assertEquals(null, intents.get(0).getExtras().getString("reason"));
    }

    @Test
    public void testOnHandleIntentReportsFailureIfNoFeatureId() {
        MyTestService svc = new MyTestService();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureName", "blah2");
        i.putExtra("featureData", "[1]");
        svc.onHandleIntent(i);

        List<Intent> intents = Robolectric.getShadowApplication().getBroadcastIntents();
        assertEquals("failed", intents.get(0).getExtras().getString("result"));
        assertEquals("Missing parameters", intents.get(0).getExtras().getString("reason"));
    }

    @Test
    public void testOnHandleIntentReportsFailureIfNoFeatureName() {
        MyTestService svc = new MyTestService();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureData", "[1]");
        svc.onHandleIntent(i);

        List<Intent> intents = Robolectric.getShadowApplication().getBroadcastIntents();
        assertEquals("failed", intents.get(0).getExtras().getString("result"));
        assertEquals("Missing parameters", intents.get(0).getExtras().getString("reason"));
    }

    @Test
    public void testOnHandleIntentReportsFailureIfNoFeatureData() {
        MyTestService svc = new MyTestService();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureName", "blah2");
        svc.onHandleIntent(i);

        List<Intent> intents = Robolectric.getShadowApplication().getBroadcastIntents();
        assertEquals("failed", intents.get(0).getExtras().getString("result"));
        assertEquals(null, intents.get(0).getExtras().getString("reason"));
    }

    @Test
    public void testOnHandleIntentReportsFailureIfFeatureDataHasInvalidJSON() {
        MyTestService svc = new MyTestService();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureName", "blah2");
        i.putExtra("featureData", "!?#/345/35126");
        svc.onHandleIntent(i);

        List<Intent> intents = Robolectric.getShadowApplication().getBroadcastIntents();
        assertEquals("failed", intents.get(0).getExtras().getString("result"));
        assertEquals("A JSONArray text must start with '[' at character 1", intents.get(0).getExtras().getString("reason"));
    }


    @Test
    public void testOnHandleIntentReportsFailureIfFeatureDataHasEmptyJSONArray() {
        MyTestService svc = new MyTestService();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureName", "blah2");
        i.putExtra("featureData", "[]");
        svc.onHandleIntent(i);

        List<Intent> intents = Robolectric.getShadowApplication().getBroadcastIntents();
        assertEquals("failed", intents.get(0).getExtras().getString("result"));
        assertEquals("Missing parameters", intents.get(0).getExtras().getString("reason"));
    }


    @Test
    public void testOnFailure() {
        // Needed to set up variables for onFailure call
        MyTestService svc = new MyTestService();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureName", "blah2");
        i.putExtra("featureData", "{}");
        svc.onHandleIntent(i); // onFailure gets called automatically in this case

        Intent result = Robolectric.shadowOf(svc).getBroadcastIntents().get(0);
        assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", result.getAction());
        assertEquals("blah1", result.getStringExtra("featureId"));
        assertEquals("blah2", result.getStringExtra("featureName"));
        assertEquals("{}", result.getStringExtra("featureData"));
        assertEquals("failed", result.getStringExtra("result"));
    }

    @Test
    public void testOnSuccess() {
        // Needed to set up variables for onFailure call
        MyTestService svc = new MyTestService();
        Intent i = new Intent("com.dashwire.configure.intent.AnyFeature");
        i.putExtra("featureId", "blah1");
        i.putExtra("featureName", "blah2");
        i.putExtra("featureData", "[1]");
        svc.onHandleIntent(i);
        svc.onSuccess();

        Intent result = Robolectric.shadowOf(svc).getBroadcastIntents().get(0);
        assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", result.getAction());
        assertEquals("blah1", result.getStringExtra("featureId"));
        assertEquals("blah2", result.getStringExtra("featureName"));
        assertEquals("[1]", result.getStringExtra("featureData"));
        assertEquals("success", result.getStringExtra("result"));
    }

}
