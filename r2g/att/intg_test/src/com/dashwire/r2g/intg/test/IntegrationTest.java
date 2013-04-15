package com.dashwire.r2g.intg.test;


import java.lang.reflect.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import android.content.ComponentName;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ServiceTestCase;
import android.util.Log;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.dashwire.config.ui.IconStartActivity;
import com.dashwire.r2g.intg.test.util.IntegrationTestUtil;
import com.dashwire.testing.helper.controller.TestingHelperService;
import com.dashwire.testing.helper.verifier.RingtoneVerifier;


import com.jayway.android.robotium.solo.Solo;

public class IntegrationTest extends ActivityInstrumentationTestCase2 <IconStartActivity> {

	WebDriver slnmDriver;
	Context m_context;
	R2G_UI_Helper r2gHelper;
	TestingHelperService testingHelperService;
	wifi_testCases wifiTestCases;
	Ringtone_testCases ringtoneTestCases;
	static JSONArray ringtoneAssets;
	static JSONArray ringtoneAssetsBackup;


	protected static final String TAG = "QA test";
	 
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
		

	//Use this method when setting project as testing project for a specific android project
	@SuppressWarnings("deprecation")
	public IntegrationTest() {
		super("com.dashwire.config.ui", IconStartActivity.class);
		Log.d("QA test", "on IntegrationTest()");
	}
	
