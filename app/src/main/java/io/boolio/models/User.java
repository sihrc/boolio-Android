package io.boolio.models;

import org.json.JSONObject;

/**
 * Created by Chris on 3/13/15.
 */
public class User {

    /**
     * Parses JSON from Boolio-Web
     * @param jsUser Package returned from Boolio-Web
     * @return User
     */
    public static User fromJSON(JSONObject jsUser) {
        return new User();
    }

    public User() {
        //TODO
    }
}
