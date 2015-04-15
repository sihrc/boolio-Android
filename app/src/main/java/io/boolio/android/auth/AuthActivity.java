package io.boolio.android.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

/**
 * Created by Chris on 3/14/15.
 */
public class AuthActivity extends FragmentActivity {

    // Navigation Management
    FragmentManager fragmentManager;
    boolean isResumed = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Auth.getAuth().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();

        /** Initialize Auth Handlers **/
        FacebookAuth.newInstance(this);
        BoolioAuth.newInstance();

        Auth.setCallbacks(new AuthCallback() {
            @Override
            public void run() {
                postLoginCallback();
            }
        }, new AuthCallback() {
            @Override
            public void run() {
                postLogoutCallback();
            }
        });

        Auth.getAuth().onCreate(this, savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    /**
     * What to do once logged in / logged Out
     * Overrode in child activity.
     */
    protected void postLoginCallback() {
        Log.i(AuthActivity.class.getName(), "Post Login Callback");
    }

    protected void postLogoutCallback() {
        Log.i(AuthActivity.class.getName(), "Post Logout Callback");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Auth.getAuth().onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
        Auth.getAuth().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        Auth.getAuth().onResume();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Auth.getAuth().onResumeFragments();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Auth.getAuth().onSaveInstanceState(outState);
    }
}