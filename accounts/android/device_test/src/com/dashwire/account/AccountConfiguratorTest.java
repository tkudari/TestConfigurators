package com.dashwire.account;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.AndroidTestCase;
import android.util.Log;
import com.dashwire.configurator.ConfiguratorConstants;
import com.dashwire.testing.helper.utils.TestingHelperUtils;
import junit.framework.Assert;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * User: tbostelmann
 * Date: 2/15/13
 */
public class AccountConfiguratorTest extends AndroidTestCase {
	private static final String TAG = AccountConfiguratorTest.class.getCanonicalName();
	private static final String INTENT_ACTION = "com.dashwire.configure.intent.Account";
	private static final int DEFAULT_TIMEOUT = 20;

	public static class AccountBroadcastReceiver extends BroadcastReceiver {
		public boolean completed = false;

		@Override
		public void onReceive(Context context, Intent intent) {
			completed = true;
		}
	};

	public void testSendIntentThatReturnsSuccess() throws JSONException {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConfiguratorConstants.INTENT_FEATURE_CONFIGURATION_RESULT);
		AccountBroadcastReceiver completeBroadcastReceiver =
				new AccountBroadcastReceiver();
		getContext().registerReceiver(completeBroadcastReceiver, filter);

		String login = "dashconfig2@gmail.com";
		String password = "abababab";
		boolean syncCalendar = true;
		boolean syncContacts = true;
		boolean autoSync = true;
		if (AccountManagerAdapter.accountExists(getContext(), login))
			AccountManagerAdapter.removeGoogleAccount(getContext(), login);

		JSONArray jsonArray = TestingHelperUtils.generateAccountsJsonArray(login, password, syncCalendar, syncContacts, autoSync);
		Intent intent = TestingHelperUtils.generateAccountsIntent(jsonArray);
		getContext().startService(intent);

		try {
			int i = 0;
			while (!completeBroadcastReceiver.completed) {
				if (i <= DEFAULT_TIMEOUT) {
					Thread.sleep(1000);
					i++;
				} else {
					Assert.fail("Test failed due to timeout");
				}
			}
		} catch (InterruptedException e) {
			Log.e(TAG, "Test was interrupted", e);
			Assert.fail("Test was interrupted");
		} catch (Exception e) {
			Log.e(TAG, "Test failed", e);
			Assert.fail("Test failed: " + e.getMessage());
		}

		if (AccountManagerAdapter.accountExists(getContext(), login))
			AccountManagerAdapter.removeGoogleAccount(getContext(), login);
	}
}
