package io.boolio.android.network.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 4/16/15.
 */
public class JSONArrayParser<T> {
    public List<T> toArray(JSONArray array) throws JSONException {
        return toArray(array, null);
    }

    public List<T> toArray(JSONArray array, Parser<T> parser) throws JSONException {
        return toArray(array, parser, false);
    }

    public List<T> toArray(JSONArray array, Parser<T> parser, boolean reverse) throws JSONException {
        List<T> results = new ArrayList<>(array.length());
        if (reverse) {
            for (int i = array.length() - 1; i >= 0; i--) {
                results.add(parser == null ? (T) array.get(i) : parser.parse((JSONObject) array.get(i)));
            }

        } else {
            for (int i = 0; i < array.length(); i++) {
                results.add(parser == null ? (T) array.get(i) : parser.parse((JSONObject) array.get(i)));
            }
        }
        return results;
    }

}
