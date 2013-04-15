package com.dashwire.contacts;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import com.dashwire.configurator.ConfiguratorBaseIntentService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContactsConfigurator extends ConfiguratorBaseIntentService {

	@Override
	protected void onConfigFeature() {
		ContactConfigurator configurator = new ContactConfigurator(this);
		configurator.setContext(this);
		configurator.setConfigDetails(mFeatureDataJSONArray);
		configurator.configure();
	}


	public static class ContactConfigurator {
		protected static final String TAG = ContactConfigurator.class.getCanonicalName();

		protected Context mContext;

		protected ContentResolver mContentResolver;

		protected JSONArray mConfigArray;

		protected ContactsConfigurator mConfigurator;

		public ContactConfigurator(ContactsConfigurator configurator  ) {
			mConfigurator = configurator;
		}

		public String name() {
			return "contacts";
		}

		public boolean createContact( Contact item ) {
			boolean succeeded = true;

			if (item == null) return false;

			int backRefIndex = 0;

			ArrayList<ContentProviderOperation> op_list = new ArrayList<ContentProviderOperation>();

			op_list.add( ContentProviderOperation.newInsert( ContactsContract.RawContacts.CONTENT_URI ).withValue( ContactsContract.RawContacts.ACCOUNT_TYPE, null ).withValue(
					ContactsContract.RawContacts.ACCOUNT_NAME, null ).build() );

			op_list.add( ContentProviderOperation.newInsert( ContactsContract.Data.CONTENT_URI ).withValueBackReference( ContactsContract.Data.RAW_CONTACT_ID,
					backRefIndex ).withValue( ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE ).withValue(
					ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, item.getName() ).build() );

			if (item.hasPhoneNumbers()) {
				for (Contact.ContactPhoneNumber cpn : item.getNumbers()) {
					op_list.add( ContentProviderOperation.newInsert( ContactsContract.Data.CONTENT_URI ).withValueBackReference( ContactsContract.Data.RAW_CONTACT_ID, backRefIndex ).withValue(
							ContactsContract.CommonDataKinds.Phone.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE ).withValue( ContactsContract.CommonDataKinds.Phone.NUMBER, cpn.getNumber() ).withValue( ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE ).withValue(
							ContactsContract.CommonDataKinds.Phone.TYPE, cpn.getType() ).build() );
				}
			}

			op_list.add( ContentProviderOperation.newInsert( ContactsContract.Data.CONTENT_URI ).withValueBackReference( ContactsContract.Data.RAW_CONTACT_ID, backRefIndex ).withValue(
					ContactsContract.CommonDataKinds.Email.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE ).withValue(
					ContactsContract.CommonDataKinds.Email.DATA, item.getEmail() ).withValue( ContactsContract.CommonDataKinds.Email.TYPE,
					ContactsContract.CommonDataKinds.Email.TYPE_HOME ).build() );
			try {
				mContentResolver.applyBatch( ContactsContract.AUTHORITY, op_list );
			} catch ( OperationApplicationException exp ) {
				succeeded = false;
			} catch ( RemoteException exp ) {
				succeeded = false;
			}
			return succeeded;
		}

		public void setContext( Context context ) {
			mContext = context;
			mContentResolver = context.getContentResolver();
		}


		public void setConfigDetails( JSONArray configArray ) {
			mConfigArray = configArray;
		}

		public JSONArray getConfigDetails() {
			return mConfigArray;
		}


		public void configure() {
			boolean succeeded = true;
			try {
				for ( int index = 0; index < mConfigArray.length(); index++ ) {
					Contact contact = Contact.parseFromJSONObject(mConfigArray.getJSONObject( index ));
					succeeded &= createContact( contact );
				}
			} catch ( JSONException je ) {
				succeeded = false;
			}

			if (succeeded) {
				mConfigurator.onSuccess();
			} else {
				mConfigurator.onFailure("Unable to set all contacts");
			}
		}


		public static class Contact {
			String name;
			String email;
			ContactPhoneNumber[] numbers;

			void setName(String n) {
				name = n;
			}

			void setEmail(String e) {
				email = e;
			}

			void setNumbers(ContactPhoneNumber[] n) {
				numbers = n;
			}

			public String getName() {
				return name;
			}

			public String getEmail() {
				return email;
			}

			public boolean hasPhoneNumbers() {
				return numbers != null && numbers.length > 0;
			}

			public ContactPhoneNumber[] getNumbers() {
				return numbers;
			}



			public static Contact parseFromJSONObject(JSONObject obj) {
				Contact retval = null;
				try {

					String name = obj.has( "name" ) ? obj.getString( "name" ) : "";
					String given = obj.has( "given" ) ? obj.getString( "given" ) : "";
					String family = obj.has( "family" ) ? obj.getString( "family" ) : "";

					if ( given.length() > 0 ) {
						name = given + " " + family;
					}

					String email = obj.has( "email" ) ? obj.getString( "email" ) : "";

					retval = new Contact();
					retval.setEmail(email);
					retval.setName(name);

					if (!obj.isNull("phones")) {
						Object tempPhones = obj.get("phones");
						if (tempPhones instanceof JSONArray) {
							retval.setNumbers(ContactPhoneNumber.parseFromJSONArray((JSONArray)tempPhones));
						} else {
							// houston we have a problem
							// nah....just do nothing which means numbers may be null
						}
					}
				} catch (JSONException ex) {

				}
				return retval;
			}


			public static class ContactPhoneNumber {
				String number;
				int type = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

				void setNumber(String n) {
					number = n;
				}

				void setType(int t) {
					type = t;
				}

				public String getNumber() {
					return number;
				}

				public int getType() {
					return type;
				}


				public static ContactPhoneNumber[] parseFromJSONArray(JSONArray array) {
					ArrayList<ContactPhoneNumber> retval = new ArrayList<ContactPhoneNumber>();

					for (int i = 0; i < array.length(); i++) {
						try {
							JSONObject phoneNumber = array.getJSONObject(i);

							int phoneType = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
							if (phoneNumber.has("type")) {
								String type = phoneNumber.getString("type");
								if ("home".equals(type)) {
									phoneType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
								} else if ("work".equals(type)) {
									phoneType = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
								}
							}

							// TODO: This is the original logic but does it make sense to insert a blank number?
							String number = phoneNumber.has("number") ? phoneNumber.getString("number") : "";

							ContactPhoneNumber cpn = new ContactPhoneNumber();
							cpn.setNumber(number);
							cpn.setType(phoneType);
							retval.add(cpn);
						} catch (JSONException ex) {
							// Eat it if we have a JSON exception
						}
					}
					return retval.toArray(new ContactPhoneNumber[retval.size()]);
				}
			}
		}
	}
}