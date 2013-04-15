package com.dashwire.account;

import android.accounts.*;
import android.content.Context;
import android.os.Bundle;
import com.dashwire.base.debug.DashLogger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * User: tbostelmann
 * Date: 2/1/13
 */

// REVIEW: Generally speaking when I see a "utils" class it makes me think of static methods with no internal state
public class AccountManagerAdapter {
    private static final String TAG = AccountManagerAdapter.class.getCanonicalName();

    public static final String ACCOUNT_TYPE_GMAIL = "com.google";
    public static final String BUNDLE_PROPERTY_USERNAME = "username";
    public static final String BUNDLE_PROPERTY_PASSWORD = "password";

    private static final int DEFAULT_TIMEOUT = 10;

    public static boolean createGoogleAccount(Context context, String email, String password) {
        return createGoogleAccount(context, email, password, DEFAULT_TIMEOUT);
    }

    public static boolean createGoogleAccount(Context context, String email, String password, int timeout) {
		if (!accountExists(context, email)) {
			DashLogger.d(TAG, "Provisioning Gmail Account");
			Bundle addAccountOptions = new Bundle();
			addAccountOptions.putString(BUNDLE_PROPERTY_USERNAME, email);
			addAccountOptions.putString(BUNDLE_PROPERTY_PASSWORD, password);

			AccountManager accountManager = AccountManager.get(context);
			AccountManagerFuture<Bundle> future =
					accountManager.addAccount(ACCOUNT_TYPE_GMAIL, null, null, addAccountOptions, null, null, null );
			Bundle resultBundle = null;
			try {
				resultBundle = future.getResult( timeout, TimeUnit.SECONDS ); // REVIEW: Does this block?
				if(resultBundle.containsKey(AccountManager.KEY_ERROR_CODE)) {
					DashLogger.e( TAG, "Error Provsioning Gmail Account: " + resultBundle.getInt(AccountManager.KEY_ERROR_CODE));
					return false;
				} else if(resultBundle.containsKey(AccountManager.KEY_ACCOUNT_NAME) &&
						resultBundle.containsKey(AccountManager.KEY_ACCOUNT_TYPE)) {
					DashLogger.i( TAG, "Succeeded provisioning Gmail Account");
					return true;
				} else {
					DashLogger.w( TAG, "Potential issue provisioning Gmail account: bundle does not contain expected values");
					return true;
				}
			} catch ( IOException ex ) {
				DashLogger.e( TAG, "IOException provisioning account.", ex);
				return false;
			} catch ( OperationCanceledException ex ) {
				DashLogger.e( TAG, "OperationCanceledException provisioning account.", ex);
				return false;
			} catch ( AuthenticatorException ex ) {
				DashLogger.e( TAG, "AuthenticatorException provisioning account.", ex);
				return false;
			}
		} else {
			DashLogger.i(TAG, "Google account already exists.  Skipping creation.");
			return true;
		}
    }

    public static boolean accountExists(Context context, String login) {
        if ( login != null ) {
            AccountManager accountManager = AccountManager.get( context );
            Account[] accounts = accountManager.getAccountsByType( null );
            for ( int index = 0; index < accounts.length; index++ ) {
                if ( login.equals( accounts[ index ].name ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean removeGoogleAccount(Context context, String login) {
        return removeGoogleAccount(context, login, DEFAULT_TIMEOUT);
    }

    public static boolean removeGoogleAccount(Context context, String login, int timeout) {
		if (accountExists(context, login)) {
			DashLogger.d(TAG, "Removing gmail account");
			Account gmailAccount = new Account(login, "com.google");
			AccountManager accountManager = AccountManager.get(context);
			AccountManagerFuture<Boolean> remoteAccountJob =
					accountManager.removeAccount(gmailAccount, null, null);
			try {
				remoteAccountJob.getResult(timeout, TimeUnit.SECONDS);
				return true;
			} catch (OperationCanceledException e) {
				DashLogger.e(TAG, "Remove gmail account was canceled", e);
			} catch (IOException e) {
				DashLogger.e(TAG, "IOException removing gmail account", e);
			} catch (AuthenticatorException e) {
				DashLogger.e(TAG, "Authentication exception removing gmail account", e);
			}
			return false;
		} else {
			DashLogger.i(TAG, "Account does not exist.  Skipping removal.");
			return false;
		}
    }
}
