package com.dashwire.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import com.xtremelabs.robolectric.Robolectric;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * User: tbostelmann
 * Date: 2/10/13
 */
@RunWith(CustomAccountTestRunner.class)
public class AccountManagerAdapterTest {
    private DWShadowAccountManager mDwShadowAccountManager;

    @Before
    public void setUp() throws Exception
    {
        AccountManager accountManager = AccountManager.get( Robolectric.application );
        mDwShadowAccountManager = Robolectric.shadowOf_(accountManager);
    }

    @Test
    public void testAccountExists() {
        Assert.assertFalse(AccountManagerAdapter.accountExists(Robolectric.application, "foo@google.com"));
    }

    @Test
    public void testSuccessfulGmailSetup() {
        String login = "foobar@gmail.com";
        String password = "foobar_password";
        Assert.assertFalse(AccountManagerAdapter.accountExists(Robolectric.application, login));
        mDwShadowAccountManager.setSimulate(DWShadowAccountManager.SIMULATE_TYPE_SUCCESS);
        Assert.assertTrue(
                "Provisioning a google account should not fail",
                AccountManagerAdapter.createGoogleAccount(Robolectric.application, login, password));
        Account account = new Account(login, AccountManagerAdapter.ACCOUNT_TYPE_GMAIL);
        Bundle bundle = mDwShadowAccountManager.getAccountBundle(account);
        Assert.assertEquals(
                "Invalid value for: " + AccountManagerAdapter.BUNDLE_PROPERTY_USERNAME,
                login, bundle.getString(AccountManagerAdapter.BUNDLE_PROPERTY_USERNAME));
        Assert.assertEquals(
                "Invalid value for: " + AccountManagerAdapter.BUNDLE_PROPERTY_PASSWORD,
                password, bundle.getString(AccountManagerAdapter.BUNDLE_PROPERTY_PASSWORD));
    }

    @Test
    public void testSimulateOperationCanceled() {
        String login = "foobar@gmail.com";
        String password = "foobar_password";
        Assert.assertFalse(AccountManagerAdapter.accountExists(Robolectric.application, login));
        mDwShadowAccountManager.setSimulate(
                DWShadowAccountManager.SIMULATE_OPERATION_CANCELED_EXCEPTION);
        Assert.assertFalse(
                "Provisioning a google account should have failed",
                AccountManagerAdapter.createGoogleAccount(Robolectric.application, login, password));
        Account account = new Account(login, AccountManagerAdapter.ACCOUNT_TYPE_GMAIL);
        // Arguably, this next test is really for the shadow
        Assert.assertNull("The account should not have been setup",
                mDwShadowAccountManager.getAccountBundle(account));
    }

    @Test
    public void testSimulateIOException() {
        String login = "foobar@gmail.com";
        String password = "foobar_password";
        Assert.assertFalse(AccountManagerAdapter.accountExists(Robolectric.application, login));
        mDwShadowAccountManager.setSimulate(
                DWShadowAccountManager.SIMULATE_IO_EXCEPTION);
        Assert.assertFalse(
                "Provisioning a google account should have failed",
                AccountManagerAdapter.createGoogleAccount(Robolectric.application, login, password));
        Account account = new Account(login, AccountManagerAdapter.ACCOUNT_TYPE_GMAIL);
        // Arguably, this next test is really for the shadow
        Assert.assertNull("The account should not have been setup",
                mDwShadowAccountManager.getAccountBundle(account));
    }

    @Test
    public void testSimulateAuthenticationException() {
        String login = "foobar@gmail.com";
        String password = "foobar_password";
        Assert.assertFalse(AccountManagerAdapter.accountExists(Robolectric.application, login));
        mDwShadowAccountManager.setSimulate(
                DWShadowAccountManager.SIMULATE_AUTHENTICATOR_EXCEPTION);
        Assert.assertFalse(
                "Provisioning a google account should have failed",
                AccountManagerAdapter.createGoogleAccount(Robolectric.application, login, password));
        Account account = new Account(login, AccountManagerAdapter.ACCOUNT_TYPE_GMAIL);
        // Arguably, this next test is really for the shadow
        Assert.assertNull("The account should not have been setup",
                mDwShadowAccountManager.getAccountBundle(account));
    }

    @Test
    public void testSimulateErrorCode() {
        String login = "foobar@gmail.com";
        String password = "foobar_password";
        Assert.assertFalse(AccountManagerAdapter.accountExists(Robolectric.application, login));
        mDwShadowAccountManager.setSimulate(
                DWShadowAccountManager.SIMULATE_ERROR_CODE);
        Assert.assertFalse(
                "Provisioning a google account should have failed",
                AccountManagerAdapter.createGoogleAccount(Robolectric.application, login, password));
        Account account = new Account(login, AccountManagerAdapter.ACCOUNT_TYPE_GMAIL);
        // Arguably, this next test is really for the shadow
        Assert.assertNull("The account should not have been setup",
                mDwShadowAccountManager.getAccountBundle(account));
    }
}
