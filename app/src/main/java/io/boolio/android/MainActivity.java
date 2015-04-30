package io.boolio.android;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.facebook.Profile;

import io.boolio.android.fragments.MainFragment;
import io.boolio.android.fragments.tutorials.TutorialPagerFragment;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.FacebookAuth;
import io.boolio.android.models.User;
import io.boolio.android.network.BoolioServer;
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

        // Get Screen Size
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;
    }

    @Override
    public void loggedIn(Profile profile) {
        NetworkCallback<User> userCallback = new NetworkCallback<User>() {
            @Override
            public void handle(User object) {
                BoolioUserHandler.getInstance(MainActivity.this).setUser(object);
                fragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit();
            }
        };

        User user = new User(profile.getId(), profile.getName());
        BoolioUserHandler.getInstance(this).setUser(user);
        BoolioServer.getInstance(this).getBoolioUserFromFacebook
                (UserParser.getInstance().toJSON(user), userCallback);
    }

    @Override
    public void loggedOut() {
        fragmentManager.beginTransaction().replace(R.id.container, TutorialPagerFragment.newInstance()).commit();
    }
}
