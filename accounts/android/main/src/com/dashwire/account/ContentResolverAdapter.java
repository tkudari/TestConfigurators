package com.dashwire.account;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;
import com.dashwire.base.debug.DashLogger;

/**
 * User: tbostelmann
 * Date: 2/9/13
 */
public class ContentResolverAdapter {
    private static final String TAG = ContentResolverAdapter.class.getCanonicalName();

    public static final String AUTHORITY_ANDROID_CALENDAR = "com.android.calendar";
    public static final String AUTHORITY_ANDROID_CONTACTS = "com.android.contacts";
    public static final String AUTHORITY_GMAIL_AUTO_SYNC = "gmail-ls";

    public static Boolean syncAccount( String email, boolean syncCalendar, boolean syncContacts, boolean autoSync ) {
        Account acc = new Account( email, AccountManagerAdapter.ACCOUNT_TYPE_GMAIL);

        ContentResolver.setIsSyncable(acc, AUTHORITY_ANDROID_CALENDAR, 1);
        ContentResolver.setSyncAutomatically( acc, AUTHORITY_ANDROID_CALENDAR, syncCalendar );
        ContentResolver.requestSync( acc, AUTHORITY_ANDROID_CALENDAR, new Bundle() );

        ContentResolver.setIsSyncable( acc, AUTHORITY_ANDROID_CONTACTS, 1 );
        ContentResolver.setSyncAutomatically( acc, AUTHORITY_ANDROID_CONTACTS, syncContacts );
        ContentResolver.requestSync( acc, AUTHORITY_ANDROID_CONTACTS, new Bundle() );

        ContentResolver.setIsSyncable( acc, AUTHORITY_GMAIL_AUTO_SYNC, 1 );
        ContentResolver.setSyncAutomatically( acc, AUTHORITY_GMAIL_AUTO_SYNC, autoSync );
        ContentResolver.requestSync( acc, AUTHORITY_GMAIL_AUTO_SYNC, new Bundle() );

        return verifySyncSettings(email, syncCalendar, syncContacts, autoSync);
    }

    public static boolean verifySyncSettings(String email, boolean syncCalendar, boolean syncContacts, boolean autoSync) {
        Account acc = new Account( email, AccountManagerAdapter.ACCOUNT_TYPE_GMAIL);

        if(ContentResolver.getIsSyncable( acc, AUTHORITY_ANDROID_CALENDAR) != 1) {
            DashLogger.e(TAG, "Gmail calendar syncable option not set correctly");
            return false;
        }
        if(ContentResolver.getSyncAutomatically( acc, AUTHORITY_ANDROID_CALENDAR) != syncCalendar) {
            DashLogger.e( TAG, "Gmail calendar auto sync option not set correctly");
            return false;
        }
        if(ContentResolver.getIsSyncable( acc, AUTHORITY_ANDROID_CONTACTS) != 1) {
            DashLogger.e( TAG, "Gmail contacts syncable option not set correctly");
            return false;
        }
        if(ContentResolver.getSyncAutomatically( acc, AUTHORITY_ANDROID_CONTACTS) != syncContacts) {
            DashLogger.e( TAG, "Gmail contacts auto sync option not set correctly");
            return false;
        }
        if(ContentResolver.getIsSyncable( acc, AUTHORITY_GMAIL_AUTO_SYNC) != 1) {
            DashLogger.d( TAG, "Gmail syncable option not set correctly");
            return false;
        }
        if(ContentResolver.getSyncAutomatically( acc, AUTHORITY_GMAIL_AUTO_SYNC) != autoSync) {
            DashLogger.d( TAG, "Gmail auto sync option not set correctly");
            return false;
        }

        return true;
    }
}
