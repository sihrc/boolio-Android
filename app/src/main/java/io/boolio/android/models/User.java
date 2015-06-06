package io.boolio.android.models;

import java.util.List;

/**
 * Created by Chris on 4/16/15.
 */
public class User {

    public String name, userId, profilePic, oauthId, gcmId;
    public int version;
    public List<String> questionsAsked, questionsAnswered, questionsSkipped;

    public User(String oauthId, String name) {
        this.name = name;
        this.oauthId = oauthId;
    }
}
