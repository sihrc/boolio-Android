package io.boolio.android.helpers;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris on 5/15/15.
 */
public class ABTestHelper {
    static ABTestHelper instance;
    public Map<String, Integer> abtests = new HashMap<>();
    Context context;


    public ABTestHelper(Context context) {
        this.context = context;
    }

    public static ABTestHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ABTestHelper(context);
        }
        return instance;
    }

    public void addTest(String name, int value) {
        abtests.put(name, value);
    }

    public int getTest(String key) {
        if (!abtests.containsKey(key)) {
            return PrefsHelper.getInstance().getInt(key);
        }
        return abtests.get(key);
    }
}
