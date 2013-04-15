package com.dashwire.r2g.intg.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.io.IOUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;


public class integrationTestHelper {	
	
	final static String TAG="QA test";

/***
 * Read a file stored on project/assets folder and returns its content	
 * @param context
 * @param fileName
 * @return String with the file content.
 */
	static public String readTextFile(Context context, String fileName){
		String result = null;
		Log.d("QA test","readTextFile");
		try {
			InputStream is = context.getResources().getAssets().open(fileName);
			result =  IOUtils.readFully(is);		
		} catch (IOException e) {
			Log.d("QA test","readTextFile. \r\n" + e.toString());
			Assert.assertTrue(false);
		}		
		return result;
	}
	
	
	public static String getWifiFromJson(String jSonConfigFile, String elementToRead){
		String wifiName=null;
		try {
			JSONObject jsonObject = new JSONObject(jSonConfigFile);
			JSONObject configObject = jsonObject.getJSONObject("config");
			JSONArray networksObject = configObject.getJSONArray("networks");
			
			//Search for ssid element
			for (int i = 0; i < networksObject.length(); i++){
				try {
					wifiName = networksObject.getJSONObject(i).getString(elementToRead);
					return wifiName;
				}catch(JSONException e){
					Log.d("QA test","Error on getWifiFromJson" + e.toString());
				}
			}
			Log.d("QA test", wifiName);
			
		}catch (JSONException e) {
			Log.d("QA test", e.toString());
		}
		return wifiName;
	}
	
	public static String setDeviceModel(String jSonConfigFile, String device_model){
		JSONObject jsonObject=null;
		
		try {
			jsonObject = new JSONObject(jSonConfigFile);
			jsonObject.remove("model");
			jsonObject.put("model", device_model);
		} catch (JSONException e) {
			
			Log.d("QA test","Error Updating JsonConfig file" + e.toString());
		}
		Log.d("QA test","UPDATED Config. \r\n" + jsonObject.toString());
		
		return jsonObject.toString(); 
		
	}
	
/***
 * Returns the ringtone OR notification values for the specified ringtone type ("sms or call" on the configuration JSon file to be send to device.	
 * @param jSonConfigFile
 * @param ringToneType (call or sms)
 * @return String[2] [0] Uri , [1] title
 */
	
	public static String[] getRingtoneFromJson(String jSonConfigFile, String ringToneType){
		String[] ringInfo = new String[2];
		String ringType;
		
		try{
			JSONObject jsonObject = new JSONObject(jSonConfigFile);
			JSONObject configObject = jsonObject.getJSONObject("config");
			JSONArray ringtonesObject = configObject.getJSONArray("ringtones");
			
			for (int i = 0; i < ringtonesObject.length(); i++){
				try {
					ringType = ringtonesObject.getJSONObject(i).getString("type").toUpperCase();
					if (ringType.indexOf(ringToneType.toUpperCase()) != -1){
						ringInfo[0] = ringtonesObject.getJSONObject(i).getString("uri");
						ringInfo[1] = ringtonesObject.getJSONObject(i).getString("title");
						break;
					}
				}catch(Exception e){
					Log.d("QA test", "getRingtoneInfo() Failed " + e.toString());
				}
			}
			Log.d("QA test", "Ringtone on Json. Title =  " + ringInfo[1] + ". Uri = " + ringInfo[0]);
			
		}catch(Exception e){
			Log.d("QA test", "getRingtoneInfo" + e.toString());
		}
		
		return ringInfo;
		
	}
	
	public static String getConfigProperty(String property_to_read, Context context){
		AssetManager am = context.getResources().getAssets();
		Properties prop = new Properties();
		String theProp=null;
	
		InputStream isConfig;
		try {
			isConfig = am.open("intg_test.properties",Context.MODE_PRIVATE);
			prop.load(isConfig);
			theProp = prop.getProperty(property_to_read);
			Log.d("QA test", "Device model = " + theProp);
		} catch (IOException e) {
			Log.d("QA test", "Error reading intg_test.properties file. " + e.toString());
		}
		return theProp;
	}
	
/*	
 //TODO .  These two methods need to be fixed. (They DO RUN on Integration.java )
	static public void readDefaultWallPaper(Context context){
		//Get default Wallpaper
		WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
		wallpaperManager.clear();		
		FrameLayout linearLayout  =(FrameLayout) context.getActivity().getWindow().getDecorView().findViewById(android.R.id.content);			
		linearLayout.setDrawingCacheEnabled(true);		
		Bitmap bitmap = linearLayout.getDrawingCache();		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);		
	}
	
	static public void saveFileToDevice(){
		try {

 //returns AEACCES (Permission denied)		
	    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);   
	    File file = new File(path, "defaulWP.jpg");
		path.mkdir();
        Log.d("QA test", "PATH="+path.getAbsolutePath());	
		OutputStream os = new FileOutputStream(file);
		os.write(bytes.toByteArray());
		os.close();

		} catch (IOException e) {
			Log.d("QA test",e.toString());
		}		
	}
*/
	
}
