package com.dashwire.wifi;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.Robolectric;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Method;

public class CustomWifiTestRunner extends RobolectricTestRunner {

    public CustomWifiTestRunner(Class testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    public void beforeTest(Method method) {
        Robolectric.bindShadowClass(DWShadowWifiManager.class);
    }


}
