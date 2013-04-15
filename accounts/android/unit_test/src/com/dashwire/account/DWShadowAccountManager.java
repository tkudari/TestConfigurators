package com.dashwire.account;

import android.accounts.*;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import com.dashwire.base.debug.DashLogger;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * User: tbostelmann
 * Date: 2/7/13
 */
@Implements(AccountManager.class)
public class DWShadowAccountManager {
    private static final String TAG = DWShadowAccountManager.class.getCanonicalName();

    private static HashMap<Context,AccountManager> instances = new HashMap<Context,AccountManager>();

    private Map<Account, Bundle> accounts = new HashMap<Account, Bundle>();

    public static final int SIMULATE_TYPE_SUCCESS = 1;
    public static final int SIMULATE_OPERATION_CANCELED_EXCEPTION = 2;
    public static final int SIMULATE_IO_EXCEPTION = 3;
    public static final int SIMULATE_AUTHENTICATOR_EXCEPTION = 4;
    public static final int SIMULATE_ERROR_CODE = 5;

    private int smSimulationType = SIMULATE_TYPE_SUCCESS;

//    public static void reset() {
//        instances = new HashMap<Context,AccountManager>();
//        smSimulationType = SIMULATE_TYPE_SUCCESS;
//    }
//

    private final Bundle generateMockBehavior(Long timeout, TimeUnit unit)
            throws OperationCanceledException, IOException, AuthenticatorException {
        Bundle bundle;
        switch(smSimulationType) {
            case SIMULATE_TYPE_SUCCESS:
                bundle = new Bundle();
                bundle.putString(AccountManager.KEY_ACCOUNT_NAME, "prolly need to set this to something");
                bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, "prolly need to set this to something");
                return bundle;
            case SIMULATE_OPERATION_CANCELED_EXCEPTION:
                throw new OperationCanceledException();
            case SIMULATE_IO_EXCEPTION:
                throw new IOException();
            case SIMULATE_AUTHENTICATOR_EXCEPTION:
                throw new AuthenticatorException();
            case SIMULATE_ERROR_CODE:
                bundle = new Bundle();
                bundle.putInt(AccountManager.KEY_ERROR_CODE, -1);
                return bundle;
            default:
                throw new RuntimeException("Unknown simulation setting");
        }
    }

    public void setSimulate(int simulationType) {
        smSimulationType = simulationType;
    }

    @Implementation
    public static AccountManager get(Context context) {
        if (!instances.containsKey(context)) {
            instances.put(context, Robolectric.newInstanceOf(AccountManager.class));
        }
        return instances.get(context);
    }

    @Implementation
    public Account[] getAccounts() {
        return accounts.keySet().toArray(new Account[accounts.keySet().size()]);
    }

    @Implementation
    public Account[] getAccountsByType(String type) {
        DashLogger.d(TAG, "getAccountsByType: " + type);
        List<Account> accountsByType = new ArrayList<Account>();

        for (Account a : accounts.keySet()) {
            if (type == null || type.equals(a.type)) {
                accountsByType.add(a);
            }
        }

        return accountsByType.toArray(new Account[accountsByType.size()]);
    }

    /**
     * Non-android accessor.  Allows the test case to populate the
     * list of active accounts.
     *
     * @param account
     */
    public void addAccount(Account account) {
        DashLogger.d(TAG, "Adding account: " + account.toString());
        accounts.put(account, null);
    }

    public Bundle getAccountBundle(Account account) {
        return accounts.get(account);
    }

    public AccountManagerFuture<Bundle> addAccount(final String accountType,
                                                   final String authTokenType, final String[] requiredFeatures,
                                                   final Bundle addAccountOptions,
                                                   final Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        DashLogger.d(TAG, "addAccount: " + accountType);
        if (accountType == null) throw new IllegalArgumentException("accountType is null");
        final Bundle optionsIn = new Bundle();
        if (addAccountOptions != null) {
            optionsIn.putAll(addAccountOptions);
        }

        return new AmsTask(activity, handler, callback) {
            public void doWork() throws RemoteException {
                DashLogger.d(TAG, "doWork()");
                if (smSimulationType == SIMULATE_TYPE_SUCCESS) {
                    String accountName = (String) optionsIn.get("username");
                    accounts.put(new Account(accountName, accountType), optionsIn);
                }
            }
        }.start();
    }

    private abstract class AmsTask extends FutureTask<Bundle> implements AccountManagerFuture<Bundle> {
        final Handler mHandler;
        final AccountManagerCallback<Bundle> mCallback;
        final Activity mActivity;
        public AmsTask(Activity activity, Handler handler, AccountManagerCallback<Bundle> callback) {
            super(new Callable<Bundle>() {
                public Bundle call() throws Exception {
                    throw new IllegalStateException("this should never be called");
                }
            });

            mHandler = handler;
            mCallback = callback;
            mActivity = activity;
        }

        public final AccountManagerFuture<Bundle> start() {
            try {
                doWork();
            } catch (RemoteException e) {
                setException(e);
            }
            return this;
        }

        protected void set(Bundle bundle) {
            // TODO: somehow a null is being set as the result of the Future. Log this
            // case to help debug where this is occurring. When this bug is fixed this
            // condition statement should be removed.
            if (bundle == null) {
                throw new RuntimeException("the bundle must not be null");
            }
            super.set(bundle);
        }

        public abstract void doWork() throws RemoteException;

        public Bundle getResult()
                throws OperationCanceledException, IOException, AuthenticatorException {
            return generateMockBehavior(null, null);
        }

        public Bundle getResult(long timeout, TimeUnit unit)
                throws OperationCanceledException, IOException, AuthenticatorException {
            return generateMockBehavior(timeout, unit);
        }

        protected void done() {
            if (mCallback != null) {
                //postToHandler(mHandler, mCallback, this);
            }
        }
    }
}
