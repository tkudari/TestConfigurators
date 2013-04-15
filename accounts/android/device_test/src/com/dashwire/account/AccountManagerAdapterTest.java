package com.dashwire.account;

import android.test.AndroidTestCase;
import junit.framework.Assert;

/**
 * User: tbostelmann
 * Date: 2/15/13
 */
public class AccountManagerAdapterTest extends AndroidTestCase {

    public void testValidGmailAccount() {
        String login = "dashconfig2@gmail.com";
        String password = "abababab";

        if (AccountManagerAdapter.accountExists(getContext(), login))
            AccountManagerAdapter.removeGoogleAccount(getContext(), login);

        Assert.assertTrue("Failed to create google account",
                AccountManagerAdapter.createGoogleAccount(getContext(), login, password));
        Assert.assertTrue("Failed to find previously created gmail account",
                AccountManagerAdapter.accountExists(getContext(), login));

        if (AccountManagerAdapter.accountExists(getContext(), login))
            AccountManagerAdapter.removeGoogleAccount(getContext(), login);
    }
}
