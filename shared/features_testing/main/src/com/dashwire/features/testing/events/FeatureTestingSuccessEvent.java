package com.dashwire.features.testing.events;

public class FeatureTestingSuccessEvent extends FeatureTesting
{
	public FeatureTestingSuccessEvent(FeatureTesting feature)
	{
		super(feature.featureId, feature.featureName, feature.featureData, feature.screenToTest, feature.testType);
		this.featureConfigStatus = feature.SUCCESS;
	}
}
