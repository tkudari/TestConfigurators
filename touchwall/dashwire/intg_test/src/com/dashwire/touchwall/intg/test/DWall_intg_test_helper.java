package com.dashwire.touchwall.intg.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.android.robotium.solo.Solo;

import android.app.Instrumentation;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class DWall_intg_test_helper {

	final static String TAG = "QA test";
	static String expectedUIMessage;
	static String resultedUIMessage;

	public static Intent setTestCase(String TESTCASES, String testCaseID, Context context, Intent intent){
		
		String testCaseLine = getConfigProperty(TESTCASES, testCaseID, context);
		
		String[][] testCaseValues = parseTestCaseLine(testCaseLine);
		
		NdefMessage ndefMessage = createNDEFMessage(testCaseValues);		
		
		return setIntentToNFC(intent, ndefMessage);			
	}
	
	
	/***
	 * Read a text file on the format of  fieldName="fieldValue" and returns the filedValue
	 * 
	 * @param fileToRead
	 * @param property_to_read
	 * @param context
	 * @return
	 */

	public  static String getConfigProperty(String fileToRead,String property_to_read, Context context){
		Log.d(TAG, "getConfigProperty");
		
		AssetManager am = context.getResources().getAssets();
		Properties prop = new Properties();
		String readValue=null;	
		InputStream isConfig;

		try {
			isConfig = am.open(fileToRead,Context.MODE_PRIVATE);
			prop.load(isConfig);
			readValue = prop.getProperty(property_to_read);
			Log.d(TAG, "Value from text file = " + readValue);
			if (readValue == null) {
				Log.e(TAG, "ERROR. Field '" + property_to_read + "' NOT Found on test cases file.");
				Assert.assertNotNull("No field found on test cases file. " + property_to_read + "  = NULL ", readValue);
			}			
			Log.d(TAG, "Field value = " + readValue);
		} catch (IOException e) {
			Log.i(TAG, "Error reading file =  " + fileToRead + e.toString());
		}
		return readValue;
	}	


	public static NdefRecord createNdefRecord(String text){

		Log.d(TAG,"createNdefRecord()");
		NdefRecord recordNFC = null;
		try{        
			String mimeType = "application/json";
			byte[] mimeBytes = mimeType.getBytes(Charset.forName("UTF-8"));
			byte[] configBytes = text.getBytes(Charset.forName("UTF-8"));
			recordNFC =  new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], configBytes);	     
		}catch(Exception e){
			Log.d(TAG,"Exception2" + e.toString());
		}

		return recordNFC;  	

	} 	
	
