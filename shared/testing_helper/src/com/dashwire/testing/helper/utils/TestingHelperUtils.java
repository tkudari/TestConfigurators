package com.dashwire.testing.helper.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TestingHelperUtils {

	private static final String TAG = "FeatureUtils";

	public static void launchHomeScreen(Context context) {
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(startMain);
	}

	public static void moveFiles(String fromPath, String toPath) throws IOException {
		File newFile;
		File folder = new File(fromPath);
		File newFolder = new File(toPath);
		newFolder.mkdir();
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				newFile = new File(toPath + File.separator + file.getName());
				Log.v(TAG, "current file = " + file.getAbsolutePath());
				Log.v(TAG, "new file = " + newFile.getAbsolutePath());
				copyFile(file, newFile);
				file.delete();
			}
		}
	}

	public static void copyFile(File inFile, File outFile) throws IOException {
		InputStream in = new FileInputStream(inFile);
		OutputStream out = new FileOutputStream(outFile);
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public static void deleteFile(String path) {
		File file = new File(path);
		file.delete();
	}
}
