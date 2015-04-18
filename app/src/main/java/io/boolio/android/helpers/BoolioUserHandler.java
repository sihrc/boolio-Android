package io.boolio.android.helpers;

import android.content.Context;

import com.facebook.Session;

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
        if (user == null || user.userId.isEmpty()) {
            user = new User();
            user.userId = PrefsHelper.getInstance(context).getString("userId");
        }
        return user;
    }

    /**
     * Setters *
     */
    public void setUser(User user) {
        this.user = user;
        PrefsHelper.getInstance(context).saveString("userId", user.userId);
        if (callback != null)
            callback.run();
    }

    public void logout() {
        user = null;
        PrefsHelper.getInstance(context).saveString("facebookId", "");
        PrefsHelper.getInstance(context).saveString("userId", "");
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            session.closeAndClearTokenInformation();
        }
    }

    public void setUserCallback(Runnable runnable) {
        callback = runnable;
    }
}
