package io.boolio.android.network;

/**
 * Created by Chris on 4/16/15.
 */
public class API {
    //    final static private String BASE = "https://beta.boolio.io/api";
    final static private String BASE = "http://10.7.88.138:3000/api";

    // Auth Routes
    final static public String FACEBOOK_USER_ENDPOINT = BASE + "/users/facebook";


    final static public String FEED_ENDPOINT = BASE + "/questions";
    final static public String POST_UPLOAD_IMAGE = BASE + "/questions/image";
    final static public String POST_QUESTION = BASE + "/questions/create";
    final static public String POST_ANSWER = BASE + "/questions/answer";
    final static public String POST_UPDATE_QUESTION = BASE + "/questions/updateImage";
    final static public String POST_GET_QUESTIONS = BASE + "/questions/ids";
    final static public String POST_SEARCH = BASE + "/questions/search";

    static public String GET_USER_ENDPOINT(String id) {
        return BASE + "/users/" + id;
    }
}
