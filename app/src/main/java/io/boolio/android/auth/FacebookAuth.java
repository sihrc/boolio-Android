package io.boolio.android.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

/**
 * Created by Chris on 3/18/15.
 */
public class FacebookAuth extends Auth {
    static FacebookAuth instance;

    Activity activity;
    UiLifecycleHelper fbUIHelper;

    public static FacebookAuth newInstance(Activity activity) {
        instance = new FacebookAuth();
        instance.activity = activity;
        return instance;
    }

    public static FacebookAuth getInstance() {
        return instance;
    }

    public boolean isAuthed() {
        Session session = Session.getActiveSession();
        return (session != null && session.isOpened());
    }

    public void onCreate(Activity activity, Bundle savedInstanceState) {
        fbUIHelper = new UiLifecycleHelper(activity, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState sessionState, Exception e) {
                if (!isResumed)
                    return;
                // Session is open - user is logged in via facebook
                checkAuth();
            }
        });
        fbUIHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        fbUIHelper.onResume();

        // Facebook Logger
        AppEventsLogger.activateApp(activity);
    }

    @Override
    public void onPause() {
        fbUIHelper.onPause();

        //Facebook Logger
        AppEventsLogger.deactivateApp(activity);
    }

    @Override
    public void onDestroy() {
        fbUIHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        fbUIHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fbUIHelper.onActivityResult(requestCode, resultCode, data);
    }
}