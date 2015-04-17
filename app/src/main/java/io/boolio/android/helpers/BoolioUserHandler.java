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
    Runnable callback;

    public BoolioUserHandler(Context context) {
        this.context = context;
    }

    public static BoolioUserHandler getInstance(final Context context) {
        if (instance == null) {
            instance = new BoolioUserHandler(context);
        }
        return instance;
    }

    /**
     * Getters *
     */
    public User getUser() {
        return user;
    }

    /**
     * Setters *
     */
    public void setUser(User user) {
        this.user = user;
        if (callback != null)
            callback.run();
    }

    public void setUserCallback(Runnable runnable) {
        callback = runnable;
    }
}
