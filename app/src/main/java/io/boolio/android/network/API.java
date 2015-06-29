package io.boolio.android.network;

/**
 * Created by Chris on 4/16/15.
 */
public class API {
    final static private String BASE = "https://api.boolio.io/api";

    // Auth Routes
    final static public String FACEBOOK_USER_ENDPOINT = BASE + "/users/facebook";

    // Questions
    final static public String FEED_ENDPOINT = BASE + "/questions";
    final static public String POST_UPLOAD_IMAGE = BASE + "/questions/image";
    final static public String POST_QUESTION = BASE + "/questions/create";
    final static public String POST_ANSWER = BASE + "/questions/answer";
    final static public String POST_UPDATE_QUESTION = BASE + "/questions/updateImage";
    final static public String POST_GET_QUESTIONS = BASE + "/questions/ids";
    final static public String REPORT_QUESTION = BASE + "/questions/report";
    final static public String DELETE_QUESTION = BASE + "/questions/delete";
    final static public String POST_SEARCH = BASE + "/questions/search";

    // Users
    final static public String POST_USER_GCMID = BASE + "/users/gcm";
    final static public String SKIP_QUESTION = BASE + "/users/skip";
    final static public String UNSKIP_QUESTION = BASE + "/users/unskip";
    final static public String GET_VERSION = BASE + "/configs/android/version";

    // Tests
    final static public String GET_TESTS = BASE + "/tests";

    static public String GET_USER_ENDPOINT(String id) {
        return BASE + "/users/" + id;
    }
}
