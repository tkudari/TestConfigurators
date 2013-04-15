package com.dashwire.config.launcher.wp;

import android.content.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

class SceneInfo {
    private static String TAG = SceneInfo.class.getCanonicalName();

    private final Context mContext;

    private WallpaperInfo mWallpaper;

    SceneInfo(Context context) {
        mContext = context;
    }

    void addWallpaper(WallpaperInfo wp) {
        mWallpaper = wp;
    }

    /**
     * Returns a DOM representation of this SceneInfo model
     *
     * @return Document representing SceneInfo model
     */
    Document buildDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            //DashLogger.e(TAG, "Exception creating new document in SceneInfo.", e);

            return null;
        }

        Document document = builder.newDocument();

        Element scene = document.createElement("scene");
        scene.setAttribute("name", "ATT");
        scene.setAttribute("homeScreen", "1");
        scene.setAttribute("page_count", "3");

        document.appendChild(scene);

        if (mWallpaper != null) {
            scene.appendChild(mWallpaper.asDomElement(document));
        }

        return document;
    }

    /**
     * Writes this model out to an XML document on disk and returns a File object referencing the
     * new file.
     *
     * @return File object referencing XML document on disk.
     */
    File writeDocument() {
        Document document = buildDocument();

        FileOutputStream fos = null;
        String filename = "newSceneWP.xml";

        try {
            fos = mContext.openFileOutput(
                    filename, Context.MODE_WORLD_READABLE);//TODO: try private
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return null;
        }

        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }

        return mContext.getFileStreamPath(filename);
    }
}
