package com.dashwire.account;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import com.xtremelabs.robolectric.Robolectric;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * User: tbostelmann
 * Date: 2/11/13
 */
@RunWith(CustomAccountTestRunner.class)
public class ContentResolverAdapterTest {

    private DWShadowContentResolver mDwShadowContentResolver;

    @Before
    public void setUp() throws Exception
    {
        Activity activity = new Activity();
        ContentResolver contentResolver = activity.getContentResolver();
        mDwShadowContentResolver = Robolectric.shadowOf_(contentResolver);
    }

    @Test
    public void testSetSyncSettings() {
        mDwShadowContentResolver.setIsWorking(true);
        String login = "foobar@gmail.com";
        boolean syncCalendar = true;
        boolean syncContacts = false;
        boolean autoSync = true;
        Assert.assertTrue(
                "This should pass",
                ContentResolverAdapter.syncAccount(login, syncCalendar, syncContacts, autoSync));
        Account account = new Account(login, AccountManagerAdapter.ACCOUNT_TYPE_GMAIL);
        assertSyncSettings(mDwShadowContentResolver, ContentResolverAdapter.AUTHORITY_ANDROID_CALENDAR, account, syncCalendar);
        assertSyncSettings(mDwShadowContentResolver, ContentResolverAdapter.AUTHORITY_ANDROID_CONTACTS, account, syncContacts);
        assertSyncSettings(mDwShadowContentResolver, ContentResolverAdapter.AUTHORITY_GMAIL_AUTO_SYNC, account, autoSync);
    }

    @Test
    public void testSetSyncSettingsFailed() {
        mDwShadowContentResolver.setIsWorking(false);
        String login = "foobar@gmail.com";
        boolean syncCalendar = true;
        boolean syncContacts = true;
        boolean autoSync = true;
        Assert.assertFalse(
                "This should fail",
                ContentResolverAdapter.syncAccount(login, syncCalendar, syncContacts, autoSync));
    }

    public static void assertSyncSettings(DWShadowContentResolver shadowContentResolver, String authority, Account account, boolean value) {
        Assert.assertEquals(1, shadowContentResolver.getIsSyncable(account, authority));
        Assert.assertEquals(value, shadowContentResolver.getSyncAutomatically(account, authority));
        Assert.assertNotNull(
                shadowContentResolver.getRequestedSync(account, authority));
    }
}
