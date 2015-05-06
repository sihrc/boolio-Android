package io.boolio.android.tracking;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

import io.boolio.android.R;

/**
 * Created by Chris on 5/6/15.
 */
public class GAHelper {
    static GAHelper instance;

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();
    Context context;


    public static GAHelper getInstance(Context context) {
        if (instance == null) {
            instance = new GAHelper(context);
        }
        return instance;
    }

    public GAHelper(Context context) {
        this.context = context;
        enableAdvertising();
    }

    public void enableAdvertising() {
        // Get tracker.
        Tracker t = getTracker(
                TrackerName.APP_TRACKER);

        // Enable Advertising Features.
        t.enableAdvertisingIdCollection(true);
    }

    public void screenSend(String name) {
        // Get tracker.
        Tracker t = getTracker(TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName(name);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void measureEvent(int categoryId, int actionId, int labelId) {
        // Get tracker.
        Tracker t = getTracker(TrackerName.APP_TRACKER);
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(context.getString(categoryId))
                .setAction(context.getString(actionId))
                .setLabel(context.getString(labelId))
                .build());
    }

    public void userTiming(int categoryId, int timingId, int labelId, long time) {
        // Get tracker.
        Tracker t = getTracker(TrackerName.APP_TRACKER);

        // Build and send timing.
        t.send(new HitBuilders.TimingBuilder()
                .setCategory(context.getString(categoryId))
                .setValue(time)
                .setVariable(context.getString(timingId))
                .setLabel(context.getString(labelId))
                .build());
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
//            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
//                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
//                    : analytics.newTracker(R.xml.ecommerce_tracker);
            Tracker t = analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
    }


}
