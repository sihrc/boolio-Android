package io.boolio.android.helpers.tracking;

/**
 * Created by Chris on 6/4/15.
 */
public enum TrackEvent {
    OPEN_APP("app_opened");

    String name;
    TrackEvent(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
