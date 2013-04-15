package com.dashwire.nfc.ui;

import com.dashwire.nfc.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class SplashActivity extends Activity {

	Handler delayHandler;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		delayHandler = new Handler();
		delayHandler.postDelayed(new DelayWelcomeScreen(this), 2000);
	}
	
	class DelayWelcomeScreen implements Runnable {
		Context context;

		public DelayWelcomeScreen(Context context) {
			this.context = context;
		}

		public void run() {
			Intent welcomeIntent = new Intent(getApplicationContext(), WelcomeActivity.class);
			welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			context.startActivity(welcomeIntent);
		}
	}
}