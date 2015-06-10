package io.boolio.android.network.models;

import java.security.InvalidParameterException;
import java.util.HashMap;

/**
 * Created by Chris on 6/17/15.
 */
public class BoolioData extends HashMap<String, Object> {
    String[] keys;

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
}