/***
 * Builds the json needed to conifgure feature using the featureID and featureName taken by the NFC DWall project
 * 	
 * @param featureId
 * @param featureName
 * @return
 */

	public static JSONObject createJsonConfig(String featureId, String featureName){

		JSONObject config = new JSONObject();
		try {
			config.put("featureId", featureId);
			config.put("featureName", featureName);
		}catch (JSONException e) {
			Log.d(TAG,"JSONException " + e.toString());
		}
		Log.i(TAG,"Config = " + config.toString());
		return config;
	}	


	public static Intent setIntentToNFC(Intent intent, NdefMessage ndefMessage){
		Log.d(TAG,"setIntentToNFC");
		
		Parcelable[] parcelable = new Parcelable[1];
		parcelable[0]=(Parcelable)ndefMessage; 
		intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, parcelable);
		return intent;
	}

	/***
	 * Creates the NdefMessage containing the Json config data to be sent to device 
	 * 
	 * @param testCaseValues
	 * @return
	 */
	public static NdefMessage createNDEFMessage(String[][] testCaseValues){
		NdefRecord[] defRec = new NdefRecord[1];
		
		String config = createJsonConfig(testCaseValues[0][0],testCaseValues[0][1]).toString();	
		
		defRec[0] = createNdefRecord(config);
		
		NdefMessage ndefMessage = new NdefMessage(defRec);
		
		return ndefMessage;	
	}
	
	
	
	public static void runMultipleTestCases(String expectedMessage, List<String[][]> tcv, Intent intent, Instrumentation instrumentation, Solo solo, Context context, String TESTCASES, String endButton){
		boolean result = false;
		String[][] oneTestCase;
		for (int i=0; i< tcv.size(); i++){
			oneTestCase = tcv.get(i);
			Log.i(TAG, "Config # " + i);
			oneTestCase[0][0] = oneTestCase[i][0]; 
			oneTestCase[0][1] = oneTestCase[i][1];
			NdefMessage ndefMessage = DWall_intg_test_helper.createNDEFMessage(oneTestCase);
			
			intent = DWall_intg_test_helper.setIntentToNFC(intent, ndefMessage);
			
			instrumentation.getContext().startActivity(intent);
			
			result = DWall_intg_test_helper.checkResult(TESTCASES,expectedMessage,context,solo);
			
			DWall_intg_test_helper.clickOnButton(endButton,solo);
			
			Assert.assertTrue(result);	
			
			wait(1000);
		}
				
	}
	


	/***
	 * Takes one line on the .txt test case file and split the values.
	 * @param testCaseLine
	 * @return
	 */
	public static String[][] parseTestCaseLine(String testCaseLine){
		String[] testCaseParts = new String[2];
		String[][] testCaseValues = new String[1][2];
		try{
			testCaseParts = testCaseLine.split(",");
			testCaseValues[0][0]=testCaseParts[0];
			testCaseValues[0][1]=testCaseParts[1];
		}catch(Exception e){
			Log.e(TAG,"ERROR. Error parsing test case line '" + testCaseLine + "'. It most follow format asset_id,FeatureName");
			Assert.assertTrue("--> ERROR. Error parsing test case line '" + testCaseLine + "'. It most follow format asset_id,FeatureName", false);
		}

		Log.d(TAG,"Test case parts = " + testCaseValues[0][0] + " - " + testCaseValues[0][1]);

		return testCaseValues;

	}




	public static List<String[][]> parseTestCaseMultipleLines(String testCaseLine){
		Log.d(TAG,"parseTestCaseMultipleLines = " +testCaseLine);
		List<String> tcs = new ArrayList<String>();
		String[] tcp;
		List<String> tcpp = new ArrayList<String>();
		List<String[][]> tcv = new ArrayList<String[][]>();

		//Get independent test cases (separated by ";" on test cases .txt file)
		tcs = Arrays.asList(testCaseLine.split(";"));
		Log.d(TAG,"tcs = " + tcs.size());
		//Get test case value (separated by "," on test cases .txt file)
		for (int i=0; i < tcs.size(); i++){
			tcp = tcs.get(i).split(",");
			tcpp.add(tcp[0]);
			tcpp.add(tcp[1]);
		}
		Log.d(TAG,"tcpp = " + tcpp.size());
		String[][] testCaseValues = new String[tcpp.size()][2];
		int x=0;
		for (int i=0; i < tcpp.size(); i=i+2){
			testCaseValues[x][0]=tcpp.get(i);
			testCaseValues[x][1]=tcpp.get(i+1);
			
			tcv.add(testCaseValues);
			Log.d(TAG,"testCaseValues = " +testCaseValues[x][0] + " - " + testCaseValues[x][1]);
			x++;
		}
		Log.d(TAG,"Test cases total = " + tcv.size());
		return tcv;	
	}	
	
/***
 * Verify if the specified text is displayed on the device's screen.
 * 	
 * @param testCasesFile	File from where to read the value to search for
 * @param valueToRead   The field name to read from File.
 * @param context
 * @param solo
 * @return
 */
	public static boolean checkResult(String testCasesFile, String valueToRead, Context context, Solo solo){
		Log.d(TAG,"checkResult()");
		boolean result;
		
		expectedUIMessage = DWall_intg_test_helper.getConfigProperty(testCasesFile, valueToRead, context);
		Assert.assertNotNull("No field found on test cases file. " + valueToRead + " NULL ", expectedUIMessage);	
		
		result = solo.waitForText(expectedUIMessage, 1, 4000);

		if (result){
			Log.i(TAG,"Found text: " + expectedUIMessage);
			resultedUIMessage=expectedUIMessage;
		}
		else{
			Log.e(TAG,"FAILED to find text: " + expectedUIMessage);
			resultedUIMessage = solo.getText(1).getText() + " " + solo.getText(2).getText();
			Log.e(TAG,"Found text: " +resultedUIMessage);
		}	
		return result;
	}
	
	/***
	 * Click on the button showing the specified text on buttonText parameter.
	 * @param buttonText  Text of the button to be clicked.
	 * @param solo
	 * @return
	 */
	public static boolean clickOnButton(String buttonText, Solo solo){
		Button button;
		Log.d(TAG,"clickOnButton()");
		boolean result = false;
		try{
			button = solo.getButton(buttonText);
			result = button.callOnClick();
			if (result == true) Log.d(TAG,"Button " + buttonText + " clicked"); else Log.e(TAG,"FAILED to click button " + buttonText);
		}catch(Exception e){
			Log.i(TAG,"ClickOnButton Exception" + e);			
		}		
		return result;
	} 		
	
	
	public static void wait(int milsec){
		try{
			Thread.sleep(milsec);
		}catch(Exception e){
			Log.d(TAG,"Error on wailt");
		}
		
	}
	
