package com.dashwire.ringtone;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Method;

public class CustomRingtoneTestRunner extends RobolectricTestRunner {

    public CustomRingtoneTestRunner(Class testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    public void beforeTest(Method method) {
        Robolectric.bindShadowClass(DWRingtoneManager.class);
        Robolectric.bindShadowClass(DWShadowMediaStoreAudioMedia.class);
    }


}
