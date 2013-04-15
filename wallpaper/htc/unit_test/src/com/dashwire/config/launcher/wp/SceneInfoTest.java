package com.dashwire.config.launcher.wp;

import com.dashwire.config.launcher.wp.SceneInfo;
import com.dashwire.config.launcher.wp.SceneSettings;
import com.dashwire.config.launcher.wp.WallpaperInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.content.Context;
import android.app.Activity;

import java.io.File;
import java.io.FileInputStream;


@RunWith(RobolectricTestRunner.class)
public class SceneInfoTest {

    @Test
    public void testDocumentCreatedWithWallpaperSet() {
        WallpaperInfo info = new WallpaperInfo(SceneSettings.BaseLauncherColumns.ITEM_TYPE_WALLPAPER_STATIC, "https://www.google.com/wallpaper");
        Activity act = new Activity();
        Context ctxt = act.getApplicationContext();
        SceneInfo scene = new SceneInfo(ctxt);
        scene.addWallpaper(info);

        Document doc = scene.buildDocument();

        Node n = doc.getFirstChild();

        Assert.assertEquals("scene", n.getNodeName());
        Assert.assertEquals("ATT", n.getAttributes().getNamedItem("name").getNodeValue());
        Assert.assertEquals("1", n.getAttributes().getNamedItem("homeScreen").getNodeValue());
        Assert.assertEquals("3", n.getAttributes().getNamedItem("page_count").getNodeValue());

        n = n.getFirstChild();
        Assert.assertEquals("WallpaperSetting", n.getNodeName());
        Assert.assertEquals("static_wallpaper", n.getAttributes().getNamedItem("itemType").getNodeValue());
        Assert.assertEquals("/wallpaper", n.getAttributes().getNamedItem("resFile").getNodeValue());
    }

    @Test
    public void testDocumentCreatedWithNoWallpaperSet() {
        WallpaperInfo info = new WallpaperInfo(SceneSettings.BaseLauncherColumns.ITEM_TYPE_WALLPAPER_STATIC, "https://www.google.com/wallpaper");
        Activity act = new Activity();
        Context ctxt = act.getApplicationContext();
        SceneInfo scene = new SceneInfo(ctxt);

        Document doc = scene.buildDocument();

        Node n = doc.getFirstChild();

        Assert.assertEquals("scene", n.getNodeName());
        Assert.assertEquals("ATT", n.getAttributes().getNamedItem("name").getNodeValue());
        Assert.assertEquals("1", n.getAttributes().getNamedItem("homeScreen").getNodeValue());
        Assert.assertEquals("3", n.getAttributes().getNamedItem("page_count").getNodeValue());

        Assert.assertFalse(n.hasChildNodes());
    }

    /*
    Note that this test actually creates an output file in a temp folder e.g.:
    /var/folders/3q/3q2UuX3HHOS3ROVEw0xDmk+++TI/-Tmp-/android-tmp274915940218232442robolectric/newSceneWP.xml

    It may fail if the running account does not have access to that.

    To fix that problem should it arise we will need to mock out the FileOutputStream class from java.
     */
    @Test
    public void testDocumentWriteWithWallpaperSet() {
        WallpaperInfo info = new WallpaperInfo(SceneSettings.BaseLauncherColumns.ITEM_TYPE_WALLPAPER_STATIC, "https://www.google.com/wallpaper");
        Activity act = new Activity();
        Context ctxt = act.getApplicationContext();
        SceneInfo scene = new SceneInfo(ctxt);
        scene.addWallpaper(info);
        File f = scene.writeDocument();
        FileInputStream fis = null;
        try { fis = ctxt.openFileInput(f.getName()); } catch (Exception ex) { Assert.fail(ex.getMessage());}
        String s = new java.util.Scanner(fis).useDelimiter("\\A").next();

        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><scene homeScreen=\"1\" name=\"ATT\" page_count=\"3\"><WallpaperSetting itemType=\"static_wallpaper\" resFile=\"/wallpaper\"/></scene>", s);
    }


    @Test
    public void testDocumentWriteWithNoWallpaperSet() {
        Activity act = new Activity();
        Context ctxt = act.getApplicationContext();
        SceneInfo scene = new SceneInfo(ctxt);
        File f = scene.writeDocument();
        FileInputStream fis = null;
        try { fis = ctxt.openFileInput(f.getName()); } catch (Exception ex) { Assert.fail(ex.getMessage());}
        String s = new java.util.Scanner(fis).useDelimiter("\\A").next();

        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><scene homeScreen=\"1\" name=\"ATT\" page_count=\"3\"/>", s);
    }


}
