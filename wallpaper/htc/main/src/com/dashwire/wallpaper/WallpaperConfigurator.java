package com.dashwire.wallpaper;

import android.content.Intent;
import android.util.Log;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.launcher.wp.HtcLauncherModel;
import com.dashwire.configurator.ConfiguratorBaseIntentService;
import org.json.JSONException;
import org.json.JSONObject;


public class WallpaperConfigurator extends ConfiguratorBaseIntentService
{
	private static final String TAG = WallpaperConfigurator.class.getCanonicalName();
    protected boolean isFirstLaunch = false;
    protected String HTCSenseLevel = "";


    @Override
	protected void onConfigFeature()
	{
        isFirstLaunch = getLastIntent().getBooleanExtra("isFirstLaunch", false);
        HTCSenseLevel = getLastIntent().getStringExtra("HTCSenseLevel");

        DashLogger.v(TAG, "featureDataJSONArray : " + mFeatureDataJSONArray.toString());

        // Note: This works for a couple given phones. As we find additional values we will have to call them out explicitly
        if (HTCSenseLevel.startsWith("4")) {
            configureHTCSenseFourOh();
        } else {
            configureHTCSenseFiveOh();
        }
	}


    void configureHTCSenseFourOh() {
        HtcLauncherModel model = new HtcLauncherModel(getApplicationContext());
        String uri = null;
        try {
            uri = mFeatureDataJSONArray.getJSONObject(0).getString("uri");
            DashLogger.v(TAG, "Wallpaper URI: " + uri);
        } catch (Exception ex) {
            System.out.println(ex.toString());
            DashLogger.v(TAG,"Exception : " + ex.getMessage());
        }
        if (uri != null) {
            model.addWallpaper(uri);
            model.store();
        } else {
            onFailure("Invalid wallpaper uri");
        }
        onSuccess();
    }

    void configureHTCSenseFiveOh() {
        try {
            JSONObject data = mFeatureDataJSONArray.getJSONObject(0);
            String sUri = data.get( "uri" ).toString();
            Log.v(TAG, "Got wallpaper config value: " + sUri);
            final String expectedPath = "file:///system/customize/resource/";
            if (sUri.indexOf(expectedPath) != -1) {
                String wallpaper = sUri.substring(expectedPath.length());
                Log.v(TAG, "Configuring wallpaper: " + wallpaper);
                Log.v(TAG, "Specifying is first boot: " + isFirstLaunch);
                Intent chgSkinIntent = new Intent();
                chgSkinIntent.setAction("com.htc.home.personalize.ACTION_CONFIG_FROM_AURORA2");
                chgSkinIntent.putExtra( "extra_wallpaper_displayname", " " );
                chgSkinIntent.putExtra( "extra_wallpaperguid", wallpaper );
                chgSkinIntent.putExtra("extra_in_oobe", isFirstLaunch);
                getApplicationContext().sendBroadcast(chgSkinIntent);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error in JSONObject" , e);
            onFailure("Invalid wallpaper uri");
            return;
        }
        onSuccess();
    }
}
