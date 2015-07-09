package io.boolio.android.models;

import java.util.List;

/**
 * Created by Chris on 4/16/15.
 */
public class User {

    public String name, _id, profilePic, oauthId, gcm;
    public List<String> questionsAsked;
    public List<String> questionsAnswered;

    public User(String oauthId, String name) {
        this.name = name;
        this.oauthId = oauthId;
    }
}
