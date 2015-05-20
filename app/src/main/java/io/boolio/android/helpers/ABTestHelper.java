package io.boolio.android.helpers;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris on 5/15/15.
 */
public class ABTestHelper {
    static ABTestHelper instance;
    Context context;

    public Map<String, Integer> abtests = new HashMap<>();


    public static ABTestHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ABTestHelper(context);
        }
        return instance;
    }

    public ABTestHelper(Context context) {
        this.context = context;
    }


    public void addTest(String name, int value) {
        abtests.put(name, value);
        Log.i("DebugDebug", name + " valOf " + value);
    }

    public int getTest(String key) {
        if (!abtests.containsKey(key)) {
            return PrefsHelper.getInstance(context).getInt(key);
        }
        return abtests.get(key);
    }
}
