package com.dashwire.features.testing.events;

public class FeatureTestingInProgressEvent extends FeatureTesting
{
	public FeatureTestingInProgressEvent(FeatureTesting feature)
	{
		super(feature.featureId, feature.featureName, feature.featureData, feature.screenToTest, feature.testType);
		this.featureConfigStatus = feature.IN_PROGRESS;
	}
}
