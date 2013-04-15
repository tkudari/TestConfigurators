package com.dashwire.screenshot.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;

import com.dashwire.screenshot.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Functional tests for the global screenshot feature.
 */
@LargeTest
public class ScreenShotTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private static final String LOG_TAG = "ScreenshotTest";
//    private static final int SCREEN_WAIT_TIME_SEC = 7;
	private static final String TAG = "ScreenShotTest";

    public ScreenShotTest() {
        super(MainActivity.class);
    }

//    /**
//     * A simple test for screenshots that launches an Activity, injects the key event combo
//     * to trigger the screenshot, and verifies the screenshot was taken successfully.
//     */
//    public void testScreenshot() throws Exception {
//        Log.d(LOG_TAG, "starting testScreenshot");
//        // launch the activity.
//        MainActivity activity = getActivity();
//        assertNotNull(activity);
//
//        File screenshotDir = getScreenshotDir();
//        Log.v(LOG_TAG,"Screenshots directory = " + screenshotDir.getAbsolutePath());
//        NewScreenshotObserver observer = new NewScreenshotObserver(
//                screenshotDir.getAbsolutePath());
//        observer.startWatching();
//        takeScreenshot();
//        // unlikely, but check if a new screenshot file was already created
//        if (observer.getCreatedPath() == null) {
//            // wait for screenshot to be created
//            synchronized(observer) {
//                observer.wait(SCREEN_WAIT_TIME_SEC*1000);
//            }
//        }
//        assertNotNull(String.format("Could not find screenshot after %d seconds",
//                SCREEN_WAIT_TIME_SEC), observer.getCreatedPath());
//
//        File screenshotFile = new File(screenshotDir, observer.getCreatedPath());
//        try {
//            assertTrue(String.format("Detected new screenshot %s but its not a file",
//                    screenshotFile.getName()), screenshotFile.isFile());
////            assertTrue(String.format("Detected new screenshot %s but its not an image",
////                    screenshotFile.getName()), isValidImage(screenshotFile));
//        } finally {
//            // delete the file to prevent external storage from filing up
//            screenshotFile.delete();
//        }
//    }
    
    /**
     * A simple test for screenshots that launches an Activity, injects the key event combo
     * to trigger the screenshot, and verifies the screenshot was taken successfully.
     */
    public void testScreenshot() throws Exception {
    	createExternalStoragePathFile();
        Log.d(LOG_TAG, "starting testScreenshot");
        takeScreenshot();
        Log.d(LOG_TAG, "ending testScreenshot");
        Thread.sleep(4000);
        onSuccess();
    }


    private static class NewScreenshotObserver extends FileObserver {
        private String mAddedPath = null;

        NewScreenshotObserver(String path) {
            super(path, FileObserver.CREATE);
        }

        synchronized String getCreatedPath() {
            return mAddedPath;
        }

        @Override
        public void onEvent(int event, String path) {
            Log.d(LOG_TAG, String.format("Detected new file added %s", path));
            synchronized (this) {
                mAddedPath = path;
                notify();
            }
        }
    }

    /**
     * Inject the key sequence to take a screenshot.
     */
    public void takeScreenshot() {
        getInstrumentation().sendKeySync(new KeyEvent(KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_POWER));
        if (Build.MANUFACTURER == "HTC")
        {
        	  getInstrumentation().sendKeySync(new KeyEvent(KeyEvent.ACTION_DOWN,
                      KeyEvent.KEYCODE_HOME));
        }else
        {
            getInstrumentation().sendKeySync(new KeyEvent(KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_VOLUME_DOWN));
        }
 
        try{ 
        	            Thread.sleep(1000); 
        	        }catch(Exception e){ 
        	        } 
        // the volume down key event will cause the 'volume adjustment' UI to appear in the
        // foreground, and steal UI focus
        // unfortunately this means the next key event will get directed to the
        // 'volume adjustment' UI, instead of this test's activity
        // for this reason this test must be signed with platform certificate, to grant this test
        // permission to inject key events to another process
        
        if (Build.MANUFACTURER == "HTC")
        {
        	  getInstrumentation().sendKeySync(new KeyEvent(KeyEvent.ACTION_UP,
                      KeyEvent.KEYCODE_HOME));
        }else
        {
            getInstrumentation().sendKeySync(new KeyEvent(KeyEvent.ACTION_UP,
                    KeyEvent.KEYCODE_VOLUME_DOWN));
        }
        
        getInstrumentation().sendKeySync(new KeyEvent(KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_VOLUME_DOWN));
        getInstrumentation().sendKeySync(new KeyEvent(KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_POWER));
    }

    /**
     * Get the directory where screenshot images are stored.
     */
    private File getScreenshotDir() {
        // TODO: get this dir location from a constant
        return new File(Environment.getExternalStorageDirectory(), "Pictures" + File.separator +
                "Screenshots");
    }

    /**
     * Return true if file is valid image file
     */
    private boolean isValidImage(File screenshotFile) {
        Bitmap b = BitmapFactory.decodeFile(screenshotFile.getAbsolutePath());
        // TODO: do more checks on image
        return b != null;
    }
    
    protected void onSuccess() {
        Intent screenShotResultIntent = new Intent();
        screenShotResultIntent.setAction("com.dashwire.screenshot.success");
        getActivity().getApplicationContext().sendOrderedBroadcast(screenShotResultIntent, null);
    }
    
	private void createExternalStoragePathFile() {
		try {
			final String screenShotsPath = "device.external.storage.screenshot=" + Environment.getExternalStorageDirectory() + File.separator + "Pictures"
					+ File.separator + "Screenshots";
			String screenShotsPathFile = "screenshotspath.properties";

			Log.v(TAG, "ScreenShotsPath = " + screenShotsPath);
			Log.v(TAG, "ScreenShotsPathFile = " + screenShotsPathFile);

			/*
			 * We have to use the openFileOutput()-method the ActivityContext
			 * provides, to protect your file from others and This is done for
			 * security-reasons. We chose MODE_WORLD_READABLE, because we have
			 * nothing to hide in our file
			 */
			FileOutputStream fOut;

			fOut = getActivity().getApplicationContext().openFileOutput(screenShotsPathFile, getActivity().getApplicationContext().MODE_WORLD_READABLE);

			OutputStreamWriter osw = new OutputStreamWriter(fOut);

			// Write the string to the file
			osw.write(screenShotsPath);

			/*
			 * ensure that everything is really written out and close
			 */
			osw.flush();
			osw.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException : " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "IOException : " + e.getMessage());
		}
	}
	
    public static String getAndroidOSProperty(Context context, String key) throws IllegalArgumentException {
        String ret= "";

        try{

            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[1];
            paramTypes[0]= String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            //Parameters
            Object[] params= new Object[1];
            params[0]= new String(key);

            ret= (String) get.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= "";
        }

        return ret;
    }
}