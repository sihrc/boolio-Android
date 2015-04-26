package io.boolio.android;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import io.boolio.android.animation.AnimationHelper;
import io.boolio.android.auth.AuthActivity;
import io.boolio.android.fragments.BoolioFragment;
import io.boolio.android.fragments.CategoriesFragment;
import io.boolio.android.fragments.CreateQuestionFragment;
import io.boolio.android.fragments.FeedFragment;
import io.boolio.android.fragments.ProfileFragment;
import io.boolio.android.fragments.SearchFragment;
import io.boolio.android.fragments.tutorials.TutorialPagerFragment;


public class MainActivity extends AuthActivity {
    final static float selectedAlpha = .5f;
    public static int SCREEN_WIDTH, SCREEN_HEIGHT;
    FragmentManager fragmentManager;
    BoolioFragment boolioFragment;
    View curNavButton;
    LinearLayout navBar;
    View navBarAdd;

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

    @Override
    protected void postLoginCallback() {
        showNavBar(true);
        if (boolioFragment == null || boolioFragment.getClass() == TutorialPagerFragment.class)
            switchFragment(FeedFragment.getInstance());
        else if (boolioFragment == FeedFragment.getInstance())
            FeedFragment.getInstance().onResume();
        else
            boolioFragment.onResume();
    }

    @Override
    protected void postLogoutCallback() {
        showNavBar(false);
        switchFragment(TutorialPagerFragment.newInstance());
    }

    public void showNavBar(boolean visible) {
        if (navBar == null)
            return;
        if (visible) {
            AnimationHelper.getInstance(this).animateViewBottomIn(navBar);
            AnimationHelper.getInstance(this).animateViewBottomIn(navBarAdd);
        } else {
            AnimationHelper.getInstance(this).animateViewBottomOut(navBar);
            AnimationHelper.getInstance(this).animateViewBottomOut(navBarAdd);
        }
    }

    private void setupNavigationBar() {
        navBar = (LinearLayout) findViewById(R.id.nav_bar);
        View feedButton = navBar.findViewById(R.id.nav_bar_feed);

        // Initially Selected
        curNavButton = feedButton;
        curNavButton.setAlpha(1f);

        navBarAdd = findViewById(R.id.nav_bar_add);
        navBarAdd.setOnClickListener(getNavClickListener(CreateQuestionFragment.newInstance(), new Runnable() {
            @Override
            public void run() {
                navBar.setAlpha(1f);
            }
        }));
        feedButton.setOnClickListener(getNavClickListener(FeedFragment.getInstance(), null));
        navBar.findViewById(R.id.nav_bar_search).setOnClickListener(getNavClickListener(SearchFragment.getInstance(), null));
        navBar.findViewById(R.id.nav_bar_category).setOnClickListener(getNavClickListener(CategoriesFragment.getInstance(), null));
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
        if (curNavButton != navBarAdd)
            curNavButton.setAlpha(selectedAlpha);
        curNavButton = v;
    }

    private void switchFragment(BoolioFragment fragment) {
        boolioFragment = fragment;
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    public void switchFragment(int position) {
        if (position == 3)
            navBarAdd.callOnClick();
        else
            navBar.getChildAt(position).callOnClick();
    }
}
