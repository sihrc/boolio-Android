package io.boolio.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.animation.AnimationHelper;
import io.boolio.android.custom.ScrollingListView;

/**
 * Created by Chris on 4/28/15.
 */
public class MainFragment extends BoolioFragment {
    final static float selectedAlpha = .5f;

    // Nav-bar views
    LinearLayout navBar;
    View curNavButton, navBarAdd;
    ViewPager viewPager;

    List<BoolioFragment> fragmentList;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        navBar = (LinearLayout) rootView.findViewById(R.id.nav_bar);
        navBarAdd = rootView.findViewById(R.id.nav_bar_add);

        viewPager = (ViewPager) rootView.findViewById(R.id.main_view_pager);

        setupNavigationBar();
        setupViewPager();

        return rootView;
    }

    ScrollingListView.ScrollChangeListener changeListener = new ScrollingListView.ScrollChangeListener() {
        @Override
        public void onScroll(boolean isScrollingUp) {
            showNavBar(isScrollingUp);
        }
    };

    private void setupViewPager() {
        fragmentList = new ArrayList<BoolioFragment>() {{
            add(FeedFragment.getInstance(changeListener));
            add(ProfileFragment.newInstance(null, changeListener));
            add(CategoriesFragment.getInstance());
            add(SearchFragment.getInstance(changeListener));
        }};

        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
    }


    public void showNavBar(boolean visible) {
        if (navBar == null)
            return;
        if (visible) {
            AnimationHelper.getInstance(activity).animateViewBottomIn(navBar);
            AnimationHelper.getInstance(activity).animateViewBottomIn(navBarAdd);
        } else {
            AnimationHelper.getInstance(activity).animateViewBottomOut(navBar);
            AnimationHelper.getInstance(activity).animateViewBottomOut(navBarAdd);
        }
    }

    private void setupNavigationBar() {
        // Initially Selected
        curNavButton = navBar.getChildAt(0);
        curNavButton.setAlpha(1f);

        navBarAdd.setOnClickListener(getNavClickListener(3, new Runnable() {
            @Override
            public void run() {
                navBar.setAlpha(1f);
            }
        }));
        navBar.findViewById(R.id.nav_bar_feed).setOnClickListener(getNavClickListener(0, null));
        navBar.findViewById(R.id.nav_bar_search).setOnClickListener(getNavClickListener(1, null));
        navBar.findViewById(R.id.nav_bar_category).setOnClickListener(getNavClickListener(2, null));
        navBar.findViewById(R.id.nav_bar_profile).setOnClickListener(getNavClickListener(3, null));
    }

    private View.OnClickListener getNavClickListener(final int index, final Runnable callback) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == curNavButton) {
                    return;
                }

                selectNavBar(v);
                viewPager.setCurrentItem(index, true);

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
}
