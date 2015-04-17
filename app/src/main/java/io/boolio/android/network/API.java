package io.boolio.android.network;

/**
 * Created by Chris on 4/16/15.
 */
public class API {
    //    final static private String BASE = "http://beta.boolio.io";
    final static private String BASE = "http://10.7.64.118:3000/api";

    // Auth Routes
    final static public String FACEBOOK_USER_ENDPOINT = BASE + "/users/facebook";

    final static public String FEED_ENDPOINT = BASE + "/questions";

    static public String GET_USER_ENDPOINT(String id) {
        return BASE + "/users/" + id;
    }

}
