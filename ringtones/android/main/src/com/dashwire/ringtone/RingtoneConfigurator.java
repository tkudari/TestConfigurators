package com.dashwire.ringtone;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.configurator.ConfiguratorBaseIntentService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class RingtoneConfigurator extends ConfiguratorBaseIntentService {

    private HashMap<String, String> recordedExceptions = new HashMap<String, String>();

    @Override
    protected void onConfigFeature() {

        try {

            for (int i = 0; i < mFeatureDataJSONArray.length(); i++) {
                JSONObject data = mFeatureDataJSONArray.getJSONObject(i);
                DashLogger.v(TAG, "onConfigFeature : " + data.toString());
                // TODO uri for silent?
                String uriString = data.get("uri").toString();
                Uri uri = Uri.parse(uriString);

                if (!ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                    uri = getAudioContentUri(uri);
                }

                String type = data.getString("type");
                setRingtone(type, uri);
            }
        } catch (Exception ex) {
            DashLogger.d("RingtoneConfigurator", "exception in onConfigFeature.. " + ex.getMessage());
            onFailure(TAG + " Exception : " + ex.getMessage());
            recordException("onConfigFeature", ex);
            return;
        }

        onSuccess();
    }

    public Uri getAudioContentUri(Uri uri) {
        String path = uri.getPath();
        Uri contentUri = MediaStore.Audio.Media.getContentUriForPath(path);
        String[] proj = {
                MediaStore.Audio.Media._ID
        };
        int idIndex = 0;
        String selection = MediaStore.Audio.Media.DATA + " LIKE '" + path + "'";
        Cursor cursor = getApplicationContext().getContentResolver().query(contentUri, proj, selection, null, null);
        try {
            if (cursor.moveToNext()) {
                return ContentUris.withAppendedId(contentUri, cursor.getInt(idIndex));
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            recordException("getAudioContentUri", ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        throw new IllegalArgumentException(uri.toString());
    }

    public void setRingtone(String type, Uri uri) {
        if (type.equalsIgnoreCase("call")) {
            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE, uri);
        } else if (type.equalsIgnoreCase("sms")) {
            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION, uri);
        }
    }

    public void injectFeatureJSONArray(JSONArray jsonArray) {
        mFeatureDataJSONArray = jsonArray;
    }

    private void recordException(String methodName, Exception e) {
        DashLogger.d(TAG, methodName + ": Recording Exception: " + e.getClass().getName());
        recordedExceptions.put(methodName, e.getClass().getName());
    }

    public String getLastRecordedException(String methodName) {
        return recordedExceptions.get(methodName);
    }

}
