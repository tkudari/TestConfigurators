package com.dashwire.nfc.ui;



import com.dashwire.nfc.R;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: grant
 * Date: 2/19/13
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class WelcomeActivity extends Activity {


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		TextView detailedText1 = (TextView) findViewById(R.id.DetailedText1); 
		TextView detailedText2 = (TextView) findViewById(R.id.DetailedText2); 
		Typeface font = Typeface.createFromAsset(getAssets(), "Exo-Light.otf");  
		detailedText1.setTypeface(font);
		detailedText2.setTypeface(font);
	}
}