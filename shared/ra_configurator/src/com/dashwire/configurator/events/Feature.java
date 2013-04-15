package com.dashwire.configurator.events;

import android.content.Intent;

public class Feature
{
	private String featureId;
	private String featureName;
	private String featureData;
	public int featureConfigStatus;
	public String featureResult;
	private Intent intent;
	
    public static final int IN_PROGRESS = 0;
    public static final int SUCCESS = 1;
    public static final int FAILED = 2;
    
    public Feature(Intent intent)
    {
    	if (intent != null)
    	{
        	this.featureId = intent.hasExtra("featureId") ? intent.getExtras().getString("featureId") : null;
        	this.featureName = intent.hasExtra("featureName") ? intent.getExtras().getString("featureName") : null;
        	this.featureData = intent.hasExtra("featureData") ? intent.getExtras().getString("featureData") : null;
        	this.intent = intent;
    	}
    }
    
    public void setFeatureId(String featureId)
    {
    	this.featureId = featureId;
    	if (this.intent.hasExtra("featureId"))
    	{
    		this.intent.removeExtra(featureId);
    	}
    	this.intent.putExtra("featureId", featureId);
    }
    
    public void setFeatureName(String featureName)
    {
    	this.featureName= featureName;
    	if (this.intent.hasExtra("featureName"))
    	{
    		this.intent.removeExtra(featureName);
    	}
    	this.intent.putExtra("featureName", featureName);
    }
    
    public void setFeatureData(String featureData)
    {
    	this.featureData= featureData;
    	if (this.intent.hasExtra("featureData"))
    	{
    		this.intent.removeExtra(featureData);
    	}
    	this.intent.putExtra("featureData", featureData);
    }
    
    public String getFeatureId()
    {
    	return this.featureId;
    }
    
    public String getFeatureName()
    {
    	return this.featureName;
    }
    
    public String getFeatureData()
    {
    	return this.featureData;
    }
    
    public Intent getIntent()
    {
    	return this.intent;
    }
}
