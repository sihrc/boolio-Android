package io.boolio.android;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import io.boolio.android.auth.AuthActivity;
import io.boolio.android.fragments.FeedFragment;
import io.boolio.android.fragments.tutorials.TutorialPagerFragment;
import io.boolio.android.helpers.BoolioUserHandler;


public class MainActivity extends AuthActivity {
    public static int SCREEN_WIDTH, SCREEN_HEIGHT;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            Fragment fragment = TutorialPagerFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, fragment.getClass().getName())
                    .commit();
        }

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;
    }

    public void switchFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    @Override
    protected void postLoginCallback() {
        switchFragment(FeedFragment.getInstance());
    }
}
