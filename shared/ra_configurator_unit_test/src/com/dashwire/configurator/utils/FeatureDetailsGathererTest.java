package com.dashwire.configurator.utils;

import static org.junit.Assert.*;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.configurator.events.Feature;
import com.xtremelabs.robolectric.res.ResourceLoader;
import com.xtremelabs.robolectric.shadows.ShadowResources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowLog;
import com.xtremelabs.robolectric.shadows.ShadowLog.LogItem;

@RunWith(RobolectricTestRunner.class)
public class FeatureDetailsGathererTest {

	// NOTE: These tests are actually pulling from the resource files b/c Robolectric is awesome!!! :)

	@Test
	public void testGatherFeatureDetailsWithValidFeatureId() throws Exception {
		FeatureDetailsGatherer gatherer = new FeatureDetailsGatherer();
		Activity act = new Activity();

		Intent i = new Intent();
		i.putExtra("featureId", "dashwall_22");
		Feature f = new Feature(i);

		Feature result = gatherer.gatherFeatureDetails(act.getApplicationContext(), f);

		assertEquals("dashwall_22", result.getFeatureId());
		assertEquals("Ringtone", result.getFeatureName());
		assertTrue("JSON feature data did not contain expected result", result.getFeatureData().contains("Carina.ogg"));
	}

	@Test(expected = NullPointerException.class)
	public void testGatherFeatureDetailsWithInvalidFeatureId() throws Exception {
		FeatureDetailsGatherer gatherer = new FeatureDetailsGatherer();
		Activity act = new Activity();

		Intent i = new Intent();
		i.putExtra("featureId", "blah");
		Feature f = new Feature(i);

		Feature result = gatherer.gatherFeatureDetails(act.getApplicationContext(), f);
	}

	@Test(expected = NullPointerException.class)
	public void testGatherFeatureDetailsWithNullFeatureId() throws Exception {
		FeatureDetailsGatherer gatherer = new FeatureDetailsGatherer();
		Activity act = new Activity();

		Intent i = new Intent();
		Feature f = new Feature(i);

		Feature result = gatherer.gatherFeatureDetails(act.getApplicationContext(), f);
	}


}
