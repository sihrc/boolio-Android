package io.boolio.android.network;

/**
 * Created by Chris on 4/16/15.
 */
public class API {
    final static private String BASE = "http://beta.boolio.io/api";

    // Auth Routes
    final static public String FACEBOOK_USER_ENDPOINT = BASE + "/users/facebook";

    //  FIX ME

    final static public String FEED_ENDPOINT = BASE + "/questions";
    final static public String CREATE_QUESTION_ENDPOINT = BASE + "/questions/create";

    final static public String POST_ANSWER_ENDPOINT = BASE + "/questions/answer";

    final static public String SEARCH_ENDPOINT = BASE + "/questions/search";


    static public String GET_USER_ENDPOINT(String id) {
        return BASE + "/users/" + id;
    }

    static public String GET_USER_ASKED(String id) {
        return BASE + "/questions/" + id + "/asked";
    }

    static public String GET_USER_ANSWERED(String id) {
        return BASE + "/questions/" + id + "/answered";
    }

}
