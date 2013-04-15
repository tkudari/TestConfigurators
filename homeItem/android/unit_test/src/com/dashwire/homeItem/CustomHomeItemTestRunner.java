package com.dashwire.homeItem;

import java.lang.reflect.Method;

import org.junit.runners.model.InitializationError;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

public class CustomHomeItemTestRunner extends RobolectricTestRunner {

	public CustomHomeItemTestRunner(Class testClass)
			throws InitializationError {
		super(testClass);
	} 

	    @Override protected void bindShadowClasses() {
	    	super.bindShadowClasses();
	        Robolectric.bindShadowClass(DWShadowIntentFilter.class);
	        Robolectric.bindShadowClass(DWShadowContext.class);
	    }
}
