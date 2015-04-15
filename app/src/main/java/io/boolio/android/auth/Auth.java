package io.boolio.android.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import io.boolio.android.models.UserType;


/**
 * Created by Chris on 3/18/15.
 * Abstract class for all Authentication Implementations
 * Ties in with Activity LifeCycle
 */
public abstract class Auth {
    /**
     * Currently used Auth *
     */
    static Auth curAuth;

    static AuthCallback loggedIn, loggedOut;
    boolean isResumed = false;

    /**
     * Get currently used Auth method
     */
    public static Auth getAuth() {
        if (curAuth == null)
            curAuth = FacebookAuth.getInstance();
        return curAuth;
    }

    /**
     * Change Auth Type based on UserType *
     */
    public static void setAuth(UserType userType) {
        switch (userType) {
            case FACEBOOK:
                curAuth = FacebookAuth.getInstance();
                break;
            default:
                curAuth = BoolioAuth.getInstance();
                break;
        }
    }

    public static void setCallbacks(AuthCallback in, AuthCallback out) {
        loggedIn = in;
        loggedOut = out;
    }

    /**
     * Authentication *
     */
    public void checkAuth() {
        if (isAuthed()) {
            loggedIn.run();
        } else {
            loggedOut.run();
        }
    }

    public abstract boolean isAuthed();

    /**
     * LifeCycle callbacks *
     */
    public abstract void onCreate(Activity activity, Bundle savedInstanceState);

    public void onResume() {
        isResumed = true;
        checkAuth();
    }

    public void onPause() {
        isResumed = false;
        checkAuth();
    }

    public abstract void onDestroy();

    public abstract void onSaveInstanceState(Bundle outState);

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public void onResumeFragments() {
        checkAuth();
    }
}