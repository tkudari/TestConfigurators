package com.dashwire.base.device;

/**
 * Author: tbostelmann
 */
public interface DashSettings {
    public static final String PROPERTY_BOOLEAN_OVERRIDE_SERVER = "overrideServer";
    public static final String PROPERTY_STRING_OVERRID_SERVER_HOST = "overrideServerHost";
    public static final String PROPERTY_STRING_TRACKING_ID = "tracking_id";
    public static final String PROPERTY_STRING_TRACKING_URI = "tracking_uri";
    public static final String PROPERTY_STRING_DEVICE_ID = "device_id";

    public String getServerHostname();

    public String getTrackingUri();

    public String getTrackingId();

    public String getPhoneNumber();

    public String getIMEI();

    public String getAndroidId();

    public String getClientVersion();

    public String getBuildRelease();

    public String getBuildIncremental();

    public String getBuildSDK();

    public String getBuildDevice();

    public String getBuildManufacturer();
}