/***
 * Create a  JPG file containing an image of the current device's wallpaper
 * 	
 * @param context
 * @param fileName  Just file name. NO extension.
 */
	
	public static void wallPaperScreenShot(Context context, String fileName){
		Log.d(TAG,"wallPaperScreenShot()");
		DWall_intg_test_helper.wait(500);
		WallpaperManager wm = WallpaperManager.getInstance(context);
		
		Drawable dw = wm.peekDrawable();
		
		Bitmap bitmap = ((BitmapDrawable)dw).getBitmap();
		
		createFileFromBitMap(bitmap, fileName);
		
	}
	
	
/***
 * Creates a JPG file containing an image of specified view. NO file extension needed.	
 * @param view		View to take image from.
 * @param fileName Just file name. No extension.
 */
	public static String takeScreenShot(View view, String fileName){
		Log.d(TAG,"takeScreenShot()");
		String fullFileName = null;
		try{
			DWall_intg_test_helper.wait(500);
			view.setDrawingCacheEnabled(true); 
			view.buildDrawingCache(); 
			Bitmap b = view.getDrawingCache();
			fullFileName = createFileFromBitMap(b, fileName);
		}catch(Exception e){
			Log.e(TAG, "takeScreenshot Exception. " + e.toString() );
		}
		
		return fullFileName;
	}

	
	
/***
 * Creates a JPG file containing an image of specified view. NO file extension needed.	
 * @param bitmap		Image bits.
 * @param fileName 		Just file name. No extension.
 */	
	public static String createFileFromBitMap(Bitmap bitmap, String fileName){
		Log.d(TAG,"External Storage Writable ? / Readable ? " + isExternalStorageWritable() + isExternalStorageReadable());
		
		final String screenShotsPath = Environment.getExternalStorageDirectory() + File.separator + "QAResults"+ File.separator + "QAResultImages" + File.separator ;
		String fullfileName = screenShotsPath + fileName + ".jpg";
				
		createDirectory(screenShotsPath);
		
		if (bitmap == null){ Log.e(TAG,"NUll bitmap"); return "";}
		
		try{    				
			FileOutputStream fos = new FileOutputStream(fullfileName); 
			Log.v(TAG, "ScreenShots file = " + fullfileName);
			if (fos != null){ 
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos); 
				fos.close(); 
			} 
		}catch (IOException e){ 
			Log.d(TAG,"Exception = " + e.toString());
		} 			
		
		return fullfileName;
	}
	
	
	public static void createXMlfile(String fileName, String xmlContent){
		Log.d(TAG,"External Storage Writable ? / Readable ? " + isExternalStorageWritable() + isExternalStorageReadable());
		
		final String screenShotsPath = Environment.getExternalStorageDirectory() + File.separator + "QAResults"+ File.separator;
		String fullfileName = screenShotsPath + fileName;				
		createDirectory(screenShotsPath);
		
		try{	
			FileWriter fw = new FileWriter(fullfileName);
			fw.write(xmlContent);
		    fw.flush();
		    fw.close();
		}catch(IOException e){
			Log.e(TAG,"Error on createXMLfile = " + e.toString());
		}
		
		
	}
	
	/***
	 * Checks if external storage is available for read and write 
	 * @return
	 */
	
	public static boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/***
	 * Checks if external storage is available to at least read 
	 * @return
	 */
	
	public static boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	
	/***
	 * Create a folder on the specified location.
	 * @param dirName  path and file name to be created.
	 */
	
	public static void createDirectory(String dirName){
		
	   File direct = new File(dirName);

	   if(!direct.exists())
	    {
	        if(direct.mkdir()) 
	          {	           		          
	        	Log.d(TAG, "New dir = " + dirName);
	          }

	    }
	}
	
	
	
 /***
 * Run a shell command (from command line)
 *
 * @param command and is parameter to be executed
 * @return command out put shown on command line.
 */
    public static String runCommand(String command) {
    	Log.d(TAG, "runCommand()");
    	
        StringBuilder commandOutput = new StringBuilder();
        try
        {
        	
     /*   	
            //Process p = Runtime.getRuntime().exec(new String[] { "/Users/guillermo/development/libs/android-sdk/platform-tools/adb pull", "/sdcard/Pictures/00_1363242012911.jpg"});
            Process p = Runtime.getRuntime().exec(new String("adb pull /sdcard/Pictures/00_1363242012911.jpg")); 
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
     */
        	
        	ProcessBuilder pb = new ProcessBuilder("ls", "/sdcard/Pictures/");
        	Process p = pb.start();
        	BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        	
            // read the output from the command
            Log.d(TAG, "ONEONE");
            String s;
            while ((s = stdInput.readLine()) != null) {
                commandOutput.append(s);
            }
        }
        catch(IOException e){
        	Log.e(TAG,"Error executing command line instruction " + e.toString());
        }
        
       
        
        Log.d(TAG, "commandOutput Length" + commandOutput.toString());
       
        
        return commandOutput.toString();
    }

	
	
	
}
