package com.dashwire.account;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;
import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

import java.util.HashMap;
import java.util.Map;

/**
 * User: tbostelmann
 * Date: 2/8/13
 */
@Implements(ContentResolver.class)
public class DWShadowContentResolver {

    // Set this when you want general failure of setting state
    private static Boolean smIsWorking = true;

    static Map<Account, Map> smIsSynchable = new HashMap<Account, Map>();
    static Map<Account, Map> smSyncAutomatically = new HashMap<Account, Map>();
    static Map<Account, Map> smRequestSyncSettings = new HashMap<Account, Map>();

    public static void setSimulateIsWorking(Boolean value) {
        smIsWorking = value;
    }

    public static Map<String, Bundle> getRequestSyncSettings(Account account) {
        if (smRequestSyncSettings.containsKey(account)) {
            return smRequestSyncSettings.get(account);
        }
        else {
            Map<String, Bundle> requestSettings = new HashMap<String, Bundle>();
            smRequestSyncSettings.put(account, requestSettings);
            return requestSettings;
        }
    }

    public static Map<String, Integer> getIsSynchableSettings(Account account) {
        if (smIsSynchable.containsKey(account)) {
            return smIsSynchable.get(account);
        }
        else {
            Map<String, Integer> settings =
                    new HashMap<String, Integer>();
            smIsSynchable.put(account, settings);
            return settings;
        }
    }

    public static Map<String, Boolean> getSyncAutomaticallySettings(Account account) {
        if (smSyncAutomatically.containsKey(account)) {
            return smSyncAutomatically.get(account);
        }
        else {
            Map<String, Boolean> settings =
                    new HashMap<String, Boolean>();
            smSyncAutomatically.put(account, settings);
            return settings;
        }
    }

    public static void setIsWorking(Boolean isWorking) {
        smIsWorking = isWorking;
    }

    @Implementation
    public static void setIsSyncable(Account account, String authority, int syncable) {
        if (smIsWorking) {
            Map<String, Integer> settings = getIsSynchableSettings(account);
            settings.put(authority, syncable);
        }
    }

    @Implementation
    public static int getIsSyncable(Account account, String authority) {
        Map<String, Integer> settings = getIsSynchableSettings(account);
        if (settings != null && settings.get(authority) != null)
            return settings.get(authority);
        else
            return -1;
    }

    @Implementation
    public static void setSyncAutomatically(Account account, String authority, boolean sync) {
        if (smIsWorking) {
            Map<String, Boolean> settings = getSyncAutomaticallySettings(account);
            settings.put(authority, sync);
        }
    }

    @Implementation
    public static boolean getSyncAutomatically(Account account, String authority) {
        Map<String, Boolean> settings = getSyncAutomaticallySettings(account);
        if (settings != null)
            return settings.get(authority);
        else
            throw new NullPointerException("Account does not have autoSync value set");
    }

    @Implementation
    public static void requestSync(Account account, String authority, Bundle bundle) {
        Map<String, Bundle> request = getRequestSyncSettings(account);
        request.put(authority, bundle);
    }

    public static Bundle getRequestedSync(Account account, String authority) {
        Map<String, Bundle> request = getRequestSyncSettings(account);
        if (request != null)
            return request.get(authority);
        else
            return null;
    }
}
