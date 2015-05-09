package io.boolio.android;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Profile;

import io.boolio.android.fragments.FeedFragment;
import io.boolio.android.fragments.MainFragment;
import io.boolio.android.fragments.ProfileFragment;
import io.boolio.android.fragments.tutorials.TutorialPagerFragment;
import io.boolio.android.gcm.GCMHelper;
import io.boolio.android.helpers.ApplicationCheckHelper;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.FacebookAuth;
import io.boolio.android.models.User;
import io.boolio.android.network.ServerUser;
import io.boolio.android.network.NetworkCallback;
import io.boolio.android.network.parser.UserParser;


public class MainActivity extends FacebookAuth {
    public static int SCREEN_WIDTH, SCREEN_HEIGHT;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        if (!ApplicationCheckHelper.checkPlayServices(this)) {
            Toast.makeText(this, getString(R.string.check_google_play_warning), Toast.LENGTH_LONG).show();
        }

        // Get Screen Size
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;
    }

    @Override
    public void loggedIn(Profile profile) {
        Log.i("DebugDebug", "here");
        NetworkCallback<User> userCallback = new NetworkCallback<User>() {
            @Override
            public void handle(User object) {
                BoolioUserHandler.getInstance(MainActivity.this).setUser(object);
                GCMHelper.getInstance(MainActivity.this).getRegistrationId();
                fragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance(parseIntent(getIntent()))).commit();
            }
        };

        User user = new User(profile.getId(), profile.getName());
        BoolioUserHandler.getInstance(this).setUser(user);
        ServerUser.getInstance(this).getBoolioUserFromFacebook
                (user, userCallback);
    }

    @Override
    public void loggedOut() {
        fragmentManager.beginTransaction().replace(R.id.container, TutorialPagerFragment.newInstance()).commit();
    }

    /**
     * Parses Intent and decides which fragment to show first
     * @param intent - intent from the notification or other sources
     * @return order of fragment
     */
    private int parseIntent(Intent intent) {
        if (intent == null) {
            return FeedFragment.ORDER;
        }

        switch (intent.getAction()) {
            case "boolio-question":
                return ProfileFragment.ORDER;
            default:
                return FeedFragment.ORDER;
        }
    }
}
