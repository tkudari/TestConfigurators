package com.dashwire.r2g.intg.test;

import com.dashwire.testing.helper.verifier.WifiVerifier;

import android.content.Context;


public class wifi_testCases {

	Context m_context;
	String ENVIRONMENT;
	String USER;
	String PSWD;
	R2G_UI_Helper r2gHelper; 
	
	
	public wifi_testCases(Context m_context, String eNVIRONMENT, String uSER,
			String pSWD, R2G_UI_Helper r2gHelper) {
		super();
		this.m_context = m_context;
		ENVIRONMENT = eNVIRONMENT;
		USER = uSER;
		PSWD = pSWD;
		this.r2gHelper = r2gHelper;

	}

	/***
	 * Execute the stpes needed to run a wifi test case (get wifi name from config file, send config file,verify on device setting that expected wifi is on wifi's lit.
	 * @param r2gConfigFile
	 */
	
	public void runTestCase(String r2gConfigFile){
		
		String wifiNameOnJson= integrationTestHelper.getWifiFromJson(r2gConfigFile, "ssid");
		
		r2gHelper.sendR2GConfig(ENVIRONMENT,USER,PSWD, r2gConfigFile, "WIFI");
		
		WifiVerifier.validateWifi(m_context,wifiNameOnJson);
		
		WifiVerifier.setWifi(m_context,"Dashwire Private", "are we agile yet?");
	}
	
}
