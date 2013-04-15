package com.dashwire.r2g.intg.test;

import java.lang.reflect.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ServiceTestCase;
import android.util.Log;

import com.dashwire.config.ui.IconStartActivity;
import com.dashwire.r2g.intg.test.util.IntegrationTestUtil;
import com.dashwire.testing.helper.controller.TestingHelperService;
import com.dashwire.testing.helper.verifier.RingtoneVerifier;
import com.dashwire.testing.helper.verifier.WifiVerifier;
import com.jayway.android.robotium.solo.Solo;

public class SmokeTest extends ActivityInstrumentationTestCase2<IconStartActivity> {

	WebDriver slnmDriver;
	Context m_context;
	R2G_UI_Helper r2gHelper;
	TestingHelperService testingHelperService;
	wifi_testCases wifiTestCases;
	Ringtone_testCases ringtoneTestCases;
	static JSONArray ringtoneAssets;
	static JSONArray ringtoneAssetsBackup;

	protected static final String TAG = "SmokeTest";

	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, "Entering onServiceConnected.");
			if (testingHelperService != null) {
				Log.e(TAG, "testingHelperService not null before onServiceConnected");
			}
			testingHelperService = ((TestingHelperService.TestingHelperBinder) service).getService();
			if (testingHelperService != null) {
				Log.v(TAG, "testingHelperService connected");
			} else {
				Log.v(TAG, "Error on connecting testingHelperService");
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			Log.d(TAG, "Entering onServiceDisconnected.");
			if (testingHelperService != null) {
				testingHelperService = null;
			}
			Log.v(TAG, "testingHelperService disconnected");
		}
	};

	// Use this method when setting project as testing project for a specific
	// android project
	@SuppressWarnings("deprecation")
	public SmokeTest() {
		super("com.dashwire.config.ui", IconStartActivity.class);
	}

	void doBindTestingHelperService(Context context) {
		Log.v(TAG, "Entering doBindTestingHelperService.");

		Intent featureControllerIntent = new Intent(context, TestingHelperService.class);
		context.bindService(featureControllerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	public void setUp() throws Exception {
		Log.d("QA test", "on setUp()");
		Solo rbtmSolo = new Solo(getInstrumentation(), getActivity());
		setActivityInitialTouchMode(false);
		// Get activity context
		m_context = getInstrumentation().getTargetContext();
		r2gHelper = new R2G_UI_Helper(rbtmSolo);
		doBindTestingHelperService(m_context);
		Thread.sleep(3000);
		wifiTestCases = new wifi_testCases(m_context, IntegrationTestUtil.ENVIRONMENT, IntegrationTestUtil.USER, IntegrationTestUtil.PSWD, r2gHelper);
	}

	public void test_SmokeTest() {

		String defaultRingtone, defaultNotificaiton;

		try {

			Log.v(TAG, "test202_validateRingtone - getting Asset for feature");
			JSONArray ringtoneAssets = testingHelperService.getAssetForFeature(getInstrumentation().getContext(), "ringtones");

			Log.v(TAG, "test202_validateRingtone - ringtone Config = " + ringtoneAssets.get(1).toString());

			JSONArray notificationAssets = testingHelperService.getAssetForFeature(getInstrumentation().getContext(), "notifications");

			Log.v(TAG, "test202_validateRingtone - notification Config = " + notificationAssets.get(1).toString());

			JSONArray wallpaperAssets = testingHelperService.getAssetForFeature(getInstrumentation().getContext(), "wallpapers");

			Log.v(TAG, "test202_validateRingtone - wallpaper Config = " + wallpaperAssets.get(1).toString());
			
			JSONArray shortcutsAssets = testingHelperService.getAssetForFeature(getInstrumentation().getContext(), "shortcuts");

			Log.v(TAG, "test202_validateRingtone - Shortcuts Config = " + shortcutsAssets.get(1).toString());
			
			JSONArray wifiAssets = testingHelperService.getAssetForFeature(getInstrumentation().getContext(), "networks");
			
			JSONArray bookmarksAssets = testingHelperService.getAssetForFeature(getInstrumentation().getContext(), "bookmarks");
			
			JSONArray contactsAssets = testingHelperService.getAssetForFeature(getInstrumentation().getContext(), "contacts");
			
			JSONArray emailAccountsAssets = testingHelperService.getAssetForFeature(getInstrumentation().getContext(), "emails");
			
			JSONArray applicationsAssets = testingHelperService.getAssetForFeature(getInstrumentation().getContext(), "applications");

			JSONObject rootConfig = new JSONObject();
			JSONObject configJSON = new JSONObject();

			JSONArray ringtoneConfigArray = new JSONArray();
			ringtoneConfigArray.put(ringtoneAssets.get(1));
			ringtoneConfigArray.put(notificationAssets.get(1));

			JSONObject testingRingtoneJSON = (JSONObject) ringtoneAssets.get(1);
			JSONObject testingNotificaitonJSON = (JSONObject) notificationAssets.get(1);

			JSONArray wallpaperConfigArray = new JSONArray();
			wallpaperConfigArray.put(wallpaperAssets.get(1));
			
			if (wifiAssets != null)
			{
				JSONArray wifiConfigArray = new JSONArray();
				wifiConfigArray.put(wifiAssets.get(0));
				wifiConfigArray.put(wifiAssets.get(1));
				wifiConfigArray.put(wifiAssets.get(2));
				configJSON.put("networks", wifiConfigArray);
			}
			
			if (bookmarksAssets != null)
			{
				JSONArray bookmarkConfigArray = new JSONArray();
				bookmarkConfigArray.put(bookmarksAssets.get(0));
				bookmarkConfigArray.put(bookmarksAssets.get(1));
				configJSON.put("bookmarks", bookmarkConfigArray);
			}
			
			if (contactsAssets != null)
			{
				JSONArray contactsConfigArray = new JSONArray();
				contactsConfigArray.put(contactsAssets.get(0));
				contactsConfigArray.put(contactsAssets.get(1));
				configJSON.put("contacts", contactsConfigArray);
			}
			
			if (emailAccountsAssets != null)
			{
				JSONArray emailAccountsConfigArray = new JSONArray();
				emailAccountsConfigArray.put(emailAccountsAssets.get(0));
				emailAccountsConfigArray.put(emailAccountsAssets.get(1));
				emailAccountsConfigArray.put(emailAccountsAssets.get(2));
				emailAccountsConfigArray.put(emailAccountsAssets.get(3));
				emailAccountsConfigArray.put(emailAccountsAssets.get(4));
				configJSON.put("emails", emailAccountsConfigArray);
			}
			
			if (applicationsAssets != null)
			{
				JSONArray applicationsConfigArray = new JSONArray();
				applicationsConfigArray.put(applicationsAssets.get(0));
				applicationsConfigArray.put(applicationsAssets.get(1));
				configJSON.put("applications", applicationsConfigArray);
			}
			
			if (shortcutsAssets != null)
			{
				JSONArray shortcutsConfigArray = new JSONArray();
				
				for (int i=1; i<=3; i++)
				{
					JSONObject shortcutJSON = (JSONObject) shortcutsAssets.get(i);
					shortcutJSON.put("screen", 1);
					shortcutJSON.put("x", i);
					shortcutJSON.put("y", 0);
					shortcutJSON.put("rows", 1);
					shortcutJSON.put("cols", 1);
				                  
					shortcutsConfigArray.put(shortcutJSON);
				}

				Log.v(TAG,"shortcuts config = " + shortcutsConfigArray.toString());
				configJSON.put("shortcuts", shortcutsConfigArray);
			}

			configJSON.put("ringtones", ringtoneConfigArray);
			configJSON.put("wallpapers", wallpaperConfigArray);

			rootConfig.put("config", configJSON);
			rootConfig.put("model", Build.DEVICE);

			Log.v(TAG, "ringtoneConfig to Server = " + rootConfig.toString());
			r2gHelper.sendR2GConfig(IntegrationTestUtil.ENVIRONMENT, IntegrationTestUtil.USER, IntegrationTestUtil.PSWD, rootConfig.toString(), "SMOKE");

			defaultRingtone = RingtoneVerifier.getDefaultRingTone(m_context, RingtoneVerifier.ringToneType.CALL);
			defaultNotificaiton = RingtoneVerifier.getDefaultRingTone(m_context, RingtoneVerifier.ringToneType.SMS);

			Log.v(TAG, "Default ringtone = " + defaultRingtone + "\r\nExpected ringtone = " + testingRingtoneJSON.get("title"));
			Log.v(TAG, "Default notification = " + defaultNotificaiton + "\r\nExpected notificaion = " + testingNotificaitonJSON.get("title"));

			assertEquals("Comparing ringtones", defaultRingtone, testingRingtoneJSON.get("title"));
			assertEquals("Comparing notifications", defaultNotificaiton, testingNotificaitonJSON.get("title"));
			
			if (wifiAssets != null)
			{
				JSONObject testingWifi_1= (JSONObject) wifiAssets.get(0);
				JSONObject testingWifi_2= (JSONObject) wifiAssets.get(1);
				JSONObject testingWifi_3= (JSONObject) wifiAssets.get(2);
				
				WifiVerifier.validateWifi(m_context, testingWifi_1.getString("ssid"));
				WifiVerifier.validateWifi(m_context, testingWifi_2.getString("ssid"));
				WifiVerifier.validateWifi(m_context, testingWifi_3.getString("ssid"));
			}

			testingHelperService.takeScreenShot("android.settings.SOUND_SETTINGS", "Ringtone");
			testingHelperService.takeScreenShot("android.intent.action.MAIN", "Wallpaper");
			testingHelperService.takeScreenShot("android.settings.WIFI_SETTINGS", "Wifi");

		} catch (JSONException e) {
			Log.e(TAG, "JSONException = " + e.getMessage());
		}
	}
	
	private Context getTestContext() {
		try {
			Method getTestContext = ServiceTestCase.class.getMethod("getTestContext");
			return (Context) getTestContext.invoke(this);
		} catch (final Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}
}
