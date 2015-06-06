package io.boolio.android;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.facebook.Profile;

import java.util.HashMap;

import io.boolio.android.fragments.FeedFragment;
import io.boolio.android.fragments.MainFragment;
import io.boolio.android.fragments.ProfileFragment;
import io.boolio.android.fragments.tutorials.TutorialPagerFragment;
import io.boolio.android.gcm.GCMHelper;
import io.boolio.android.helpers.ApplicationCheckHelper;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.Dialogs;
import io.boolio.android.helpers.FacebookAuth;
import io.boolio.android.helpers.PrefsHelper;
import io.boolio.android.helpers.tracking.EventTracker;
import io.boolio.android.helpers.tracking.TrackEvent;
import io.boolio.android.models.User;
import io.boolio.android.network.NetworkCallback;
import io.boolio.android.network.ServerUser;


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

        ServerUser.getInstance(this).getABTests();
        ServerUser.getInstance(this).getConfigs(new Runnable() {
            @Override
            public void run() {
                updateApp();
            }
        });
    }

    @Override
    public void loggedIn(Profile profile) {
        NetworkCallback<User> userCallback = new NetworkCallback<User>() {
            @Override
            public void handle(User object) {
                BoolioUserHandler.getInstance(MainActivity.this).setUser(object);
                GCMHelper.getInstance(MainActivity.this).getRegistrationId();
                fragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance(parseIntent(getIntent()))).commitAllowingStateLoss();
                PrefsHelper.getInstance(MainActivity.this).saveString("userId", object.userId);
                EventTracker.getInstance(MainActivity.this).attachUser(object.userId);
                EventTracker.getInstance(MainActivity.this).track(TrackEvent.OPEN_APP);
                updateApp();
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
     *
     * @param intent - intent from the notification or other sources
     * @return order of fragment
     */
    private int parseIntent(Intent intent) {
        if (intent == null) {
            return FeedFragment.ORDER;
        }

        switch (intent.getAction()) {
            case "boolio-question":
                EventTracker.getInstance(this).track(TrackEvent.PUSH_NOTIFICATION, new HashMap<String, Object>() {{
                    put("type", "update_answers");
                }});
                return ProfileFragment.ORDER;
            case "new-feed":
                EventTracker.getInstance(this).track(TrackEvent.PUSH_NOTIFICATION, new HashMap<String, Object>(){{
                    put("type", "new_questions");
                }});
                return FeedFragment.ORDER;

            default:
                return FeedFragment.ORDER;
        }
    }

    private void updateApp() {
        if (BuildConfig.VERSION_CODE < Integer.parseInt(PrefsHelper.getInstance(this).getString("version"))) {
            Dialogs.messageDialog(this, R.string.update_title, R.string.update_message, new Runnable() {
                @Override
                public void run() {
                    final String appPackageName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
        }
    }
}
