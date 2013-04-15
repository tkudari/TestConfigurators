package com.dashwire.account;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Method;

/**
 * User: tbostelmann
 * Date: 2/1/13
 */
public class CustomAccountTestRunner  extends RobolectricTestRunner {

    public CustomAccountTestRunner(Class testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    public void beforeTest(Method method) {
        Robolectric.bindShadowClass(DWShadowContentResolver.class);
        Robolectric.bindShadowClass(DWShadowAccountManager.class);
    }
}
