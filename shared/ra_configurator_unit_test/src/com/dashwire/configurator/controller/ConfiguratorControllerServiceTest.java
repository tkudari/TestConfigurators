package com.dashwire.configurator.controller;

import android.content.Context;
import com.dashwire.configurator.events.Feature;
import com.dashwire.configurator.utils.FeatureDetailsGatherer;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.runner.RunWith;

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

public class ConfiguratorControllerServiceTest {

    public class MyFeatureDetailsGatherer extends FeatureDetailsGatherer {
        boolean throwEx = false;
        Feature featureToReturn = null;


        public void setFeatureToReturn(Feature f) {
            featureToReturn = f;
        }

        public void setThrowException(boolean throwIt) {
            throwEx = throwIt;
        }

        @Override
        public Feature gatherFeatureDetails(Context context, Feature feature) {
            if (!throwEx) {
                return featureToReturn;
            } else {
            	Log.e("ConfiguratorControllerServiceTest","Stupid Exception");
            }
			return feature;
        }
    }


    @Test
    public void testOnStartCommandWithNullIntent() {
        ConfiguratorControllerService svc = new ConfiguratorControllerService();

        int retval = svc.onStartCommand(null, 0, 0);

        assertEquals(ConfiguratorControllerService.START_STICKY, retval);
        List<LogItem> logs = ShadowLog.getLogs();
        assertTrue("No logs found", logs.size() > 0);
        assertEquals("onStartCommand intent was null. Taking no action.", logs.get(0).msg);
    }

    @Test
    public void testOnStartCommandInvalidFeatureIntent() {
        ConfiguratorControllerService svc = new ConfiguratorControllerService();
        Intent i = new Intent();

        int retval = svc.onStartCommand(i, 0, 0);

        assertEquals(ConfiguratorControllerService.START_STICKY, retval);

        List<Intent> intents = Robolectric.shadowOf(svc).getBroadcastIntents();
        assertTrue("No intents found", intents.size() > 0);
        assertEquals("failed", intents.get(0).getStringExtra("result"));
        assertEquals("FeatureId missing", intents.get(0).getStringExtra("reason"));
    }

    @Test
    public void testOnStartCommandOnlyFeatureIdSpecifiedAndFeatureGenerationThrowsException() {
        ConfiguratorControllerService svc = new ConfiguratorControllerService();
        MyFeatureDetailsGatherer fdg = new MyFeatureDetailsGatherer();
        fdg.setThrowException(true);
        svc.setFeatureDetailsGatherer(fdg);

        Intent i = new Intent();
        i.putExtra("featureId", "blah");


        int retval = svc.onStartCommand(i, 0, 0);

        assertEquals(ConfiguratorControllerService.START_STICKY, retval);
        List<Intent> intents = Robolectric.shadowOf(svc).getBroadcastIntents();
        assertTrue("No intents found", intents.size() > 0);
        assertEquals("failed", intents.get(0).getStringExtra("result"));
        assertEquals("My stupid mock exception", intents.get(0).getStringExtra("reason"));
    }

    @Test
    public void testOnStartCommandFeatureIdSpecifiedButFeatureGenerationOnlyReturnsPartialResult() {
        ConfiguratorControllerService svc = new ConfiguratorControllerService();
        MyFeatureDetailsGatherer fdg = new MyFeatureDetailsGatherer();
        Intent i = new Intent();
        i.putExtra("featureId", "blah");

        Intent featureSetupIntent = new Intent();
        featureSetupIntent.putExtra("featureId", "blah");
        featureSetupIntent.putExtra("featureName", "moreblah");
        fdg.setFeatureToReturn(new Feature(featureSetupIntent));
        svc.setFeatureDetailsGatherer(fdg);

        int retval = svc.onStartCommand(i, 0, 0);

        assertEquals(ConfiguratorControllerService.START_STICKY, retval);
        List<Intent> intents = Robolectric.shadowOf(svc).getBroadcastIntents();
        assertTrue("No intents found", intents.size() > 0);
        assertEquals("failed", intents.get(0).getStringExtra("result"));
        assertEquals("Unable to determine FeatureName or FeatureData", intents.get(0).getStringExtra("reason"));
    }


    @Test
    public void testOnStartCommandFeatureIdSpecifiedAndFeatureGenerationReturnsFullResult() {
        ConfiguratorControllerService svc = new ConfiguratorControllerService();
        MyFeatureDetailsGatherer fdg = new MyFeatureDetailsGatherer();
        Intent i = new Intent();
        i.putExtra("featureId", "blah");

        Intent featureSetupIntent = new Intent();
        featureSetupIntent.putExtra("featureId", "blah");
        featureSetupIntent.putExtra("featureName", "moreblah");
        featureSetupIntent.putExtra("featureData", "evenmoreblah");
        fdg.setFeatureToReturn(new Feature(featureSetupIntent));
        svc.setFeatureDetailsGatherer(fdg);

        int retval = svc.onStartCommand(i, 0, 0);

        assertEquals(ConfiguratorControllerService.START_STICKY, retval);
        Intent nextSvc = Robolectric.getShadowApplication().getNextStartedService();
        assertEquals("blah", nextSvc.getExtras().getString("featureId"));
        assertEquals("moreblah", nextSvc.getExtras().getString("featureName"));
        assertEquals("evenmoreblah", nextSvc.getExtras().getString("featureData"));
    }

    @Test
    public void testOnStartCommandFullySpecifiedIntent() {
        ConfiguratorControllerService svc = new ConfiguratorControllerService();
        MyFeatureDetailsGatherer fdg = new MyFeatureDetailsGatherer();
        Intent i = new Intent();
        i.putExtra("featureId", "blah");
        i.putExtra("featureName", "moreblah");
        i.putExtra("featureData", "evenmoreblah");

        int retval = svc.onStartCommand(i, 0, 0);

        assertEquals(ConfiguratorControllerService.START_STICKY, retval);
        Intent nextSvc = Robolectric.getShadowApplication().getNextStartedService();
        assertEquals("blah", nextSvc.getExtras().getString("featureId"));
        assertEquals("moreblah", nextSvc.getExtras().getString("featureName"));
        assertEquals("evenmoreblah", nextSvc.getExtras().getString("featureData"));
    }

}
