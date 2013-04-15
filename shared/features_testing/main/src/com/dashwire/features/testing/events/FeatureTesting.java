package com.dashwire.features.testing.events;

public class FeatureTesting
{
	public static final int NEW = 0;
    public static final int IN_PROGRESS = 1;
    public static final int SUCCESS = 2;
    public static final int FAILED = 3;
	
	public String featureId;
	public String featureName;
	public String featureData;
	public int featureConfigStatus = NEW;
	public String screenToTest;
	public String testType;
	
    public FeatureTesting(String featureId, String featureName, String featureData, String screenToTest, String testType)
	{
		this.featureId = featureId;
		this.featureName = featureName;
		this.featureData = featureData;
		this.screenToTest = screenToTest;
		this.testType = testType;
	}
}
