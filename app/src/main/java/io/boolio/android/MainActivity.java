package io.boolio.android;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import io.boolio.android.auth.AuthActivity;
import io.boolio.android.fragments.BoolioFragment;
import io.boolio.android.fragments.CreateQuestionFragment;
import io.boolio.android.fragments.FeedFragment;
import io.boolio.android.fragments.ProfileFragment;
import io.boolio.android.fragments.tutorials.TutorialPagerFragment;
import io.boolio.android.network.BoolioServer;


public class MainActivity extends AuthActivity {
    final static float selectedAlpha = .5f;
    public static int SCREEN_WIDTH, SCREEN_HEIGHT;
    FragmentManager fragmentManager;
    BoolioFragment boolioFragment;
    View curNavButton;
    View navBar;
    View navBarAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    @Override
    protected void postLoginCallback() {
        showNavigationbar(true);
        switchFragment(FeedFragment.getInstance());
    }

    @Override
    protected void postLogoutCallback() {
        showNavigationbar(false);
        switchFragment(TutorialPagerFragment.newInstance());
    }

    public void showNavigationbar(boolean show) {
        if (navBar == null) {
            navBar = findViewById(R.id.nav_bar);
            navBarAdd = findViewById(R.id.nav_bar_add);
        }
        navBar.setVisibility(show ? View.VISIBLE: View.GONE);
        navBarAdd.setVisibility(show ? View.VISIBLE: View.GONE);
    }

    private void setupNavigationBar() {
        View navBar = findViewById(R.id.nav_bar);
        View feedButton = navBar.findViewById(R.id.nav_bar_feed);

        // Initially Selected
        curNavButton = feedButton;
        curNavButton.setAlpha(1f);

        navBarAdd = findViewById(R.id.nav_bar_add);
        navBarAdd.setOnClickListener(getNavClickListener(CreateQuestionFragment.getInstance(), null));
        feedButton.setOnClickListener(getNavClickListener(FeedFragment.getInstance(), null));
        navBar.findViewById(R.id.nav_bar_search).setOnClickListener(getNavClickListener(null, null));
        navBar.findViewById(R.id.nav_bar_category).setOnClickListener(getNavClickListener(null, null));
        navBar.findViewById(R.id.nav_bar_profile).setOnClickListener(getNavClickListener(
                ProfileFragment.newInstance(null), null));
    }

    private View.OnClickListener getNavClickListener(final BoolioFragment fragment, final Runnable callback) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == curNavButton) {
                    return;
                }
                selectNavBar(v);
                if (fragment != null) {
                    switchFragment(fragment);
                    fragment.onResume();
                }

                if (callback != null)
                    callback.run();
            }
        };
    }

    private void selectNavBar(View v) {
        v.setAlpha(1f);
        curNavButton.setAlpha(selectedAlpha);
        curNavButton = v;
    }

    public void switchFragment(BoolioFragment fragment) {
        boolioFragment = fragment;
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }
}
