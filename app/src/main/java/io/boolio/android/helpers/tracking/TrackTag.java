package io.boolio.android.helpers.tracking;

/**
 * Created by Chris on 6/4/15.
 */
public enum TrackTag {
    CLIENT("client"),
    VERSION("version"),
    DATE("date");

    String name;

    TrackTag(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
