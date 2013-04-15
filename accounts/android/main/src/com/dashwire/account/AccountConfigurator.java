package com.dashwire.account;

import com.dashwire.base.debug.DashLogger;
import com.dashwire.configurator.ConfiguratorBaseIntentService;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: tbostelmann
 * Date: 1/30/13
 */
public class AccountConfigurator extends ConfiguratorBaseIntentService {

    private static final String TAG = AccountConfigurator.class.getCanonicalName();

    private static String[] REQUIRED_PARAMETERS = {"login", "password", "sync_calendar", "sync_contacts"};

    private static final String MESSAGE = "GoogleLogin JSON does not contain {0} entry";

    @Override
    protected void onConfigFeature() {
        setupAccounts();
    }

    void setupAccounts() {
		boolean hasFailure = false;
        for (int i=0; i<mFeatureDataJSONArray.length(); i++) {
            try {
                JSONObject account = mFeatureDataJSONArray.getJSONObject(i);
                if(!isValidJson(account)) {
                    DashLogger.e(TAG, "Gmail account is invalid.  Skipping...");
                    hasFailure = true;
                    continue;
                }

                String login = account.getString("login");
                if (AccountManagerAdapter.accountExists(getApplicationContext(), login)) {
                    DashLogger.i(TAG, "Google account already exists.  Skipping...");
                }
                else {
                    String password = account.getString( "password" );
                    boolean syncCalendar = account.getBoolean( "sync_calendar" );
                    boolean syncContacts = account.getBoolean( "sync_contacts" );
                    boolean autoSync = true; // REVIEW: Out of curiosity, why is this true if we don't specify it?
                    if (account.has("auto_sync")) { 
                        autoSync = account.getBoolean("auto_sync");
                    }
                    boolean success = AccountManagerAdapter.createGoogleAccount(getApplicationContext(), login, password);
                    if (success) {
                        ContentResolverAdapter.syncAccount(login, syncCalendar, syncContacts, autoSync);
                    }
                    else {
                        hasFailure = true;
                    }
                }
            } catch (JSONException e) {
                DashLogger.e(TAG, "Error handling gmail account", e);
                hasFailure = true;
            } catch (Exception e) {
                DashLogger.e(TAG, "Unknown error handling gmail account", e);
                hasFailure = true;
            }
        }

        if (hasFailure) { // REVIEW: We do not currently provide the information on failure, but error messages should be as specfic as possible
            // TODO: refactor so that specific error message is relayed
            onFailure("General error in Gmail configuration");
        }
        else {
            onSuccess();
        }
    }

    private boolean isValidJson(JSONObject account) {
        boolean isValid = true;
        for (String param: REQUIRED_PARAMETERS) {
            if (!account.has(param)) {
                isValid = false;
                DashLogger.e(TAG, "Gmail account config is missing value for: " + param);
            }
        }
        return isValid;
    }
}
