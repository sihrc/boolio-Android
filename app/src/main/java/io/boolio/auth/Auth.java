package io.boolio.auth;

import android.os.Bundle;

import io.boolio.models.User;

/**
 * Created by Chris on 3/13/15.
 */
public abstract class Auth {
    User user;
    UnAuthedUIUpdate unAuthedUIUpdate;

    /**
     * Check if User information exists
     */
    public boolean isAuthed() {
        return user == null;
    }

    /**
     * Un-authenticate User
     * Should clear user information completely
     * Should call unAuthenticate();
     */
    public void unAuth() {
        //TODO
        user = null;
        unAuthedUIUpdate.update();
    }

    /**
     * Callback for updating UI to reflect auth status
     * @param unAuthedUIUpdate - callback
     */
    public void setUnAuthedUIUpdate(UnAuthedUIUpdate unAuthedUIUpdate) {
        this.unAuthedUIUpdate = unAuthedUIUpdate;
    }

    // Authenticate using various auth methods
    public abstract void authenticate(Bundle arguments);
    public abstract void unAuthenticate();
}
