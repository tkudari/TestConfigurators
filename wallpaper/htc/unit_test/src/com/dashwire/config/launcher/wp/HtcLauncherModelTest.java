package com.dashwire.config.launcher.wp;

import com.dashwire.config.launcher.wp.HtcLauncherModel;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowLog;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import android.content.Context;
import android.app.Activity;

import java.io.File;

import android.content.Intent;
import java.util.List;

import com.xtremelabs.robolectric.shadows.ShadowLog.LogItem;


@RunWith(RobolectricTestRunner.class)
public class HtcLauncherModelTest {
    @Test
    public void testStoreWithNoWallpaperSet() {
        Activity act = new Activity();
        Context ctxt = act.getApplicationContext();
        HtcLauncherModel model = new HtcLauncherModel(ctxt);
        model.store();
        List<Intent> intents = Robolectric.getShadowApplication().getBroadcastIntents();
        Intent i = intents.get(0);

        Assert.assertEquals("com.htc.home.personalize.ACTION_READY2GO", i.getAction());
        Assert.assertTrue(i.getStringExtra("EXTRA_SCENE_PATH").contains("newSceneWP.xml"));
    }

    @Test
    public void testStoreWithWallpaperSet() {
        Activity act = new Activity();
        Context ctxt = act.getApplicationContext();
        HtcLauncherModel model = new HtcLauncherModel(ctxt);
        model.addWallpaper("https://www.google.com/wallpaper");
        model.store();
        List<Intent> intents = Robolectric.getShadowApplication().getBroadcastIntents();
        Intent i = intents.get(0);

        Assert.assertEquals("com.htc.home.personalize.ACTION_READY2GO", i.getAction());
        Assert.assertTrue(i.getStringExtra("EXTRA_SCENE_PATH").contains("newSceneWP.xml"));
    }

    @Test
    public void testAddWallpaper() {
        Activity act = new Activity();
        Context ctxt = act.getApplicationContext();
        HtcLauncherModel model = new HtcLauncherModel(ctxt);
        model.addWallpaper("https://www.google.com/wallpaper");
        model.store();

        List<LogItem> logs = ShadowLog.getLogs();
        LogItem item_of_interest = null;
        for (LogItem li : logs) {
            if (li.msg.startsWith("XML File = ")) {
                item_of_interest = li;
                break;
            }
        }

        String path = item_of_interest.msg.split(" ")[3];
        String content = null;
        try { content = new java.util.Scanner(new File(path)).useDelimiter("\\A").next(); } catch (Exception ex) { Assert.fail(ex.toString()); }

        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><scene homeScreen=\"1\" name=\"ATT\" page_count=\"3\"><WallpaperSetting itemType=\"static_wallpaper\" resFile=\"/wallpaper\"/></scene>", content);

    }
}
