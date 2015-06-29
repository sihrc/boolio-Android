package io.boolio.android.helpers.tracking;

import android.content.Context;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.boolio.android.BuildConfig;
import io.boolio.android.R;
import io.boolio.android.helpers.Debugger;
import io.boolio.android.models.Question;

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
    public static EventTracker getInstance(Context context) {
        if (instance == null)
            instance = new EventTracker(context);
        return instance;
    }

    public EventTracker(Context context) {
        this.context = context;
        this.mixpanel = MixpanelAPI.getInstance(context, context.getString(R.string.mixpanel_token));
        setupDefaultValues();
    }

    private void setupDefaultValues() {
        // Mixpanel already has date / operating system/ etc
        // Send a "User Type: Paid" property will be sent
        // with all future track calls.
        JSONObject props = new JSONObject();
        try {
            props.put("dev", BuildConfig.DEBUG);
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

    /**
     * Event Tracking *
     */
    public void track(TrackEvent event) {
        track(event, null);
    }

    public void trackQuestion(TrackEvent event, final Question question, final String answer) {
       track(event, new HashMap<String, Object>() {{
           put("tags", question.tags);
           put("has_image", question.image != null && !question.image.equals(""));
           put("default_answer", question.left.equals("No"));
           put("question_id", question._id);
           put("creator_id", question.creatorId);
           if (answer != null)
               put("answer", answer);
       }});
    }

    public void track(TrackEvent event, Map<String, Object> params) {
        JSONObject pack = new JSONObject();
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                try {
                    pack.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        Debugger.log(EventTracker.class, event.toString() + " " + pack.toString());
        mixpanel.track(event.toString(), pack);
    }

    public void flush() {
        if (mixpanel != null)
            mixpanel.flush();
    }

}
