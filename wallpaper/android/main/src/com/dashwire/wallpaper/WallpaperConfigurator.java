package com.dashwire.wallpaper;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.configurator.ConfiguratorBaseIntentService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class WallpaperConfigurator extends ConfiguratorBaseIntentService
{

	private static final String TAG = WallpaperConfigurator.class.getCanonicalName();
	private HashMap<String, String> recordedExceptions = new HashMap<String, String>();

	@Override
	protected void onConfigFeature()
	{
		try
		{
			DashLogger.v(TAG, "featureDataJSONArray : " + mFeatureDataJSONArray.toString());
			JSONObject data = mFeatureDataJSONArray.getJSONObject(0);
			Uri uri = Uri.parse(data.get("uri").toString());
			String scheme = uri.getScheme();
			if (scheme.equals("file"))
			{
				setFileWallpaper(uri);
			} else if (scheme.equals("android.resource"))
			{
				setResourceWallpaper(uri);
			}
			DashLogger.v(TAG,"after setting wallpaper uri");
		} catch (Exception e)
		{
			DashLogger.v(TAG,"Exception : " + e.getMessage());
			e.printStackTrace();
			onFailure(TAG + " Exception : " + e.getMessage());
			return;
		}
		DashLogger.v(TAG,"Wallpaper set calling onSuccess");
		onSuccess();
	}

	private void setFileWallpaper(Uri uri) throws IOException, NullPointerException
	{
		InputStream in = getApplicationContext().getContentResolver().openInputStream(uri);
		try
		{
			WallpaperManager.getInstance(getApplicationContext()).setStream(in);
		} 
		
		catch (NullPointerException e)
		{
			recordException("setFileWallpaper", e);
			throw e;
		} 
		
		finally
		{
			in.close();
		}
	}

	private void setResourceWallpaper(Uri uri) throws NameNotFoundException, IOException, IndexOutOfBoundsException, NullPointerException
	{
		DashLogger.v(TAG,"Setting Wallpaper as android resource");
		String packageName = uri.getHost();
		List<String> pathSegments = uri.getPathSegments();
		String resourceType = null, resourceName = null;
		try 
		{
			resourceType = pathSegments.get(0);
			resourceName = pathSegments.get(1);
		} 
		
		catch (IndexOutOfBoundsException e)
		{
			recordException("setResourceWallpaper", e);
			throw e;
		}
		PackageManager packageManager = getApplicationContext().getPackageManager();

		DashLogger.v(TAG,"packageName = " + packageName);
		Context appContext = getApplicationContext().createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
		final Resources resources = packageManager.getResourcesForApplication(packageName);

		DashLogger.v(TAG,"Resource Name = " + resourceName);
		
		int res;
		try 
		{
			res = resources.getIdentifier(resourceName, resourceType, packageName);
		} 
		catch (NullPointerException e) 
		{
			recordException("setResourceWallpaper", e);
			throw e;
		}
		DashLogger.v(TAG,"res = " + res);
		setResourceWallpaper(appContext, res);
	}

	private void setResourceWallpaper(Context ctx, int resource) throws IOException
	{
		DashLogger.v(TAG,"Setting resource : " + resource);
		WallpaperManager.getInstance(ctx).setResource(resource);
		DashLogger.v(TAG,"After setting in wallpaper manager : " + resource);
	}
	
	public void injectFeatureJSONArray(JSONArray jsonArray) 
	{
		mFeatureDataJSONArray = jsonArray;
	}
	
	public void onConfigFeatureWrapper() 
	{
		this.onConfigFeature();
	}
	
	private void recordException(String methodName, Exception e) 
	{
		recordedExceptions.put(methodName, e.getClass().getName());
	}
	
	public String getLastRecordedException(String methodName) 
	{
		return recordedExceptions.get(methodName);
	}
}
