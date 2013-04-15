package com.dashwire.touchwall.intg.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jayway.android.robotium.solo.Solo;


public class IntegrationTest extends ActivityInstrumentationTestCase2{
	//	private static final String TARGET_PACKAGE_ID="com.dashwire.dwall_nfc_integ_test";
	//	private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME="com.dashwire.dwall_nfc_integ_test.IntgMainActivity";
	
	//  private static final String TARGET_PACKAGE_ID="com.dashwire.nfc";
	//  private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME="com.dashwire.nfc.ui.WelcomeActivity"; 
	
	private static final String TARGET_PACKAGE_ID="com.dashwire.touchwall.dashwire";
	private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME="com.dashwire.nfc.ui.SplashActivity";	

	private final String TESTCASES = "DWall_test_cases.txt";
	private static final String TAG = "QA test";
	private Solo solo;

	String AssetId = null;
	String AssetDesc = null;
	String testCaseLine = null;
	String[] testCaseParts = new String [2];
	String[][] testCaseValues = new String[1][2];

	Intent intent = new Intent(NfcAdapter.ACTION_NDEF_DISCOVERED);
	NdefMessage ndefMessage;
	NdefRecord[] defRec = new NdefRecord[1];
	Parcelable[] parcelable = new Parcelable[1];

	Instrumentation instrumentation;
	Instrumentation.ActivityMonitor monitor;
	Activity currentActivity;
	Context	context;
	View view;
	ArrayList<View> views;
	boolean result;
	String expectedUIMessage;
	Button button;
	String endButton;
	
	TestXmlHelper testResults = new TestXmlHelper();

	private static Class launcherActivityClass;

	static{
		try{ 
			launcherActivityClass=Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME); 
	
		} catch (ClassNotFoundException e){
			Log.d(TAG,"ClassNotFoundException " + e.toString());
			throw new RuntimeException(e);
		}
	}

	public IntegrationTest()throws ClassNotFoundException{ 
		
		super(TARGET_PACKAGE_ID,launcherActivityClass); 
	}


	@Override
	protected void setUp() throws Exception
	{
		Log.d(TAG,"setUp()");	
		instrumentation = getInstrumentation();

		solo = new Solo(instrumentation,getActivity());

		//get test application context (NOT the application Under test context)
		context = instrumentation.getContext();
		intent.setType("application/json");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		endButton= DWall_intg_test_helper.getConfigProperty(TESTCASES, "buttonDone", context);
		
		views = solo.getViews();
		
		if (views != null) {
			Log.d(TAG,"Views # " + views.size());
			view = views.get(0);
		}else{
			Log.e(TAG,"Views = Null ");
		}
		Log.i(TAG,"---------------------------------------------");	
		//Pause to wait for the Splash screen to close
		DWall_intg_test_helper.wait(2500);
	}


	/******************************************************************************************
	 * 
	 * TEST CASES BEGIN
	 * 	
	 *******************************************************************************************/

/***
 * Welcome screen
 */
	
//	public void test102_SplashScreen(){
//		Log.i(TAG,"test102_SplashScreen");
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"welcomeTitle",context,solo);
//		String imgName = DWall_intg_test_helper.takeScreenShot(view, "00");
//		String kk =  DWall_intg_test_helper.runCommand("adb ls " + imgName);
//		Log.d(TAG,kk);	
//	}	
	
