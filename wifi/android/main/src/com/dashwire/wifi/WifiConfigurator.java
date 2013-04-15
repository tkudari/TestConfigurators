package com.dashwire.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.dashwire.configurator.ConfiguratorBaseIntentService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WifiConfigurator extends ConfiguratorBaseIntentService
{

	private static final String TAG = WifiConfigurator.class.getCanonicalName();
	private HashMap<String, String> recordedExceptions = new HashMap<String, String>();
    private static final String TIMEOUT_SET_TAG = "alreadySetRetryTimeoutOnce";


    @Override
    protected void onConfigFeature() {
        boolean alreadySet = getLastIntent().getBooleanExtra(TIMEOUT_SET_TAG, false);
        InnerWifiConfiguratorController controller = new InnerWifiConfiguratorController(getApplicationContext(), this, alreadySet);
        controller.startConfiguration(mFeatureDataJSONArray);
    }


    public static class InnerWifiConfiguratorController {
        private Context mContext = null;
        private InnerWifiConfigurationBroadcastReceiver mWifiReceiver = null;
        private final int mRetries = 2;
        private WifiConfigurator mWifi = null;
        private Handler mHandler = null;
        private WifiRadioStartupTimer mStartupTimer = null;
        private boolean alreadySetRetryTimeoutOnce = false;

        private List<WifiConfigurationItem> mItems = new ArrayList<WifiConfigurationItem>();

        public InnerWifiConfiguratorController(Context context, WifiConfigurator wifi, boolean alreadySetTimeout) {
            mContext = context;
            mWifi = wifi;
            Looper looper = Looper.getMainLooper();
            mHandler = new Handler(looper);
            mStartupTimer = new WifiRadioStartupTimer(this);
            alreadySetRetryTimeoutOnce = alreadySetTimeout;
        }

        Context getContext() {
            return mContext;
        }

        WifiConfigurator getWifiConfigurator() {
            return mWifi;
        }

        boolean getAlreadySetRetryTimeoutOnce() {
            return alreadySetRetryTimeoutOnce;
        }

        public List<WifiConfigurationItem> marshalItems(JSONArray array) {
            List<WifiConfigurationItem> items = new ArrayList<WifiConfigurationItem>(array.length());
            for (int i=0; i < array.length(); i++) {
                try {
                    WifiConfigurationItem item = WifiConfigurationItem.generateItem(array.getJSONObject(i));
                    if (item != null) {
                        items.add(item);
                    }
                } catch (Exception ex) {
                    // do nothing
                }
            }
            return items;
        }

        public void cleanupCallbacks() {
            mHandler.removeCallbacks(mStartupTimer);
            unregisterReceiver();
        }

        public void startConfiguration(JSONArray array) {
            mItems = marshalItems(array);

            if (isWifiEnabled()) {
                doSetup();
            } else {
                // enable it and call setup when it is ready
                if (!alreadySetRetryTimeoutOnce) {
                    Log.v( TAG, "Setting the wifi timeout callback");
                    mHandler.postDelayed(mStartupTimer, 10000L); // Give the radio 10 seconds to start up or report failure
                    alreadySetRetryTimeoutOnce = true;
                    registerReceiver();
                    enableWifi();
                } else {
                    Log.v( TAG, "Already set retry timer. Failing b/c radio is not in stable state.");
                    notifyFailure("Attempted to enable wifi twice");
                }
            }
        }

        public void doSetup() {
            for(WifiConfigurationItem item : mItems) {
                boolean succeeded = false;
                for (int i = 0; i < mRetries; i++) {
                    InnerWifiConfigurator config = new InnerWifiConfigurator(mContext);
                    succeeded = config.setupWifi(item);
                    if (succeeded) {
                        break; // No need for retry
                    }
                }
                // Short circuit out if we fail to set up any given wifi
                if (!succeeded) {
                    notifyFailure("setupWifi failed to configure within the given number of retries");
                    return;
                }
            }
            notifySuccess();
        }

        public boolean isWifiEnabled() {
            WifiManager manager = ( WifiManager ) mContext.getSystemService( Context.WIFI_SERVICE );
            return manager.isWifiEnabled();
        }

        public void enableWifi() {
            WifiManager manager = ( WifiManager ) mContext.getSystemService( Context.WIFI_SERVICE );
            manager.setWifiEnabled(true);
        }

        public void notifyFailure(String reason) {
            mWifi.onFailure(reason);
        }

        public void notifySuccess() {
            mWifi.onSuccess();
        }

        public void registerReceiver() {
            // TODO: SECURITY Add component or permission filter.
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction( WifiManager.WIFI_STATE_CHANGED_ACTION );
            mWifiReceiver = new InnerWifiConfigurationBroadcastReceiver(this);

            mContext.registerReceiver( mWifiReceiver, intentFilter );
            // TODO: SECURITY Change back to this one once the sysuid and
            // paltform key is done
            // mContext.registerReceiver(mWifiReceiver, intentFilter,
            // "android.permission.CHANGE_NETWORK_STATE", null);
        }

        public void unregisterReceiver() {
            if (mWifiReceiver != null) {
                mContext.unregisterReceiver(mWifiReceiver);
            } else {
                Log.v( TAG, "mWifiReceiver was null. No action taken." );
            }
        }

        public Intent getLastIntentWithTimeoutTag() {
            return getWifiConfigurator().getLastIntent().putExtra(TIMEOUT_SET_TAG, true);
        }

        public void relaunchIntentToService() {
            cleanupCallbacks();
            Intent i = getLastIntentWithTimeoutTag();
            getContext().startService(i);
        }

        public void failAfterTimeout() {
            cleanupCallbacks();
            notifyFailure("Radio took too long to become enabled");
        }
    }

    public static class WifiRadioStartupTimer implements Runnable {

        InnerWifiConfiguratorController mController = null;

        public WifiRadioStartupTimer(InnerWifiConfiguratorController controller) {
            mController = controller;
        }

        @Override
        public void run() {
            Log.v(TAG, "WifiRadioStartupTimer run method");
            mController.failAfterTimeout();
        }
    }

    public static class InnerWifiConfigurationBroadcastReceiver extends BroadcastReceiver {
        InnerWifiConfiguratorController mController = null;

        public InnerWifiConfigurationBroadcastReceiver(InnerWifiConfiguratorController controller) {
            mController = controller;
        }

        @Override
        public void onReceive( Context context, Intent intent ) {
            WifiManager manager = ( WifiManager ) context.getSystemService( Context.WIFI_SERVICE );
            Log.v( TAG, "checking for wifi" );
            Log.v( TAG, "Wifi State: " + manager.getWifiState());

            if ( manager.getWifiState() == WifiManager.WIFI_STATE_ENABLED ) {
                Log.v(TAG, "wifi enabled");
                mController.relaunchIntentToService();
            }
        }
    }


    // Tested in WifiConfigurationItemTest.java
    public static class WifiConfigurationItem {
        public String SSID = null;
        public String Security = null;
        public String Key = null;

        public WifiConfigurationItem(String ssid, String security, String key)  {
            SSID = ssid;
            Security = security;
            Key = key;
        }

        public static WifiConfigurationItem generateItem(JSONObject item) {
            try {

                String ssid = item.getString( "ssid" );
                String security = item.getString( "security" );
                String key = item.getString( "key" );
                // TODO: Can we push validation of these up here out of InnerWifiConfigurator?
                return new WifiConfigurationItem(ssid, security, key);
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class InnerWifiConfigurator {
        private Context mContext = null;

        public InnerWifiConfigurator(Context context) {
            mContext = context;
        }

        public boolean setupWifi(WifiConfigurationItem item) {
            // TODO wep security
            WifiConfiguration config = null;
            if ( "none".equals( item.Security ) ) {
                config = generateSecurityLevelNoneConfig(item.SSID);
            } else if ( "wep".equals( item.Security ) ) {
                config = generateSecurityLevelWEPConfig(item.SSID, item.Key);
            } else {
                config = generateSecurityLevelWPAConfig(item.SSID, item.Key);
            }

            WifiManager manager = ( WifiManager ) mContext.getSystemService( Context.WIFI_SERVICE );
            List<WifiConfiguration> configuredNetworks = manager.getConfiguredNetworks();
            for ( WifiConfiguration configuredNetwork : configuredNetworks ) {
                if ( config.SSID.equalsIgnoreCase( configuredNetwork.SSID ) ) {
                    manager.removeNetwork( configuredNetwork.networkId );
                }
            }

            int newNetworkId = manager.addNetwork( config );


            if ( newNetworkId != -1 ) {
                boolean newNetworkEnabledFlag = false;
                if ( manager.startScan() ) {
                    List<ScanResult> accessPoints = manager.getScanResults();
                    if ( accessPoints != null ) {
                        for ( ScanResult accessPoint : accessPoints ) {
                            //String newSSID = removeChar( config.SSID, '"' );
                            String newSSID = config.SSID.replace("\"", ""); // Strip out the double-quotes
                            if ( newSSID.equalsIgnoreCase( accessPoint.SSID ) ) {
                                newNetworkEnabledFlag = manager.enableNetwork( newNetworkId, true );
                            }
                        }
                    }
                }
                if ( !newNetworkEnabledFlag ) {
                    Log.v( TAG, "Wifi not enabled: id =  " + newNetworkId );
                    manager.enableNetwork( newNetworkId, false );
                }
                manager.reassociate();
                Log.v( TAG, "Saving wifi configuration: security = " + item.Security + " for " + item.SSID );
                boolean saveResult = manager.saveConfiguration();
                Log.v( TAG, "Manage.saveConfiguration result: " + saveResult );
                return saveResult;
            } else {
                Log.v( TAG, "newNetworkId was -1 for " + item.SSID);
                return false;
            }
        }

        WifiConfiguration generateSecurityLevelNoneConfig(String ssid) {
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = "\"" + ssid + "\"";
            config.allowedKeyManagement.set( WifiConfiguration.KeyMgmt.NONE );
            return config;
        }

        WifiConfiguration generateSecurityLevelWEPConfig(String ssid, String key) {
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = "\"" + ssid + "\"";
            config.wepKeys[ 0 ] = key;
            config.wepTxKeyIndex = 0;
            config.priority = 40;
            config.allowedKeyManagement.set( WifiConfiguration.KeyMgmt.NONE );
            config.allowedProtocols.set( WifiConfiguration.Protocol.RSN );
            config.allowedProtocols.set( WifiConfiguration.Protocol.WPA );
            config.allowedAuthAlgorithms.set( WifiConfiguration.AuthAlgorithm.OPEN );
            config.allowedAuthAlgorithms.set( WifiConfiguration.AuthAlgorithm.SHARED );
            config.allowedGroupCiphers.set( WifiConfiguration.GroupCipher.WEP40 );
            config.allowedGroupCiphers.set( WifiConfiguration.GroupCipher.WEP104 );
            config.allowedGroupCiphers.set( WifiConfiguration.GroupCipher.CCMP );
            config.allowedGroupCiphers.set( WifiConfiguration.GroupCipher.TKIP );
            config.allowedPairwiseCiphers.set( WifiConfiguration.PairwiseCipher.CCMP );
            config.allowedPairwiseCiphers.set( WifiConfiguration.PairwiseCipher.TKIP );
            return config;
        }

        WifiConfiguration generateSecurityLevelWPAConfig(String ssid, String key) {
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = "\"" + ssid + "\"";
            config.allowedKeyManagement.set( WifiConfiguration.KeyMgmt.WPA_PSK );
            config.allowedAuthAlgorithms.set( WifiConfiguration.AuthAlgorithm.OPEN );
            config.preSharedKey = "\"" + key + "\"";
            return config;
        }

    }
}
