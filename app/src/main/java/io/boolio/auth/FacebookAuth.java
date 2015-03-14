package io.boolio.auth;

import android.os.Bundle;

/**
 * Created by Chris on 3/13/15.
 */
public class FacebookAuth extends Auth {
    // Singleton Instance
    static FacebookAuth instance;

    /**
     * Finds the running instance of FacebookAuth for App Session
     * @return FacebookAuth
     */
    public static FacebookAuth getInstance() {
        if (instance == null)
            instance = new FacebookAuth();

        return instance;
    }

    @Override
    public void authenticate(Bundle arguments) {

    }

    @Override
    public void unAuthenticate() {

    }
}
