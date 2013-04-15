package com.dashwire.contacts;

import java.lang.Exception;
import java.util.ArrayList;


import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.*;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;


@RunWith(RobolectricTestRunner.class)
public class ContactsConfiguratorTest {

	Context mContext;

	@Before
	public void doBefore() {
		mContext = Robolectric.getShadowApplication().getApplicationContext();
	}

	@Test
	public void testSetConfigDetails() throws Exception {

		ContactsConfigurator svc = new ContactsConfigurator();

		ContactsConfigurator.ContactConfigurator configurator = new ContactsConfigurator.ContactConfigurator(svc);
		JSONArray array = new JSONArray("[]");
		configurator.setConfigDetails(array);

		JSONArray retval = configurator.getConfigDetails();
		Assert.assertEquals(0, retval.length());
	}


	@Test
	public void testConfigureContactWithNullPhoneField() throws Exception {

		ContactsConfigurator svc = new ContactsConfigurator();

		ContactsConfigurator.ContactConfigurator configurator = new ContactsConfigurator.ContactConfigurator(svc);
		configurator.setContext(mContext);
		JSONArray array = new JSONArray("[{\"given\":\"\",\"email\":\"grant@dashwire.com\",\"family\":\"\",\"phones\":null}]");
		configurator.setConfigDetails(array);

		configurator.configure();
		ShadowContentResolver resolver = Robolectric.shadowOf(mContext.getContentResolver());


		Intent result = Robolectric.shadowOf(svc).getBroadcastIntents().get(0);
		assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", result.getAction()); // Proxy for successful onSuccess call
		assertEquals("success", result.getStringExtra("result"));

		Assert.assertEquals(3, resolver.getContentProviderOperations(ContactsContract.AUTHORITY).size());
	}

	@Test
	public void testConfigureContactWithEmptyArrayPhoneField() throws Exception {

		ContactsConfigurator svc = new ContactsConfigurator();

		ContactsConfigurator.ContactConfigurator configurator = new ContactsConfigurator.ContactConfigurator(svc);
		configurator.setContext(mContext);
		JSONArray array = new JSONArray("[{\"given\":\"\",\"family\":\"\",\"phones\":[],\"email\":\"grant@dashwire.com\"}]");
		configurator.setConfigDetails(array);

		configurator.configure();
		ShadowContentResolver resolver = Robolectric.shadowOf(mContext.getContentResolver());

		Intent result = Robolectric.shadowOf(svc).getBroadcastIntents().get(0);
		assertEquals("com.dashwire.feature.intent.CONFIGURATION_RESULT", result.getAction()); // Proxy for successful onSuccess call
		assertEquals("success", result.getStringExtra("result"));

		Assert.assertEquals(3, resolver.getContentProviderOperations(ContactsContract.AUTHORITY).size());

		ArrayList<ContentProviderOperation> operations = resolver.getContentProviderOperations(ContactsContract.AUTHORITY);
		ShadowContentProviderOperation shadowOp = Robolectric.shadowOf(operations.get(0));
	}

	// TODO: Write test with all fields filled out
	// TODO: Write test with multiple phone numbers
	// TODO: Write some code to check that the values are being set correcting in the ContentProviderOperations
	// TODO: Fix original problem
	// TODO: Refactor code as appropriate




}
