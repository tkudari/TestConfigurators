package com.dashwire.nfc.ui;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dashwire.nfc.R;
import com.dashwire.nfc.events.NFCFeature;
import com.dashwire.nfc.events.NFCFeatureResultEvent;
import com.dashwire.nfc.utils.BusProvider;
import com.dashwire.nfc.utils.FeatureBehaviorOverrides;
import com.squareup.otto.Subscribe;

public class ConfigurationStatusActivity extends Activity {

	private static final String TAG = "ConfigurationStatusActivity";
	TextView shortMessage, detailedMessage;
	private NFCFeature currentFeature = null;
	Button finish;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configure);
		
		currentFeature = new NFCFeature(getIntent());
		shortMessage = (TextView) findViewById(R.id.short_message);
		detailedMessage = (TextView) findViewById(R.id.detailed_explanation);
		
		Typeface fontExoBold = Typeface.createFromAsset(getAssets(), "Exo-DemiBold.otf"); 
		Typeface fontExoLight = Typeface.createFromAsset(getAssets(), "Exo-Light.otf");  
		shortMessage.setTypeface(fontExoBold);
		detailedMessage.setTypeface(fontExoLight);
		
		finish = (Button) findViewById(R.id.finish_button);
		finish.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchThankYouScreen();
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		BusProvider.getInstance().register(this);	
		updateScreen();
	}

	@Override
	protected void onDestroy() {
		BusProvider.getInstance().unregister(this);	
		super.onDestroy();
	}
	
	@Subscribe
	public void onFeatureResult(NFCFeatureResultEvent event) {
		Log.v(TAG, "onFeatureResult");
		currentFeature = event;
		updateScreen();
	}
	
	private void updateScreen()
	{
		if (currentFeature.featureConfigStatus == NFCFeature.IN_PROGRESS)
		{
			shortMessage.setVisibility(View.GONE);
			detailedMessage.setVisibility(View.GONE);
			finish.setVisibility(View.INVISIBLE);
		} else if (currentFeature.featureConfigStatus == NFCFeature.SUCCESS)
		{
			shortMessage.setVisibility(View.VISIBLE);
			detailedMessage.setVisibility(View.VISIBLE);
			finish.setVisibility(View.VISIBLE);
			shortMessage.setText(this.getString(R.string.success_short));
			try {
				JSONArray featureDataArray = new JSONArray(currentFeature.getFeatureData());
				if (featureDataArray.getJSONObject(0).has("title"))
				{
					detailedMessage.setText("\"" + featureDataArray.getJSONObject(0).getString("title") +"\""+ " " +  this.getString(NFCFeature.sNFCFeaturesNames.get(currentFeature.getFeatureName())) + " " + this.getString(R.string.success_detail));
				} else
				{
					detailedMessage.setText( this.getString(NFCFeature.sNFCFeaturesNames.get(currentFeature.getFeatureName())) + " " + this.getString(R.string.success_detail));
				}
			} catch (JSONException e) {
				Log.e(TAG,"JSONException : " + e.getMessage());
			}
		} else if (currentFeature.featureConfigStatus == NFCFeature.FAILED)
		{
			if (FeatureBehaviorOverrides.needsCustomErrorMessage(currentFeature.getFeatureName())) {
				String errorMessage = FeatureBehaviorOverrides.getCustomErrorMessage(this, currentFeature.getFeatureName(), currentFeature.failedReason);
				
				SpannableString styledErrorMsg = new SpannableString(errorMessage); 
//				styledErrorMsg.setSpan(new StyleSpan(Typeface.BOLD), 0, 15, 0); 

				ImageSpan is = new ImageSpan(this, R.drawable.alert_icon_halfsize);
				SpannableString imageAdder = new SpannableString("bleh" + "\n\n");
				imageAdder.setSpan(is, 0, 4, 0);
												
				shortMessage.setVisibility(View.VISIBLE);
				detailedMessage.setVisibility(View.VISIBLE);
				finish.setVisibility(View.VISIBLE);
				shortMessage.setText("Personalizing " + this.getString(NFCFeature.sNFCFeaturesNames.get(currentFeature.getFeatureName())));				
				detailedMessage.setText(imageAdder);
				detailedMessage.append(styledErrorMsg);

				
			} else {
				shortMessage.setVisibility(View.VISIBLE);
				detailedMessage.setVisibility(View.VISIBLE);
				finish.setVisibility(View.VISIBLE);
				shortMessage.setText(this.getString(R.string.failure_short));
				detailedMessage.setText(this.getString(R.string.failed_detail1) + " " + this.getString(NFCFeature.sNFCFeaturesNames.get(currentFeature.getFeatureName()))
						+ "\n" + currentFeature.failedReason);
			}			
		}
		
	}
	
	private void launchThankYouScreen() {
		Intent thankYouIntent = new Intent(getApplicationContext(), ThankYouActivity.class);
		thankYouIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(thankYouIntent);
	}
}
