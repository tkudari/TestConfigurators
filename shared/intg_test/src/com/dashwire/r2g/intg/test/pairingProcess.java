package com.dashwire.r2g.intg.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.Assert;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import com.dashwire.r2g.intg.test.util.IntegrationTestUtil;

import android.util.Base64;
import android.util.Log;

public class pairingProcess {

	
	
/***
 * 
 * Executes pairing process compose by 1 HTTP get and 2 HTTP Post
 * 
 */	
	
	public static void doParing(String pairCode, String url, String user, String pswd, String configFile, int sleepTime){
		
		
		HttpResponse httpResponse = null;
		String cookie, csrf_token, tracking_id, strHttpBody=null;	
		
		//Paring on R2G server is done on 3 steps.  1 HTTP Get and 2 HTT POST
		try {
			Thread.sleep(sleepTime);		
			httpResponse = HttpHelper.doHttpGet(url+"/pair",user,pswd);
					
			//Get Cookie and Token from HTTP headers Response from dashconfig.com      
			cookie = httpResponse.getFirstHeader("Set-Cookie").toString();
			cookie = cookie.substring(cookie.indexOf("_server_session"));
			Log.d("QA test", "Cookie =  OK"); //+ cookie
			strHttpBody = httpResponseToString(httpResponse);	
			csrf_token = getCsrfToken(strHttpBody);	
			tracking_id = getTrackingID(strHttpBody);	
			
			Log.d("QA test", "paring code");
			// 1st POST including paring code, cookie ant token
			doParingCodePost(url, csrf_token, cookie, pairCode);
			Thread.sleep(sleepTime);
			
			Log.d("QA test", "Posting Config"); 
			//2nd POST
			doSendConfigPost(url, cookie, csrf_token, tracking_id, configFile);			


		}catch (Exception e) {
			Log.d("QA test", "doParing on " + url+"/pair" + e.toString());	
			System.exit(1);
			Assert.fail( "doParing:" +url+"/pair" );

		}	
	}	
	
/***
 * First HTTP Post used on the pairing process used to get device ID and info.
 */

