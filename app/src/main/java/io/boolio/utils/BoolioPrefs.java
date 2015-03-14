package io.boolio.utils;

import android.content.Context;
import android.content.SharedPreferences;

import io.boolio.R;

/**
 * Created by Chris on 3/13/15.
 */
public class BoolioPrefs {
    // Singleton instance
    static BoolioPrefs instance;

    // Preferences
    SharedPreferences sharedPreferences;

    // Keys
    final String FTUE = "FTUE";

    /**
     * @param context - context from which to use shared preferences
     * @return instance of prefs handler
     */
    public static BoolioPrefs getInstance(Context context) {
        if (instance == null)
            instance = new BoolioPrefs(context);
        return instance;
    }

    public BoolioPrefs(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    /**
     * Getters
     * gets attributes saved in preferences
     */
    public boolean isFTUE() {
        return sharedPreferences.getBoolean(FTUE, true);
    }


    /**
     * Setters
     * saves attributes in preferences
     */
    public void setFTUE(boolean isFTUE) {
        sharedPreferences.edit().putBoolean(FTUE, isFTUE).apply();
    }
}
