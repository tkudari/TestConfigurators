package com.dashwire.account;

import android.test.AndroidTestCase;
import junit.framework.Assert;

/**
 * User: tbostelmann
 * Date: 2/15/13
 */
public class ContentResolverAdapterTest extends AndroidTestCase {

    public void testSynchSettings() {
        String login = "dashconfig2@gmail.com";
        String password = "abababab";
        boolean syncCalendar = true;
        boolean syncContacts = true;
        boolean autoSync = true;

        if (AccountManagerAdapter.accountExists(getContext(), login))
            AccountManagerAdapter.removeGoogleAccount(getContext(), login);

        Assert.assertTrue("Failed to create google account",
                AccountManagerAdapter.createGoogleAccount(getContext(), login, password));

        Assert.assertTrue("Failed to synch account",
                ContentResolverAdapter.syncAccount(login, syncCalendar, syncContacts, autoSync));

		if (AccountManagerAdapter.accountExists(getContext(), login))
			AccountManagerAdapter.removeGoogleAccount(getContext(), login);
	}
}
