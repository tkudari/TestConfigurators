package com.dashwire.testing.helper.verifier;

import java.util.List;

import junit.framework.Assert;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiVerifier {
/***
 * Returns a list with the info of the wifi settings found on device
 * @param context
 * @return
 */
	static public List<WifiConfiguration> readWifiList(Context context){
		
		Log.d("QA test","readWifiSettings");
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiMgr.getConfiguredNetworks();
	}
	
/***
 * Search on a wifi list for the specified wifi name.	
 * @param theWifiList
 * @param wifiToSearch
 * @return true if the specified wifi name is found on the list.
 */
	static public boolean isWifiOnList(List<WifiConfiguration> theWifiList, String wifiToSearch){
		boolean result = false;
		Log.d("QA test", "isWifiOnList. Search for:" + wifiToSearch);
		if (theWifiList == null) Assert.fail();

		for (WifiConfiguration aWifi: theWifiList){	
			if (aWifi.SSID.indexOf(wifiToSearch) != -1){
				Log.d("QA test","Found WIFI: " + aWifi.SSID);
				result = true;		
			}
		}
		
		if (!result) Log.d("QA test", "WIFI not found");
		
		return result;	
	}
	
/***
 * Get wifi list from device and search for the specified wifi name. Assert error if wifi is not found on device wifi settings.
 * @param context
 * @param wifiName
 * @return
 */
	
	static public void validateWifi(Context context, String wifiName){		
		boolean result;
		List<WifiConfiguration> wifiList = readWifiList(context);
		result = isWifiOnList(wifiList, wifiName);

		Assert.assertEquals("Wifi \"" + wifiName + "\" NOT Found on device wifi settings", true, result);
		WifiVerifier.setWifi(context, "Dashwire Private", "are we agile yet?");
	}
	
	
/***
 * Returns the device's current connected Wifi		
 * @param context
 * @return the connected wifi
 */
	static public String getActiveWifi(Context context){
		
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		String name = wifiInfo.getSSID();
		Assert.assertNotNull(name);
		return name;
	}
		
/***
 * Set the device's wifi to the specified wireless network (WPA)	
 * @param context
 * @param SSID		Wifi name
 * @param pswd
 */
	static public void setWifi(Context context, String SSID, String pswd){
		Log.d("QA test", "setWifi");
			
		for (int i=0; i < 4; i++ ){
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiConfiguration config= new WifiConfiguration();
			
			config.SSID="\"" + SSID +"\"";
			config.preSharedKey="\"" + pswd + "\"";
			config.hiddenSSID = true;
			config.status = WifiConfiguration.Status.ENABLED;
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);			
			int netId=wifiManager.addNetwork(config);
			wifiManager.saveConfiguration();
			wifiManager.reconnect();
			Log.d("QA test", "netId=" + netId);
			boolean b = wifiManager.enableNetwork(netId, true);        
			Log.d("QA test", "Enabled Network =" + b );
			if (netId != -1){
				break;
			}
			else{
				i++;
				try {Thread.sleep(2000);} catch (InterruptedException e){e.printStackTrace();}
				config=null;
				wifiManager=null;				
			}
		}
		
	}
	
}
