package io.boolio.android.helpers.tracking;

import android.content.Context;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import io.boolio.android.BuildConfig;
import io.boolio.android.R;

/**
 * Created by Chris on 6/3/15.
 */
public class EventTracker {
    static EventTracker instance;
    boolean personVerified = false;

    Context context;
    MixpanelAPI mixpanel;


    /**
     * Initialize the library with your
     * Mixpanel project token, MIXPANEL_TOKEN, and a reference
     * to your application context.
     */
    public static void init(Context context) {
        if (instance == null)
            instance = new EventTracker(context);
        if (instance.mixpanel == null)
            instance.mixpanel = MixpanelAPI.getInstance(context, context.getString(R.string.mixpanel_token));
    }

    public static EventTracker getInstance(Context context) {
        if (instance == null)
            init(context);
        return instance;
    }

    public EventTracker(Context context) {
        this.context = context;
        setupDefaultValues();
    }

    private void setupDefaultValues() {
        // Send a "User Type: Paid" property will be sent
        // with all future track calls.
        JSONObject props = new JSONObject();
        try {
            props.put("client", "Android");
            props.put("version", BuildConfig.VERSION_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanel.registerSuperProperties(props);
    }

    public void attachUser(String userId) {
        // Associate all future events sent from
        // the library with the distinct_id from boolio server
        if (mixpanel != null) {
            mixpanel.identify(userId);
            personVerified = true;
        }
    }

    /**
     * Add user data to mixpanel user profile
     */
    public void addUserData(String userId, String key, String value) {
        if (!personVerified) {
            Log.w("MixPanel", "Cannot set profile tags for unverified user");
            return;
        }
        mixpanel.getPeople().identify(userId);
        mixpanel.getPeople().set(key, value);
    }

    /**
     * Increment tags
     */
    public void incrementTag(ProfileTag tag, int value) {
        if (tag == null || value == 0)
            return;
        mixpanel.getPeople().increment(tag.toString(), value);
    }

}
