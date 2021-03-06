package io.boolio.android;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.facebook.Profile;

import java.util.HashMap;

import butterknife.ButterKnife;
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
import io.boolio.android.helpers.Utils;
import io.boolio.android.helpers.tracking.EventTracker;
import io.boolio.android.helpers.tracking.TrackEvent;
import io.boolio.android.models.User;
import io.boolio.android.network.clients.BoolioGeneralClient;
import io.boolio.android.network.clients.BoolioUserClient;
import io.boolio.android.network.helpers.BoolioCallback;
import retrofit.Callback;


public class MainActivity extends FacebookAuth {
    public static int SCREEN_WIDTH, SCREEN_HEIGHT;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);
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

        checkVersion();
    }

    @Override
    public void loggedIn(Profile profile) {
        Callback<User> userCallback = new BoolioCallback<User>() {
            @Override
            public void handle(User object) {
                BoolioUserHandler.getInstance().setUser(object);
                GCMHelper.getInstance(MainActivity.this).getRegistrationId();
                PrefsHelper.getInstance().saveString("_id", object._id);
                EventTracker.getInstance(MainActivity.this).attachUser(object._id);
                EventTracker.getInstance(MainActivity.this).track(TrackEvent.OPEN_APP);
                fragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance(parseIntent(getIntent()))).commitAllowingStateLoss();
            }
        };
        User user = new User(profile.getId(), profile.getName());
        BoolioUserHandler.getInstance().setUser(user);
        BoolioUserClient.api().getBoolioUserFromFacebook
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
                EventTracker.getInstance(this).track(TrackEvent.PUSH_NOTIFICATION, new HashMap<String, Object>() {{
                    put("type", "new_questions");
                }});
                return FeedFragment.ORDER;

            default:
                return FeedFragment.ORDER;
        }
    }

    /**
     * Gets the app version from the play store through an external API call
     */
    private void checkVersion() {
        if (!Utils.isFromPlayStore(this))
            return;
        BoolioGeneralClient.api().getAndroidVersion(BuildConfig.VERSION_CODE, new BoolioCallback<Integer>() {
            @Override
            public void handle(Integer object) {
                if (BuildConfig.VERSION_CODE < object) {
                    Dialogs.messageDialog(MainActivity.this, R.string.update_title, R.string.update_message, new Runnable() {
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
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventTracker.getInstance(this).flush();
    }
}
