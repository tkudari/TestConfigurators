package com.dashwire.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Pair;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;

@SuppressWarnings({"UnusedDeclaration"})
@Implements(WifiManager.class)
public class DWShadowWifiManager {
    private boolean accessWifiStatePermission = true;
    private boolean wifiEnabled = true;
    private WifiInfo wifiInfo;
    private List<ScanResult> scanResults;
    private Map<Integer, WifiConfiguration> networkIdToConfiguredNetworks = new LinkedHashMap<Integer, WifiConfiguration>();
    public boolean wasSaved = true;
    private Pair<Integer, Boolean> lastEnabledNetwork;
    private int wifiState = WifiManager.WIFI_STATE_UNKNOWN;

    private boolean nextNetworkAddFails = false;

    @Implementation
    public boolean setWifiEnabled(boolean wifiEnabled) {
        checkAccessWifiStatePermission();
        this.wifiEnabled = wifiEnabled;
        return true;
    }

    @Implementation
    public boolean isWifiEnabled() {
        checkAccessWifiStatePermission();
        return wifiEnabled;
    }

    @Implementation
    public WifiInfo getConnectionInfo() {
        checkAccessWifiStatePermission();
        if (wifiInfo == null) {
            wifiInfo = Robolectric.newInstanceOf(WifiInfo.class);
        }
        return wifiInfo;
    }

    @Implementation
    public List<ScanResult> getScanResults() {
        return scanResults;
    }

    @Implementation
    public List<WifiConfiguration> getConfiguredNetworks() {
        final ArrayList<WifiConfiguration> wifiConfigurations = new ArrayList<WifiConfiguration>();
        for (WifiConfiguration wifiConfiguration : networkIdToConfiguredNetworks.values()) {
            wifiConfigurations.add(wifiConfiguration);
        }
        return wifiConfigurations;
    }

    @Implementation
    public int addNetwork(WifiConfiguration config) {
        if (nextNetworkAddFails) {
            nextNetworkAddFails = false;
            return -1;
        } else {
            int networkId = networkIdToConfiguredNetworks.size();
            config.networkId = -1;
            networkIdToConfiguredNetworks.put(networkId, makeCopy(config, networkId));
            return networkId;
        }
    }

    private WifiConfiguration makeCopy(WifiConfiguration config, int networkId) {
        WifiConfiguration copy = shadowOf(config).copy();
        copy.networkId = networkId;
        return copy;
    }

    public void setNextNetworkAddToFail() {
        nextNetworkAddFails = true;
    }


    @Implementation
    public int updateNetwork(WifiConfiguration config) {
        if (config == null || config.networkId < 0) {
            return -1;
        }
        networkIdToConfiguredNetworks.put(config.networkId, makeCopy(config, config.networkId));
        return config.networkId;
    }

    @Implementation
    public boolean saveConfiguration() {
        return wasSaved;
    }

    public void setSaveConfigurationReturnValue(boolean value) {
        wasSaved = value;
    }

    @Implementation
    public boolean enableNetwork(int netId, boolean disableOthers) {
        lastEnabledNetwork = new Pair<Integer, Boolean>(netId, disableOthers);
        return true;
    }

    public void setAccessWifiStatePermission(boolean accessWifiStatePermission) {
        this.accessWifiStatePermission = accessWifiStatePermission;
    }

    private void checkAccessWifiStatePermission() {
        if (!accessWifiStatePermission) {
            throw new SecurityException();
        }
    }

    public void setScanResults(List<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }

    public Pair<Integer, Boolean> getLastEnabledNetwork() {
        return lastEnabledNetwork;
    }

    @Implementation
    public int getWifiState() {
        return wifiState;

    }

    public void setWifiStateUnknown() { wifiState = WifiManager.WIFI_STATE_UNKNOWN; }
    public void setWifiStateDisabled() { wifiState = WifiManager.WIFI_STATE_DISABLED; }
    public void setWifiStateDisabling() { wifiState = WifiManager.WIFI_STATE_DISABLING; }
    public void setWifiStateEnabling() { wifiState = WifiManager.WIFI_STATE_ENABLING; }
    public void setWifiStateEnabled() { wifiState = WifiManager.WIFI_STATE_ENABLED; }

    @Implementation
    public boolean removeNetwork(int networkId) {
        WifiConfiguration removed = networkIdToConfiguredNetworks.remove(networkId);
        if (removed != null) {
            return true;
        } else {
            return false;
        }
    }

    @Implementation
    public boolean startScan() {
        return true;
    }

}
