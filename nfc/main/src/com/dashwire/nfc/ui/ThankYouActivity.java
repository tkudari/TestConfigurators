package com.dashwire.nfc.ui;

import com.dashwire.nfc.R;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

public class ThankYouActivity extends Activity {
	
	long closeTimeout = 5000, interval = 1000;
	TextView detailedText1, detailedText2, detailedText3;
		
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thank_you);

		Typeface fontExoBold = Typeface.createFromAsset(getAssets(), "Exo-DemiBold.otf");
		Typeface fontExoLight = Typeface.createFromAsset(getAssets(), "Exo-Light.otf");  

		detailedText1 = (TextView) findViewById(R.id.TyText1); 
		detailedText2 = (TextView) findViewById(R.id.TyText2);
		detailedText3 = (TextView) findViewById(R.id.TyText3);
		detailedText1.setTypeface(fontExoBold);
		detailedText2.setTypeface(fontExoLight);
		detailedText3.setTypeface(fontExoLight);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		new CountDownTimer(closeTimeout, interval) {
		     public void onTick(long millisUntilFinished) {
		    	 detailedText3.setText(getResources().getString(R.string.tytext3) + " " + (millisUntilFinished / 1000) + ((millisUntilFinished / 1000 > 1) ? " " +getResources().getString(R.string.seconds)+" " : " "+getResources().getString(R.string.second)+" "));
		     }
		     public void onFinish() {
		    	 finish();
		     }
		  }.start();
	}
	

}
