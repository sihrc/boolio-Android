package io.boolio.android.helpers;

import android.content.Context;

import io.boolio.android.models.User;

/**
 * Created by Chris on 4/16/15.
 */
public class BoolioUserHandler {
    static BoolioUserHandler instance;

    Context context;
    User user;

    public BoolioUserHandler(Context context) {
        this.context = context;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new BoolioUserHandler(context);
        }
    }
    public static BoolioUserHandler getInstance() {
        return instance;
    }

    /**
     * Getters *
     */
    public User getUser() {
        return user;
    }
    public String getUserId() {
        if (user == null)
            return null;
        return user._id;
    }

    /**
     * Setters *
     */
    public void setUser(User user) {
        this.user = user;
    }
}
