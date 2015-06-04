package io.boolio.android.helpers.tracking;

/**
 * Created by Chris on 6/3/15.
 */
public enum ProfileTag {
    ASKED("asked"),
    ANSWERED("answered"),
    KARMA("karma");


    String name;
    ProfileTag(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