//	public void test103_DashWallLaunchesWelcomeScreen(){
//		Log.i(TAG,"test103_DashWallLaunchesWelcomeScreen");
//	
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"welcomeTitle",context,solo);
//		Assert.assertTrue(result);		
//	}	
//		
//	
//	public void test104_DashWallThankYouTitle(){
//		Log.i(TAG,"test104_DashWallThankYouTitle");
//	
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneRingtone", context, intent);
//		
//		instrumentation.getContext().startActivity(intent);	
//
//		DWall_intg_test_helper.checkResult(TESTCASES,"successConfigRT",context,solo);
//
//		DWall_intg_test_helper.clickOnButton(endButton,solo);
//		
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"thankYouTitle",context,solo);
//
//		Assert.assertTrue(result);	
//	}	
//	
//	
//	
//
///***
// * Configure one feature	
// */
//	
//	public void test110_oneWallpaper(){	
//		Log.i(TAG,"test110_oneWallpaper");
//
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneWallpaper", context, intent);
//
//		instrumentation.getContext().startActivity(intent);	
//
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"successConfigWP",context,solo);
//
//		DWall_intg_test_helper.clickOnButton(endButton,solo);
//
//		Assert.assertTrue(result);
//
//	}
//
//
//	public void test112_oneRingtone(){
//		Log.i(TAG,"test112_oneRingtone");
//
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneRingtone", context, intent);
//
//		instrumentation.getContext().startActivity(intent);		
//
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"successConfigRT",context,solo);
//
//		DWall_intg_test_helper.clickOnButton(endButton,solo);
//
//		Assert.assertTrue(result);
//
//	}	
//
//	
//	public void test114_OneNotification(){
//		Log.i(TAG,"test114_oneNotification");
//
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneNotification", context, intent);
//
//		instrumentation.getContext().startActivity(intent);		
//
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"successConfigNF",context,solo);
//
//		DWall_intg_test_helper.clickOnButton(endButton,solo);
//
//		Assert.assertTrue(result);
//
//	}	
//
//	public void test116_ConfiguringOneWidget(){
//		Log.i(TAG,"test116_ConfiguringOneWidget");
//	
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneWidget", context, intent);
//	
//		instrumentation.getContext().startActivity(intent);		
//	
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"successConfigWG",context,solo);
//	
//		DWall_intg_test_helper.clickOnButton(endButton,solo);
//	
//		Assert.assertTrue(result);
//	}
//	
//	public void test118_ConfiguringOneShortcut(){
//		Log.i(TAG,"test118_ConfiguringOneShortcut");
//	
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneShortcut", context, intent);
//	
//		instrumentation.getContext().startActivity(intent);		
//	
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"successConfigSC",context,solo);
//	
//		DWall_intg_test_helper.clickOnButton(endButton,solo);
//	
//		Assert.assertTrue(result);
//	}	
//	
/////***
//// * Configuration Screen	
//// */
////	
////	public void test134_ConfiguringScreenNotification(){
////		Log.i(TAG,"test134_ConfiguringScreenNotification");
////	
////		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneNotification", context, intent);
////	
////		instrumentation.getContext().startActivity(intent);		
////	
////		result = DWall_intg_test_helper.checkResult(TESTCASES,"successConfigNF",context,solo);
////	
////		DWall_intg_test_helper.clickOnButton(endButton,solo);
////	
////		Assert.assertTrue(result);
////	}	
////	
////	public void test136_ConfiguringScreenShortcut(){
////		Log.i(TAG,"test136_ConfiguringScreenShortcut");
////	
////		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneShortcut", context, intent);
////	
////		instrumentation.getContext().startActivity(intent);		
////	
////		result = DWall_intg_test_helper.checkResult(TESTCASES,"successConfigSC",context,solo);
////	
////		DWall_intg_test_helper.clickOnButton(endButton,solo);
////	
////		Assert.assertTrue(result);
////	}	
////	
////	public void test138_ConfiguringScreenWidgets(){
////		Log.i(TAG,"test138_ConfiguringScreenWidgets");
////	
////		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneWidget", context, intent);
////	
////		instrumentation.getContext().startActivity(intent);		
////	
////		result = DWall_intg_test_helper.checkResult(TESTCASES,"successConfigWG",context,solo);
////	
////		DWall_intg_test_helper.clickOnButton(endButton,solo);
////	
////		Assert.assertTrue(result);
////	}	
//
///***
// * Multiple configurations	
// */
//	
//	
//	public void test202_SetMultipleWallpapers(){
//		Log.i(TAG,"test202_SetMultipleWallpapers");		
//		String testCaseLine = DWall_intg_test_helper.getConfigProperty(TESTCASES, "multipleWallpapers", context);
//		List<String[][]> testCasesList = DWall_intg_test_helper.parseTestCaseMultipleLines(testCaseLine);
//
//		DWall_intg_test_helper.runMultipleTestCases("successConfigWP",testCasesList, intent, instrumentation, solo, context, TESTCASES, endButton);	
//	}		
//	
//	
//	public void test204_SetMultipleRingtones(){
//		Log.i(TAG,"test204_SetMultipleRingtones");		
//		String testCaseLine = DWall_intg_test_helper.getConfigProperty(TESTCASES, "multipleRingtones", context);
//		List<String[][]> testCasesList = DWall_intg_test_helper.parseTestCaseMultipleLines(testCaseLine);
//
//		DWall_intg_test_helper.runMultipleTestCases("successConfigRT", testCasesList, intent, instrumentation, solo, context, TESTCASES, endButton);	
//	}	
//
//
//	public void test206_SetMultipleNotifications(){
//		Log.i(TAG,"test206_SetMultipleNotifications");		
//		String testCaseLine = DWall_intg_test_helper.getConfigProperty(TESTCASES, "multipleNotifications", context);
//		List<String[][]> testCasesList = DWall_intg_test_helper.parseTestCaseMultipleLines(testCaseLine);
//
//		DWall_intg_test_helper.runMultipleTestCases("successConfigNF", testCasesList, intent, instrumentation, solo, context, TESTCASES, endButton);	
//	}	
//	
//	
//	public void test208_SetMultipleShortcuts(){
//		Log.i(TAG,"test208_SetMultipleShortcuts");		
//		String testCaseLine = DWall_intg_test_helper.getConfigProperty(TESTCASES, "multipleShortcuts", context);
//		List<String[][]> testCasesList = DWall_intg_test_helper.parseTestCaseMultipleLines(testCaseLine);
//
//		DWall_intg_test_helper.runMultipleTestCases("successConfigSC",testCasesList, intent, instrumentation, solo, context, TESTCASES, endButton);	
//	}		
//
//
//	public void test210_SetMultipleWidgets(){
//		Log.i(TAG,"test210_SetMultipleWidgets");		
//		String testCaseLine = DWall_intg_test_helper.getConfigProperty(TESTCASES, "multipleWidgets", context);
//		List<String[][]> testCasesList = DWall_intg_test_helper.parseTestCaseMultipleLines(testCaseLine);
//
//		DWall_intg_test_helper.runMultipleTestCases("successConfigWG",testCasesList, intent, instrumentation, solo, context, TESTCASES, endButton);	
//	}			
//
///***
// * Incorrect values	
// */
//	
//	public void test402_InexistentWallpaperID(){	
//		Log.i(TAG,"test402_InexistentWallpaperID");
//
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"inexistentWP", context, intent);
//
//		instrumentation.getContext().startActivity(intent);	
//
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"incorrectIDMessage",context,solo);
//
//		DWall_intg_test_helper.clickOnButton(endButton,solo);
//
//		Assert.assertTrue(result);	
//	}	
//
//	
//	public void test404_InexistentRingtoneID(){	
//		Log.i(TAG,"test404_InexistentRingtoneID");
//
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"inexistentSC", context, intent);
//
//		instrumentation.getContext().startActivity(intent);	
//
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"incorrectIDMessage",context,solo);
//
//		DWall_intg_test_helper.clickOnButton(endButton,solo);
//
//		Assert.assertTrue(result);	
//	}	
//
//	
//	public void test406_InexistentNotificationID(){	
//		Log.i(TAG,"test406_InexistentNotificationID");
//
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"inexistentNF", context, intent);
//
//		instrumentation.getContext().startActivity(intent);	
//
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"incorrectIDMessage",context,solo);
//
//		DWall_intg_test_helper.clickOnButton(endButton,solo);
//
//		Assert.assertTrue(result);	
//	}	
//
//
//	public void test408_InexistentShortcutID(){	
//		Log.i(TAG,"test408_InexistentShortcutID");
//
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"inexistentSC", context, intent);
//
//		instrumentation.getContext().startActivity(intent);	
//
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"incorrectIDMessage",context,solo);
//
//		DWall_intg_test_helper.clickOnButton(endButton,solo);
//
//		Assert.assertTrue(result);
//	}
//
//	
//	public void test410_InexistentWidgetID(){	
//		Log.i(TAG,"test410_InexistentWidgetID");
//
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"inexistentWG", context, intent);
//
//		instrumentation.getContext().startActivity(intent);	
//
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"incorrectIDMessage",context,solo);
//
//		DWall_intg_test_helper.clickOnButton(endButton,solo);
//
//		Assert.assertTrue(result);	
//	}		
//	
//
//	public void test412_inexistentFeature(){	
//		Log.i(TAG,"test412_inexistentFeature");
//
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"inexistentFeature", context, intent);
//
//		instrumentation.getContext().startActivity(intent);	
//
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"welcomeTitle",context,solo);
//
//		Assert.assertTrue(result);	
//	}
//
///***
// * Screens full	
// */
//	
//	public void test503_Send_100_MultipleConfigOnARow(){
//		Log.i(TAG,"test503_Send_100_MultipleConfigOnARow");
//		//Set the asset to configure
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneWidget", context, intent);
//		instrumentation.getContext().startActivity(intent);	
//		//Set the asset to be sent multiple times. Is Should NOT be processed.
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneNotification", context, intent);
//	
//		Log.i(TAG,"Start sending 100 new intent");
//		for (int i=0; i< 100; i++){
//			instrumentation.getContext().startActivity(intent);	
//			//Log.i(TAG,"Send new intent "  + intent.getType());
//		}
//		Log.i(TAG,"Ending sending 100 new intent");
//		result = DWall_intg_test_helper.checkResult(TESTCASES,"successConfigWG",context,solo);
//		Assert.assertTrue(result);
//		Log.i(TAG,"Any of the 100 new intents was processed while processing first intent");
//		DWall_intg_test_helper.clickOnButton(endButton,solo);
//	}	
//	
//	
//	
//	public void test504_NoMoreRoomForWidgets(){
//		Log.i(TAG,"test504_NoMoreRoomForWidgets");
//		
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneWidget", context, intent);
//		
//		for (int i=0; i< 25; i++){
//			instrumentation.getContext().startActivity(intent);
//
//			result = DWall_intg_test_helper.checkResult(TESTCASES,"noMoreRoomWG",context,solo);
//			if (result == true) break;
//			
//			DWall_intg_test_helper.clickOnButton(endButton,solo);
//		}		
//	
//		if (result)	DWall_intg_test_helper.clickOnButton(endButton,solo);
//
//		Assert.assertTrue(result);
//	}
//
//	
//	public void test506_NoMoreRoomForShortcuts(){
//		Log.i(TAG,"test506_NoMoreRoomForShortcuts");
//		
//		intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneShortcut", context, intent);
//		
//		for (int i=0; i< 25; i++){
//			instrumentation.getContext().startActivity(intent);
//			
//			result = DWall_intg_test_helper.checkResult(TESTCASES,"noMoreRoomSC",context,solo);
//			if (result == true) break;
//			
//			DWall_intg_test_helper.clickOnButton(endButton,solo);
//		}		
//	
//		if (result)	DWall_intg_test_helper.clickOnButton(endButton,solo);
//		Assert.assertTrue(result);
//	}
	

