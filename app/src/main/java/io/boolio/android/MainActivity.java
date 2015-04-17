package io.boolio.android;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import io.boolio.android.auth.AuthActivity;
import io.boolio.android.fragments.BoolioFragment;
import io.boolio.android.fragments.FeedFragment;
import io.boolio.android.fragments.tutorials.TutorialPagerFragment;
import io.boolio.android.helpers.BoolioUserHandler;


public class MainActivity extends AuthActivity {
    public static int SCREEN_WIDTH, SCREEN_HEIGHT;
    final static float selectedAlpha = .5f;

    FragmentManager fragmentManager;
    View curNavButton;

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

        setupNavigationBar();
    }

    public void switchFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    @Override
    protected void postLoginCallback() {
        switchFragment(FeedFragment.getInstance());
    }

    private void setupNavigationBar() {
        View navBar = findViewById(R.id.nav_bar);

        View feedButton = navBar.findViewById(R.id.nav_bar_feed);

        // Initially Selected
        curNavButton = feedButton;
        curNavButton.setAlpha(selectedAlpha);

        feedButton.setOnClickListener(getNavClickListener(null, null));
        navBar.findViewById(R.id.nav_bar_search).setOnClickListener(getNavClickListener(null, null));
        navBar.findViewById(R.id.nav_bar_add).setOnClickListener(getNavClickListener(null, null));
        navBar.findViewById(R.id.nav_bar_category).setOnClickListener(getNavClickListener(null, null));
        navBar.findViewById(R.id.nav_bar_profile).setOnClickListener(getNavClickListener(null, null));
    }

    private View.OnClickListener getNavClickListener(final BoolioFragment fragment, final Runnable callback) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNavBar(v);
                if (fragment != null)
                    switchFragment(fragment);
                if (callback != null)
                    callback.run();
            }
        };
    }

    private void selectNavBar(View v) {
        v.setAlpha(selectedAlpha);
        curNavButton.setAlpha(1f);
        curNavButton = v;
    }
}
