package com.dashwire.r2g.intg.test;

import java.util.ArrayList;
import junit.framework.Assert;
import com.jayway.android.robotium.solo.Solo;
import android.annotation.TargetApi;
import android.util.Log;
import android.widget.ImageView;

public class R2G_UI_Helper {

	Solo m_robotium = null;
	
	public R2G_UI_Helper(Solo m_robtium) {
		super();
		this.m_robotium = m_robtium;
	}

	
	
	public void sendR2GConfig(String url, String user, String pswd, String configFile, String testType ){
		try {
			tapOnGetStarted();
			String pairCode = getPairCodeFromScreen();
			
			if ("SMOKE".equalsIgnoreCase(testType))
			{
				Log.d("QA test", "SMOKE Test");
				Log.d("QA test", "pairingProcess.doParing : " + configFile);
				pairingProcess.doParing(pairCode, url, user, pswd, configFile, 4000);
				Thread.sleep(25000);
			}else
			{
				pairingProcess.doParing(pairCode, url, user, pswd, configFile, 2000);
			}
		}catch(Exception e){
			Log.d("QA test", "ERROR on sendR2GConfig");
			Assert.fail("Error on sendR2GConfig /r/n" + e.toString());
		}
		
		tapOnFinish();
	}
	
	/***
	 * Simulates tapping Get Started button on screen
	 */
	@TargetApi(15)
	boolean tapOnGetStarted() throws Exception{
		Log.d("QA test", "tapOnGetStarted");
		boolean result =false;
		ImageView getStartedImage=null; ;
		//Get all ImageViews on screen and click on the first clickable one.
		ArrayList<ImageView> imgsViewOnScreen = m_robotium.getCurrentImageViews(); 				
		for (int i=0; i < imgsViewOnScreen.size(); i++){
			getStartedImage = null;
			getStartedImage = (ImageView) imgsViewOnScreen.get(i);	
			if (getStartedImage.isClickable() == true ){
				m_robotium.clickOnImage(i);
				result = true;
				break;
			}
		}
			
		if (!result){
			Log.d("QA test", "Get Started Image not found");
			Assert.fail("Get Started Image not found");
		}

		return result;
	}
	
	public void tapOnFinish() {
		
		Log.d("QA test", "tapOnFinish");
		
		for(int i=0; i<4; i++){
			try{
				m_robotium.clickOnButton("Finish");
				break;
			}catch(Exception e){
				m_robotium.sleep(2000);
				Log.d("QA test", "Finish button not found. Retrying. " + e.toString());
				i++;
			}
		}	
	}
	
	
/***
 * Read pairing code from screen and validates it has the expected format [A-Z0-9]{6}
 * 
*/	
	
	protected String getPairCodeFromScreen() throws Exception{
			
		String pairCode=null;
		int i=0;
		Log.v("QA test", "getPairCodeFromScreen");
		
		for(i=0; i<4; i++){
			try{
			pairCode = m_robotium.getText(6).getText().toString();
			}catch(Exception e){
				m_robotium.sleep(2000);
				Log.d("QA test", "Pair code not found. Retrying");
				i++;
			}
		}
		i=0;
		while (pairCode.matches("^[A-Z0-9]{6}$") == false){
			m_robotium.sleep(1000);
			pairCode = m_robotium.getText(6).getText().toString();
			if ( i++ > 10){
				Log.d("QA test", "Pair code not found");
				pairCode =null;
				break;
			}
			
		}
		Log.v("QA test", "Pair Code="+pairCode);
		//Assert.assertNotNull("Pair code not found",pairCode);	
		return pairCode;
	}


}
