package io.boolio.auth;

import android.os.Bundle;

/**
 * Created by Chris on 3/13/15.
 */
public class BoolioAuth extends Auth {
    // Singleton Instance
    static BoolioAuth instance;

    /**
     * Finds the running instance of FacebookAuth for App Session
     * @return FacebookAuth
     */
    public static BoolioAuth getInstance() {
        if (instance == null)
            instance = new BoolioAuth();

        return instance;
    }

    @Override
    public void authenticate(Bundle arguments) {

    }

    @Override
    public void unAuthenticate() {

    }
}