	void doBindTestingHelperService(Context context) {
		Log.v(TAG, "Entering doBindTestingHelperService.");

		Intent featureControllerIntent = new Intent(context, TestingHelperService.class);
		context.bindService(featureControllerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	public void setUp() throws Exception {
		Log.d("QA test", "on setUp()");
		Solo rbtmSolo = new Solo(getInstrumentation(),getActivity());
		setActivityInitialTouchMode(false);
		//Get activity context
		m_context = getInstrumentation().getTargetContext();
		r2gHelper = new R2G_UI_Helper(rbtmSolo);
		doBindTestingHelperService(m_context);
		Thread.sleep(3000);
		wifiTestCases = new wifi_testCases(m_context, IntegrationTestUtil.ENVIRONMENT, IntegrationTestUtil.USER, IntegrationTestUtil.PSWD, r2gHelper);
		//ringtoneTestCases = new Ringtone_testCases(m_context, ENVIRONMENT, USER, PSWD, r2gHelper, testingHelperService);	
	}
	
	
	/***
	 * Add a WEP wifi to device	
	 */
	public void test104_setWifi_NonSecure(){
		
		//TODO: replace parameters on createWifiConfigFile() to use values from JSONArray wifiAssets = IntgTestingDataMocks.getTestingConfigFromDataMocks(getInstrumentation().getContext(), "networks");		
		String configFile = createWifiConfigFiles("aQANoSecure","none","");		
		wifiTestCases.runTestCase(configFile);
				
	}
	
	/***
	 * Add a WEP wifi to device	
	 */
	public void test106_setWifi_WEP(){
		
		String configFile = createWifiConfigFiles("aQAWEP","wep","A2C7BB35B9");
		wifiTestCases.runTestCase(configFile);	
			
	}
	
	/***
	 * Add a WPA2 wifi to device	
	 */
	public void test108_setWifi_WPA2(){
		
		String configFile = createWifiConfigFiles("Dashwire Private","wpa2-psk","are we agile yet?");
		wifiTestCases.runTestCase(configFile);
		
	}
	
	/***
	 * Add a WPA2 wifi not in range invalid password
	 */
	public void test110_setWifi_WPA2_notInRange_invalidPassword(){
		
		String configFile = createWifiConfigFiles("aQAWepInvalidPswd","wep","a2b4567890");
		wifiTestCases.runTestCase(configFile);
		
	}	
	
	/***
	 * Add a WEP not in Range (valid password) wifi to device	
	 */
	public void test112_setWifi_WEP_notInRange(){
		
		String configFile = createWifiConfigFiles("aQAWepWifi","wep","A2C7BB35B9");
		wifiTestCases.runTestCase(configFile);

	}	
	
	/***
	 * Add a WEP not in Range (Invalid password) wifi to device	
	 */
	public void test114_setWifi_WEP_notInRange_InvalidPassword(){
		
		String configFile = createWifiConfigFiles("1Wpa2InvPswd","wpa2-psk","a2b4567890");
		wifiTestCases.runTestCase(configFile);
		
	}
	

	// RINGTONES test cases
	//TODO:create test cases methods dynamically depending on how many assets gathered	
	
	public void test200_validateRingtone() throws JSONException{
		
		Ringtone_testCases.assetsFromGatheringTool = testingHelperService.getAssetForFeature(getTestContext(), "ringtones");	
		Log.d("QA test", "200 test case");
		this.runRingtoneTest(0);
			
	}

	public void test201_validateRingtone(){	
		
		Log.d("QA test", "203 test case");
		this.runRingtoneTest(1);
	}	

	public void test202_validateRingtone(){	
		
		Log.d("QA test", "204 test case");
		this.runRingtoneTest(2);
	}
	
	public void test203_validateRingtone(){	
		
		Log.d("QA test", "203 test case");
		this.runRingtoneTest(3);
	}	

	public void test204_validateRingtone(){	
		
		Log.d("QA test", "206 test case");
		this.runRingtoneTest(4);
	}		

	
	//NOTIFICATION test cases
	//TODO:create test cases methods dynamically depending on how many assets gathered	
	public void test300_validateRingtone(){
		Log.d("QA test", "300 test case");
		Ringtone_testCases.assetsFromGatheringTool = null;
		Ringtone_testCases.assetsFromGatheringTool = testingHelperService.getAssetForFeature(getTestContext(), "notifications");		
		this.runNotificationTest(0);
	}

	public void test301_validateRingtone(){	
		Log.d("QA test", "301 test case");
		this.runNotificationTest(1);
	}	

	public void test302_validateRingtone(){	
		
		Log.d("QA test", "302 test case");
		this.runNotificationTest(2);
	}
	
	public void test303_validateRingtone(){	
		
		Log.d("QA test", "303 test case");
		this.runNotificationTest(3);
	}	

	public void test304_validateRingtone(){	
		
		Log.d("QA test", "304 test case");
		this.runNotificationTest(4);
	}			

	
	
	public void runNotificationTest(int i){
		runRingtoneTest(i, RingtoneVerifier.ringToneType.SMS);
	}
	
	
	public void runRingtoneTest(int i){
		
		runRingtoneTest(i, RingtoneVerifier.ringToneType.CALL);
	}
	
	
	
	public  void runRingtoneTest(int i, RingtoneVerifier.ringToneType ringtoneType){
		String defaultRingtone = null;
		String featureName = "Ringtone";
		String screenToTest = "android.settings.SOUND_SETTINGS";
		JSONObject theRingtone = null;
		
		ringtoneAssets = Ringtone_testCases.assetsFromGatheringTool;
				

		try {	
								
			theRingtone =(JSONObject) Ringtone_testCases.assetsFromGatheringTool.get(i);			
			Log.v(TAG,"Ringtone to configure = " + theRingtone.get("title") );
			
			String ringtoneConfigFile =
					createRingtoneConfigFiles(theRingtone);
			
			r2gHelper.sendR2GConfig(IntegrationTestUtil.ENVIRONMENT,IntegrationTestUtil.USER,IntegrationTestUtil.PSWD, ringtoneConfigFile, "INTG");

		} catch (JSONException e) {
			Log.v(TAG,"JSONException " + e.toString());			
		} catch (Exception e) {
			Log.v(TAG,"JSONException " + e.getMessage() );
		}
		
		finally {
			try {
				defaultRingtone = RingtoneVerifier.getDefaultRingTone(m_context, ringtoneType);
				assertEquals("Comparing ringtones", theRingtone.get("title").toString().trim(), defaultRingtone.trim());
				testingHelperService.takeScreenShot(screenToTest, featureName);
				
			} catch (JSONException e) {
				Log.v(TAG,"JSONException on finally " + e.getMessage() );
			}
		}
	}
		
	public  String createRingtoneConfigFiles(JSONObject ringtone){
		JSONObject rootConfig = new JSONObject();
		JSONObject configJSON = new JSONObject();
		JSONArray ringtoneConfigArray = new JSONArray();		
		
		try {
			ringtoneConfigArray.put(ringtone);
			configJSON.put("ringtones", ringtoneConfigArray);
			rootConfig.put("config", configJSON);
			rootConfig.put("model",Build.DEVICE);
			Log.v(TAG,"Device model = " + Build.DEVICE);
			Log.v(TAG,"ringtoneConfig to Server = " + rootConfig.toString());			
			
		} catch (JSONException e) {
			Log.e(TAG,"JSONException = " + e.getMessage());
		}
		return rootConfig.toString();	
	}	
	
	
	public  String createWifiConfigFiles(String SSID, String security, String key){
		
		JSONArray  wifiInfo = new JSONArray();
		JSONObject oneSet = new JSONObject();
		
		JSONObject rootConfig = new JSONObject();
		JSONObject configJSON = new JSONObject();
		JSONArray ringtoneConfigArray = new JSONArray();	
		
		try {
			oneSet.put("ssid",SSID);
			oneSet.put("security",security);
			oneSet.put("key",key);
			wifiInfo.put(oneSet);			
			
			configJSON.put("networks", wifiInfo);
			rootConfig.put("config", configJSON);
			rootConfig.put("model",Build.DEVICE);
			Log.v(TAG,"Device model = " + Build.DEVICE);
			Log.v(TAG,"WifiConfig to Server = " + rootConfig.toString());			
			
		} catch (JSONException e) {
			Log.e(TAG,"JSONException = " + e.getMessage());
		}
		return rootConfig.toString();	
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
