package io.boolio.android.helpers;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import io.boolio.android.helpers.tracking.EventTracker;
import io.boolio.android.network.clients.BoolioUserClient;
import io.boolio.android.network.helpers.BoolioCallback;

/**
 * Created by Chris on 5/4/15.
 */
public class Debugger {
    static Debugger instance;

    public static Debugger getInstance() {
        if (instance == null)
            instance = new Debugger();
        return instance;
    }

    // Enable Debugging
    public static Map<Class, Boolean> tags = new HashMap<Class, Boolean>() {{
        put(BoolioCallback.class, false);
        put(EventTracker.class, false);
    }};

    public static void log(Class tag, String msg) {
        if (tags.containsKey(tag) && tags.get(tag)) {
            Log.d("BoolioDebug", tag.getSimpleName() + ":\n" + msg);
        }
    }

    public static void i(String msg) {
        Log.i("DebugDebug", msg);
    }
}
