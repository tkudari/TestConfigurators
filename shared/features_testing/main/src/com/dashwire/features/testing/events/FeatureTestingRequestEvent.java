package com.dashwire.features.testing.events;


public class FeatureTestingRequestEvent extends FeatureTesting
{

	public FeatureTestingRequestEvent(String featureId, String featureName, String featureData, String screenToTest, String testType) {
		super(featureId, featureName, featureData, screenToTest, testType);
	}

}
