package io.boolio.android.network.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

/**
 * Created by Chris on 4/16/15.
 */
public abstract class Parser<T> {
    private final static boolean DEBUG = false;
    JSONObject object;

    String getString(String key) throws JSONException {
        if (object.has(key))
            return object.getString(key);
        if (DEBUG)
            Log.e("Parser", "Missing Key: " + key);
        return null;
    }

    int getInt(String key) throws JSONException {
        if (object.has(key))
            return object.getInt(key);
        if (DEBUG)
            Log.e("Parser", "Missing Key: " + key);
        return 0;
    }

    JSONObject getJSONObject(String key) throws JSONException {
        if (object.has(key))
            return object.getJSONObject(key);
        if (DEBUG)
            Log.e("Parser", "Missing Key: " + key);
        return null;
    }

    JSONArray getJSONArray(String key) throws JSONException {
        if (object.has(key))
            return object.getJSONArray(key);
        if (DEBUG)
            Log.w("Parser", "Missing Key: " + key);
        return new JSONArray();
    }

    public T parse(JSONObject object) {
        this.object = object;
        return parse();
    }

    public abstract T parse();

    void put(JSONObject object, String key, Object value) throws JSONException {
        if (value == null)
            return;
        object.put(key, value);
    }

    public abstract JSONObject toJSON(T object);
    public abstract JSONArray toListIDs(Collection<T> objects);
}
