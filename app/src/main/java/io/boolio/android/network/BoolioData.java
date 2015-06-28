package io.boolio.android.network;

import java.security.InvalidParameterException;
import java.util.HashMap;

/**
 * Created by Chris on 6/17/15.
 */
public class BoolioData extends HashMap<String, Object> {
    String[] keys;

    public static BoolioData create() {
        return new BoolioData();
    }

    public static BoolioData keys(String... strings) {
        BoolioData data = new BoolioData();
        data.keys = strings;
        return data;
    }

    public BoolioData values(Object... objs) {
        if (objs.length != keys.length) {
            throw new InvalidParameterException("Keys are not the same length as objects");
        }
        for (int i = 0; i < objs.length; i++) {
            put(keys[i], objs[i]);
        }

        return this;
    }

    public BoolioData add(String key, Object value) {
        put(key, value);
        return this;
    }
}
