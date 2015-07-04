package io.boolio.android.helpers.tracking;

/**
 * Created by Chris on 6/4/15.
 */
public enum TrackEvent {
    OPEN_APP("app_opened"),
    PULL_DOWN("pull_down"),
    CREATE_QUESTION("create_question"),
    ANSWER_QUESTION("answer_question"),
    SKIP_QUESTION("skip_question"),
    UNSKIP_QUESTION("unskip_question"),
    ATTEMPT_IMAGE_SEARCH("attempt_image_search"),
    CHOSE_IMAGE_SEARCH("chose_image_search"),
    LOAD_PICTURE("load_picture"),
    TAKE_PICTURE("take_picture"),
    BUTTON_NAVIGATE("button_navigate"),
    SLIDE_NAVIGATE("slide_navigate"),
    PUSH_NOTIFICATION("push_notification"),
    DELETE_QUESTION("delete_question"),
    REPORT_QUESTION("report_question"),
    SEARCH("search");

    String name;

    TrackEvent(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
