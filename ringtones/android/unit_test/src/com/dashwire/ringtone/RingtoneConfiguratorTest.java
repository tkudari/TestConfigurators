package com.dashwire.ringtone;



import android.media.RingtoneManager;
import android.provider.MediaStore;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowApplication;
import com.xtremelabs.robolectric.shadows.ShadowContentResolver;
import com.xtremelabs.robolectric.shadows.ShadowService;
import com.xtremelabs.robolectric.tester.android.database.SimpleTestCursor;
import com.xtremelabs.robolectric.tester.android.database.TestCursor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;

import com.xtremelabs.robolectric.util.Transcript;

@RunWith(CustomRingtoneTestRunner.class)
public class RingtoneConfiguratorTest {
	
	private RingtoneConfigurator ringtoneIntentService;
	private Context context;
	private Activity activity;
    public Transcript transcript;
    private ContextWrapper contextWrapper;


	
	@Before
	public void setUp() throws Exception
	{
		ringtoneIntentService = new RingtoneConfigurator();
		activity = new Activity();
		context = activity.getApplicationContext();
        contextWrapper = new ContextWrapper(activity);
        transcript = new Transcript();

        DWRingtoneManager.lastRingtoneUri = null;
        DWRingtoneManager.lastType = -1;
	}
	
	@Test
	public void wrongUriProvidedShouldThrowException() 
	{
		JSONObject obj = new JSONObject();
		try {
			obj.put("uri", Uri.parse("uriOfNoRelevance"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONArray testArray = new JSONArray();
		testArray.put(obj);
		ringtoneIntentService.injectFeatureJSONArray(testArray);
		ringtoneIntentService.onConfigFeature();
		
		Assert.assertEquals(ringtoneIntentService.getLastRecordedException("getAudioContentUri"), "java.lang.NullPointerException");
	}
	
	@Test
	public void missingUriShouldThrowThrowAnException() 
	{
		JSONObject obj = new JSONObject();
		try {
			obj.put("non-uri", Uri.parse("non-uri-object"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONArray testArray = new JSONArray();
		testArray.put(obj);
		ringtoneIntentService.injectFeatureJSONArray(testArray);
		ringtoneIntentService.onConfigFeature();
		
		Assert.assertEquals(ringtoneIntentService.getLastRecordedException("onConfigFeature"), "org.json.JSONException");
	}


    @Test
    public void testSetRingtoneCall() {
        ringtoneIntentService.setRingtone("call", Uri.parse("http://something"));
        Assert.assertEquals("http://something", DWRingtoneManager.lastRingtoneUri.toString());
        Assert.assertEquals(1, DWRingtoneManager.lastType);
    }

    @Test
    public void testSetRingtoneSMS() {
        ringtoneIntentService.setRingtone("sms", Uri.parse("http://something"));
        Assert.assertEquals("http://something", DWRingtoneManager.lastRingtoneUri.toString());
        Assert.assertEquals(2, DWRingtoneManager.lastType);
    }

    @Test
    public void testSetRingtoneNothing() {
        ringtoneIntentService.setRingtone("tejus", Uri.parse("http://something"));
        Assert.assertEquals(null, DWRingtoneManager.lastRingtoneUri);
        Assert.assertEquals(-1, DWRingtoneManager.lastType);
    }


    @Test(expected=IllegalArgumentException.class)
    public void testGetAudioContentUriNoResults() {
        Uri uri = Uri.parse("http://something");
        ShadowApplication shadowApplication = Robolectric.getShadowApplication();
        ShadowContentResolver shadowContentResolver = Robolectric.shadowOf(shadowApplication.getContentResolver());
        SimpleTestCursor simpleTestCursor = new SimpleTestCursor() {
            @Override
            public int getInt(int columnIndex) {
                return (Integer) results[0][columnIndex];
            }
        };
        Object[][] retval = new Object[0][0];
        simpleTestCursor.setResults(retval);
        shadowContentResolver.setCursor(simpleTestCursor);

        DWShadowMediaStoreAudioMedia.ContentUriForPath = Uri.parse("http://returnuri/somepath");

        Uri result = ringtoneIntentService.getAudioContentUri(uri);
    }

    @Test
    public void testGetAudioContentUriReturnsUri() {
        Uri uri = Uri.parse("http://something");
        ShadowApplication shadowApplication = Robolectric.getShadowApplication();
        ShadowContentResolver shadowContentResolver = Robolectric.shadowOf(shadowApplication.getContentResolver());
        SimpleTestCursor simpleTestCursor = new SimpleTestCursor() {
            @Override
            public int getInt(int columnIndex) {
                return (Integer) results[0][columnIndex];
            }
        };
        Object[][] retval = new Object[1][1];
        retval[0][0] = new Integer(1000);
        simpleTestCursor.setResults(retval);
        shadowContentResolver.setCursor(simpleTestCursor);

        DWShadowMediaStoreAudioMedia.ContentUriForPath = Uri.parse("http://returnuri/somepath");

        Uri result = ringtoneIntentService.getAudioContentUri(uri);
        Assert.assertEquals("http://returnuri/somepath/1000", result.toString());
    }



}
