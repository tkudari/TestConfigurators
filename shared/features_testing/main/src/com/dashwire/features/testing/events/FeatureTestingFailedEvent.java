package com.dashwire.features.testing.events;

public class FeatureTestingFailedEvent extends FeatureTesting
{
	String failedReason;
	
	public FeatureTestingFailedEvent(FeatureTesting feature, String failedReason)
	{
		super(feature.featureId, feature.featureName, feature.featureData, feature.screenToTest, feature.testType);
		this.failedReason = failedReason;
		this.featureConfigStatus = feature.FAILED;
	}
}
