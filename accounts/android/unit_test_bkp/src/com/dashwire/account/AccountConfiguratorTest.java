package com.dashwire.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.base.device.DeviceInfo;
import com.dashwire.configurator.ConfigurationRequestReceiver;
import com.dashwire.configurator.ConfiguratorBaseIntentService;
import com.xtremelabs.robolectric.Robolectric;
import junit.framework.Assert;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * User: tbostelmann
 * Date: 2/1/13
 */
@RunWith(CustomAccountTestRunner.class)
public class AccountConfiguratorTest {

    AccountConfigurator mAccountConfiguratorService;
    DWShadowAccountManager mDwShadowAccountManager;
    DWShadowContentResolver mDwShadowContentResolver;
    AccountManagerAdapter mAccountManagerAdapter;

    String mLogin = "foobar@gmail.com";
    String mPassword = "foobar_password";
    boolean mSyncEmail = true;
    boolean mSyncContacts = true;
    boolean mSyncCalendar = true;
    boolean mAutoSync = true;


    @Before
    public void setUp() throws Exception
    {
        DashLogger.setDebugMode(true);
        DeviceInfo.isAutomatedTestEnvironment = true;
        mAccountConfiguratorService = new AccountConfigurator();
        AccountManager accountManager = AccountManager.get( Robolectric.application );
        mDwShadowAccountManager = Robolectric.shadowOf_(accountManager);
        ContentResolver contentResolver = Robolectric.application.getContentResolver();
        mDwShadowContentResolver = Robolectric.shadowOf_(contentResolver);
    }

    @Test
    public void testValidAccountSimulateOperationCanceledException() throws JSONException {
        initValues();
        Assert.assertFalse(AccountManagerAdapter.accountExists(Robolectric.application, mLogin));

        mDwShadowAccountManager.setSimulate(
                DWShadowAccountManager.SIMULATE_OPERATION_CANCELED_EXCEPTION);
        Intent featureIntent = generateFeatureIntent(mLogin, mPassword, mSyncEmail, mSyncContacts, mSyncCalendar);
        mAccountConfiguratorService.onHandleIntent(featureIntent);

        assertAccountNotCreated();
    }

    @Test
    public void testValidAccountSimulateAuthenticationException() throws JSONException {
        initValues();
        Assert.assertFalse(AccountManagerAdapter.accountExists(Robolectric.application, mLogin));

        mDwShadowAccountManager.setSimulate(
                DWShadowAccountManager.SIMULATE_AUTHENTICATOR_EXCEPTION);
        Intent featureIntent = generateFeatureIntent(mLogin, mPassword, mSyncEmail, mSyncContacts, mSyncCalendar);
        mAccountConfiguratorService.onHandleIntent(featureIntent);

        assertAccountNotCreated();
    }

    @Test
    public void testValidAccountSimulateIOException() throws JSONException {
        initValues();
        Assert.assertFalse(AccountManagerAdapter.accountExists(Robolectric.application, mLogin));

        mDwShadowAccountManager.setSimulate(
                DWShadowAccountManager.SIMULATE_IO_EXCEPTION);
        Intent featureIntent = generateFeatureIntent(mLogin, mPassword, mSyncEmail, mSyncContacts, mSyncCalendar);
        mAccountConfiguratorService.onHandleIntent(featureIntent);

        assertAccountNotCreated();
    }

    @Test
    public void testValidAccountSimulateErrorCode() throws JSONException {
        initValues();
        Assert.assertFalse(AccountManagerAdapter.accountExists(Robolectric.application, mLogin));

        mDwShadowAccountManager.setSimulate(
                DWShadowAccountManager.SIMULATE_ERROR_CODE);
        Intent featureIntent = generateFeatureIntent(mLogin, mPassword, mSyncEmail, mSyncContacts, mSyncCalendar);
        mAccountConfiguratorService.onHandleIntent(featureIntent);

        assertAccountNotCreated();
    }

    @Test
    public void testValidAccountAndSync() throws JSONException {
        Assert.assertFalse(AccountManagerAdapter.accountExists(Robolectric.application, mLogin));

        Intent featureIntent = generateFeatureIntent(mLogin, mPassword, mSyncEmail, mSyncContacts, mSyncCalendar);
        mAccountConfiguratorService.onHandleIntent(featureIntent);

        assertBundleAndSyncSettings(mLogin, mPassword, mSyncCalendar, mSyncContacts, mAutoSync);
    }

    private void assertAccountNotCreated() {
        Account account = new Account(mLogin, AccountManagerAdapter.ACCOUNT_TYPE_GMAIL);
        Bundle bundle = mDwShadowAccountManager.getAccountBundle(account);
        Assert.assertNull("Account should not have been created", bundle);
    }

    private void initValues() {
        mLogin = "foobar@gmail.com";
        mPassword = "foobar_password";
        mSyncEmail = true;
        mSyncContacts = true;
        mSyncCalendar = true;
        mAutoSync = true; // Default value - does not appear to be set-able.
    }

    private void assertBundleAndSyncSettings(String login, String password, boolean syncCalendar, boolean syncContacts, boolean autoSync) {
        Account account = new Account(login, AccountManagerAdapter.ACCOUNT_TYPE_GMAIL);
        Bundle bundle = mDwShadowAccountManager.getAccountBundle(account);
        Assert.assertNotNull("No bundle was created", bundle);
        Assert.assertEquals(
                "Invalid value for: " + AccountManagerAdapter.BUNDLE_PROPERTY_USERNAME,
                login, bundle.getString(AccountManagerAdapter.BUNDLE_PROPERTY_USERNAME));
        Assert.assertEquals(
                "Invalid value for: " + AccountManagerAdapter.BUNDLE_PROPERTY_PASSWORD,
                password, bundle.getString(AccountManagerAdapter.BUNDLE_PROPERTY_PASSWORD));

        ContentResolverAdapterTest.assertSyncSettings(mDwShadowContentResolver, ContentResolverAdapter.AUTHORITY_ANDROID_CALENDAR, account, syncCalendar);
        ContentResolverAdapterTest.assertSyncSettings(mDwShadowContentResolver, ContentResolverAdapter.AUTHORITY_ANDROID_CONTACTS, account, syncContacts);
        ContentResolverAdapterTest.assertSyncSettings(mDwShadowContentResolver, ContentResolverAdapter.AUTHORITY_GMAIL_AUTO_SYNC, account, autoSync);
    }

    private Intent generateFeatureIntent(String login, String password, boolean syncEmail, boolean syncContacts, boolean syncCalendar) throws JSONException {
        JSONArray accountJsonArray = generateAccountsJsonArray(login, password, syncEmail, syncContacts, syncCalendar);
        String featureName = "Account";
        Intent configIntent = ConfiguratorBaseIntentService.generateIntent(featureName, accountJsonArray);
        return ConfigurationRequestReceiver.generateFeatureIntent(featureName, configIntent);
    }

	public static JSONArray generateAccountsJsonArray(String login, String password, boolean syncEmail, boolean syncContacts, boolean syncCalendar)
			throws JSONException {
		return new JSONArray("[" +
				"{\"login\":\"" + login + "\", " +
				"\"password\":\"" + password + "\", " +
				"\"service\":\"Gmail\"," +
				"\"sync_email\": \"" + syncEmail + "\", " +
				"\"sync_contacts\": \"" + syncContacts + "\", " +
				"\"sync_calendar\": \"" + syncCalendar + "\"}" +
				"]");
	}
}