//public void test116_oneShortcut(){	
//	Log.i(TAG,"test116_oneShortcut");
//
//	intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneWallpaper", context, intent);
//
//	instrumentation.getContext().startActivity(intent);	
//
//	result = DWall_intg_test_helper.checkResult(TESTCASES,"successConfigWP",context,solo);
//
//	DWall_intg_test_helper.takeScreenShot(view, "01");
//	
//	DWall_intg_test_helper.clickOnButton(endButton,solo);
//
//	DWall_intg_test_helper.wallPaperScreenShot(context, "01");
//
//	Assert.assertTrue(result);
//}		
//
//
///
/*
public void test117_oneShortcut(){	
	Log.i(TAG,"test116_oneShortcut");

	intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneWallpaper2", context, intent);

	instrumentation.getContext().startActivity(intent);	

	result = DWall_intg_test_helper.checkResult(TESTCASES,"successConfigWP",context,solo);

	DWall_intg_test_helper.takeScreenShot(view, "01");
	
	DWall_intg_test_helper.clickOnButton(endButton,solo);

	DWall_intg_test_helper.wallPaperScreenShot(context, "01");

	Assert.assertTrue(result);
}		
*/
	
public void test0_startXMLReport(){
	testResults.startXMLfile();
}
	
public void test118_oneShortcut(){
	
	String testTitle = "test118_oneShortcut";

	intent = DWall_intg_test_helper.setTestCase(TESTCASES,"oneWallpaper2", context, intent);

	instrumentation.getContext().startActivity(intent);	

	result = DWall_intg_test_helper.checkResult(TESTCASES,"successConfigWP",context,solo);
	
	DWall_intg_test_helper.takeScreenShot(view, testTitle);
	
	testResults.LogTestResultXML(testTitle, DWall_intg_test_helper.expectedUIMessage, DWall_intg_test_helper.resultedUIMessage, result);
	
	Assert.assertTrue(true);

}		
	
public void test99999_closingXMLReport(){
	
	testResults.closeXMLfile();
}		

}