	private static  void doParingCodePost(String url, String csrf_token, String cookie, String pairCode){
	
		try {
			URL r2gURL = new URL(url+"/pair");
			HttpURLConnection coonect = (HttpURLConnection)r2gURL.openConnection();

			coonect.setDoOutput(true);
			coonect.setRequestMethod("POST");
			coonect.setRequestProperty("X-CSRF-Token", csrf_token);
			coonect.setRequestProperty("Content-Type","application/json; charset=UTF-8");
			coonect.setRequestProperty("Origin", "http://dev.dashconfig.com");
			coonect.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			coonect.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
			coonect.setRequestProperty("Referer", "http://dev.dashconfig.com");
			coonect.setRequestProperty("Accept-Charset","ISO-8859-1,utf-8;q=0.7,*;q=0.3");
			coonect.setRequestProperty("Cookie", cookie);
			String kk="att"+":"+"multihabitatmonkeyreport";
			String encoded = Base64.encode(kk.getBytes(), 0).toString();
			coonect.setRequestProperty("Authorization", "Basic "+encoded);	
			Log.d("QA test", "doParingCodePost() " + url);
			// Get the request's output stream 
		    OutputStreamWriter out = new OutputStreamWriter(coonect.getOutputStream());
		    // write the data to the request body
		    out.write("{\"code\":\"" + pairCode + "\"}");
		    out.flush();
		    out.close();		        
			Log.d("QA test", "FIRST POST" + coonect.getHeaderField("Status"));			
			
		    InputStream inputStream = coonect.getInputStream();
		    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		    StringBuilder total = new StringBuilder();
		    String line;
		    //Show post result
		    while ((line = r.readLine()) != null) {
		        total.append(line);
		    }	 
		    Log.d("QA test", total.toString());	
		}catch (Exception e) {
			Log.d("QA test", "doParingCodePost" + e.toString());	
			Assert.fail("doParingCodePost:" +url+"/pair");
		}
	}

/***
 * Second pairing process' HTTP Post to send JSon config file to device. 
 * 
 */
	public static void doSendConfigPost(String url, String cookie, String csrf_token, String tracking_id, String configFile){
		
		try{	
		    URL urll = new URL(url+"/config");
			HttpURLConnection connection = (HttpURLConnection)urll.openConnection();
	
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Cookie", cookie);
			connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			connection.setRequestProperty("X-CSRF-Token", csrf_token);
			connection.setRequestProperty("Accept-Encoding","gzip,deflate,sdch");
			connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");			
			connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
			connection.setRequestProperty("Accept-Charset","ISO-8859-1,utf-8;q=0.7,*;q=0.3");
			connection.setRequestProperty("Referer","http://dev.dashconfig.com/"); 
			//connection.setRequestProperty("Host", "ready2go.qa.dashlab.net");
			connection.setRequestProperty("Host", IntegrationTestUtil.HOST);
			
			String kk="att"+":"+IntegrationTestUtil.PSWD;
			String encoded = Base64.encode(kk.getBytes(), 0).toString();
			connection.setRequestProperty("Authorization", "Basic "+encoded);
			// Get the request's output stream 
			OutputStreamWriter outt = new OutputStreamWriter(connection.getOutputStream());
		    // write the data to the request body
		    //outt.write("{\"tracking_id\":\"" + tracking_id + "\","+"\"config\": {\"wallpapers\": [{\"uri\": \"android.resource://com.lge.launcher2/drawable/wallpaper_08\",\"page\": 0,\"src\": \"/branded/lg/74568098/devices/daejang/wallpaper/wallpaper_08.jpg\"}],\"model\": \"l1a\",\"paired\": true}}");
						
			//Log.d("QA test", "{\"tracking_id\":\"" + tracking_id + "\","+configFile.substring(1));
			outt.write("{\"tracking_id\":\"" + tracking_id + "\","+configFile.substring(1));
		    outt.flush();
		    outt.close();		    
			
			Log.d("QA test", "ConfigFile POST" + connection.getHeaderField("Status"));
			
		    InputStream inputStream = connection.getInputStream();
		    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		    StringBuilder total = new StringBuilder();
		    String line;
		    while ((line = r.readLine()) != null) {
		        total.append(line);
		        Log.d("QA test", line);
		    }	
		    connection.disconnect();
		    Log.d("QA test", "Backend server disconnected");
		}catch(IOException e){
			Log.d("QA test", "doParingCodePost" + e.toString());	
			Assert.assertTrue(false);	
		}
		
		
	}
	
/***
 * Parse/extract csrf-token from HTML body (44chars) on ready to go web page
 * @param htmlText
 * @return the csrf-token
 */
	
	private static String getCsrfToken(String htmlText){
		String subString = null;		
		try{
			int labelIndex = htmlText.indexOf("\" name=\"csrf-token\" />");
			subString = htmlText.substring(labelIndex-44, labelIndex);
			Log.d("QA test", "csrf-token = "+ subString);
		}catch(IndexOutOfBoundsException e){
       	 	Log.d("QA test", "Parsing Error = " + e.toString());        
		} 
		//Assert.assertNotNull("Null csrf-token ", subString);
		return subString;
	}
	
/***
 * Parse/extract the Tracking_Id from HTML body on r2gweb page.	
 * @param htmlText
 * @return
 */
	
	private static String getTrackingID(String htmlText){
		String subString = null;				
		try{
			int trackPosition = htmlText.indexOf("tracking_id");
			subString = htmlText.substring(trackPosition+14, trackPosition+14+40);
			Log.d("QA test", "Tracking ID = " + subString);
		}catch(IndexOutOfBoundsException e){
       	 	Log.d("QA test", "Parsing Error = " + e.toString());        
		} 
		//Assert.assertNotNull("Null tracking_id ", subString);		
		return subString;
	}
	
	
	private static  String httpResponseToString(HttpResponse response){
		String body = null;
		HttpEntity entity = response.getEntity();
		try {
			body =  EntityUtils.toString(entity);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return body;
	}
	
}
