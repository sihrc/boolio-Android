package io.boolio.android.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Chris on 3/18/15.
 */
public class BoolioAuth extends Auth {
    static BoolioAuth instance;

    public static BoolioAuth newInstance() {
        instance = new BoolioAuth();
        return instance;
    }

    public static BoolioAuth getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public boolean isAuthed() {
        return false;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}