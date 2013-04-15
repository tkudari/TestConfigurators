package com.dashwire.config.launcher.wp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;

public class HtcLauncherModel {

    private static final String TAG = HtcLauncherModel.class.getCanonicalName();

    private final SceneInfo mSceneInfo;

    private final Context mContext;

    public HtcLauncherModel(Context context) {
        mSceneInfo = new SceneInfo(context);
        mContext = context;
    }

    public void store() {
        File file = mSceneInfo.writeDocument();
        if (file != null) {
            Log.v(TAG, "XML File = " + file.getAbsolutePath());
            Intent clientIntent = new Intent(
                    "com.htc.home.personalize.ACTION_READY2GO");
            clientIntent.putExtra("EXTRA_SCENE_PATH", file.getAbsolutePath());
            mContext.sendBroadcast(clientIntent);
        } else {
            Log.v(TAG, "XML File was null. No action taken");
        }
    }

    public void addWallpaper(String wallpaperUri) {
        mSceneInfo.addWallpaper(new WallpaperInfo(
                SceneSettings.Favorites.ITEM_TYPE_WALLPAPER_STATIC,
                wallpaperUri));
    }
}
