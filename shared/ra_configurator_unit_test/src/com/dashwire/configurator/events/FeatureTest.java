package com.dashwire.configurator.events;

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
public class FeatureTest {

    @Before
    public void setup() {
        DashLogger.setDebugMode(true);
    }

    @Test
    public void testOnCreateFeatureWithNullIntent() {
    	Feature featureNull = new Feature(null);
    	assertNull("FeatureId is Null",featureNull.getFeatureId());
    	assertNull("FeatureName is Null", featureNull.getFeatureName());
    	assertNull("Feature intent is Null", featureNull.getIntent());
    }

    @Test
    public void testfeatureFieldsBasedonIntentPassed() {
    	Intent newIntent = new Intent();
    	newIntent.putExtra("featureId","testFeatureId");
    	newIntent.putExtra("featureName","testFeatureName");
    	newIntent.putExtra("featureData","testFeatureData");
    	
    	Feature feature = new Feature(newIntent);
    	assertEquals("testFeatureId",feature.getFeatureId());
    	assertEquals("testFeatureName", feature.getFeatureName());
    	assertEquals("testFeatureData", feature.getFeatureData());
    }
    
    @Test
    public void testForIntentExtraCreatedWhenFeatureFieldsSet() {
    	Feature feature = new Feature(new Intent());
    	
    	feature.setFeatureId("testFeatureId");
    	feature.setFeatureName("testFeatureName");
    	feature.setFeatureData("testFeatureData");
    	
    	assertEquals("testFeatureId", feature.getIntent().getStringExtra("featureId"));
    	assertEquals("testFeatureName", feature.getIntent().getStringExtra("featureName"));
    	assertEquals("testFeatureData", feature.getIntent().getStringExtra("featureData"));
    }
    
    @Test
    public void testForIntentExtraUpdatedWhenFeatureFieldsSet() {
    	
    	Intent newIntent = new Intent();
    	newIntent.putExtra("featureId","oldFeatureId");
    	newIntent.putExtra("featureName","oldFeatureName");
    	newIntent.putExtra("featureData","oldFeatureData");
    	
    	Feature feature = new Feature(newIntent);
    	
    	feature.setFeatureId("testFeatureId");
    	feature.setFeatureName("testFeatureName");
    	feature.setFeatureData("testFeatureData");
    	
    	assertEquals("testFeatureId", feature.getIntent().getStringExtra("featureId"));
    	assertEquals("testFeatureName", feature.getIntent().getStringExtra("featureName"));
    	assertEquals("testFeatureData", feature.getIntent().getStringExtra("featureData"));
    }

   
}
