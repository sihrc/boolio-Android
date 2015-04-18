package io.boolio.android.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AppEventsLogger;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import io.boolio.android.helpers.PrefsHelper;
import io.boolio.android.network.BoolioServer;

/**
 * Created by Chris on 3/18/15.
 */
public class FacebookAuth extends Auth {
    static FacebookAuth instance;

    Activity activity;
    UiLifecycleHelper fbUIHelper;
    String facebookId;


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

    public void onCreate(final Activity activity, Bundle savedInstanceState) {
        fbUIHelper = new UiLifecycleHelper(activity, new Session.StatusCallback() {
            @Override
            public void call(final Session session, SessionState sessionState, Exception e) {
                if (!isResumed)
                    return;

                if (sessionState.isOpened()) {
                    getMe(session);
                }

                if (sessionState.isClosed()) {
                    ((AuthActivity) activity).postLogoutCallback();
                }
            }
        });
        fbUIHelper.onCreate(savedInstanceState);
    }

    // Facebook Get OAuthID From Me Request
    private void getMe(final Session session) {
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                // If the response is successful
                if (session == Session.getActiveSession() && user != null) {
                    facebookId = user.getId();
                    PrefsHelper.getInstance(activity).saveString("facebookId", facebookId);
                    // Session is open - user is logged in via facebook
                    checkAuth();
                    updateInformation();
                }
            }
        });
        Request.executeBatchAsync(request);
    }

    // Get Profile Information for Facebook Profile
    private void updateInformation() {
        if (facebookId != null && !facebookId.equals("")) {
            new Request(
                    Session.getActiveSession(),
                    "/me",
                    null,
                    HttpMethod.GET,
                    new Request.Callback() {
                        public void onCompleted(Response response) {
                            /* handle the result */
                            if (response.getError() != null) {
                                return;
                            }

                            BoolioServer.getInstance(activity).getBoolioUserFromFacebook(response.getGraphObject().getInnerJSONObject());
                        }
                    }
            ).executeAsync();
        } else {
            facebookId = PrefsHelper.getInstance(activity).getString("facebookId");
            if (facebookId.equals("")) {
                getMe(Session.getActiveSession());
            } else {
                updateInformation();
            }
        }
    }

    @Override
    public void onResume() {
        fbUIHelper.onResume();
        updateInformation();

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