package com.dashwire.testing.helper.screenshot;

public class ScreenShotRequest {
	
	public String screenToTake;
	public String featureName;
	
	public ScreenShotRequest(String screenToTake, String featureName) {
		this.screenToTake = screenToTake;
		this.featureName = featureName;
	}
}
