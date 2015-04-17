package io.boolio.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import io.boolio.android.R;

/**
 * Created by Chris on 4/16/15.
 */
public class PrefsHelper {
    static PrefsHelper instance;

    SharedPreferences prefs;

    public static PrefsHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PrefsHelper();
            instance.prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        }

        return instance;
    }

    /** Getters **/
    public String getString(String key) {
        return prefs.getString(key, "");
    }

    /** Setters **/
    public void saveString(String key, String value) {
        if (value == null) {
            Log.e("PreferenceHandler", key + ", value is null");
            return;
        }
        prefs.edit().putString(key, value).apply();
    }
}
