package io.boolio.android.network;

/**
 * Created by Chris on 4/16/15.
 */
public class API {
    //    final static private String BASE = "http://beta.boolio.io";
    final static private String BASE = "http://10.26.68.22:3000/api";

    // Auth Routes
    final static public String FACEBOOK_USER_ENDPOINT = BASE + "/users/facebook";

    final static public String FEED_ENDPOINT = BASE + "/questions";
    final static public String CREATE_QUESTION_ENDPOINT = BASE + "/questions/create";

}
