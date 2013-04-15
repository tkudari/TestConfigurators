package com.dashwire.config.launcher.wp;

import com.dashwire.config.launcher.wp.SceneSettings;
import com.dashwire.config.launcher.wp.WallpaperInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


@RunWith(RobolectricTestRunner.class)
public class WallpaperInfoTest {

    @Test
    public void testWallpaperInfo() {

        new WallpaperInfo(1,"file://someplace");

    }

    Document createDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            Assert.fail();
        }

        return builder.newDocument();
    }

    @Test
    public void testStaticWallpaper() {
        WallpaperInfo info = new WallpaperInfo(SceneSettings.BaseLauncherColumns.ITEM_TYPE_WALLPAPER_STATIC, "https://www.google.com/wallpaper");
        Document doc = createDocument();
        Element ele = info.asDomElement(doc);

        Assert.assertEquals("WallpaperSetting", ele.getNodeName());
        Assert.assertEquals("static_wallpaper", ele.getAttribute("itemType"));
        Assert.assertEquals("/wallpaper", ele.getAttribute("resFile"));
    }

    @Test
    public void testLiveWallpaper() {
        WallpaperInfo info = new WallpaperInfo(SceneSettings.BaseLauncherColumns.ITEM_TYPE_WALLPAPER_LIVE, "https://www.google.com/wallpaper");
        Document doc = createDocument();
        Element ele = info.asDomElement(doc);

        Assert.assertEquals("WallpaperSetting", ele.getNodeName());
        Assert.assertEquals("live_wallpaper", ele.getAttribute("itemType"));
        Assert.assertEquals("/wallpaper", ele.getAttribute("resFile"));
    }

    @Test
    public void testSomeOtherValueWallpaper() {
        WallpaperInfo info = new WallpaperInfo(-3452, "https://www.google.com/wallpaper");
        Document doc = createDocument();
        Element ele = info.asDomElement(doc);

        Assert.assertEquals("WallpaperSetting", ele.getNodeName());
        Assert.assertEquals("live_wallpaper", ele.getAttribute("itemType"));
        Assert.assertEquals("/wallpaper", ele.getAttribute("resFile"));
    }

}
