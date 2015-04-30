package io.boolio.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.soundcloud.android.crop.Crop;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.animation.AnimationHelper;
import io.boolio.android.custom.ScrollingListView;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.PictureHelper;

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
    ScrollingListView.ScrollChangeListener changeListener = new ScrollingListView.ScrollChangeListener() {
        @Override
        public void onScroll(boolean isScrollingUp) {
            showNavBar(isScrollingUp);
        }
    };

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureHelper.REQUEST_TAKE_PHOTO || requestCode == PictureHelper.REQUEST_PICK_PHOTO || requestCode == Crop.REQUEST_CROP) {
            fragmentList.get(2).onActivityResult(requestCode, resultCode, data);
        }
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

    private void setupNavigationBar() {
        // Initially Selected
        View feedButton = navBar.findViewById(R.id.nav_bar_feed);
        curNavButton = feedButton;
        feedButton.setAlpha(1f);

        navBarAdd.setOnClickListener(getNavClickListener(2, new Runnable() {
            @Override
            public void run() {
                navBar.setAlpha(1f);
            }
        }));

        feedButton.setOnClickListener(getNavClickListener(0, null));
        navBar.findViewById(R.id.nav_bar_profile).setOnClickListener(getNavClickListener(1, null));
        navBar.findViewById(R.id.nav_bar_search).setOnClickListener(getNavClickListener(3, null));
        navBar.findViewById(R.id.nav_bar_category).setOnClickListener(getNavClickListener(4, null));

    }

    private void setupViewPager() {
        fragmentList = new ArrayList<BoolioFragment>() {{
            add(FeedFragment.getInstance(changeListener));
            add(ProfileFragment.newInstance(BoolioUserHandler.getInstance(activity).getUser().userId, changeListener));
            add(CreateQuestionFragment.newInstance(new Runnable() {
                @Override
                public void run() {
                    navBar.getChildAt(0).callOnClick();
                }
            }));
            add(SearchFragment.getInstance(changeListener));
            add(CategoriesFragment.getInstance());
        }};

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (navBar != null) {
                    selectNavBar(navBar.getChildAt(position * 2));
                    fragmentList.get(position).refreshPage();
                    if (viewPager.getCurrentItem() == 2) {
                        showNavBar(true);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

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


    private View.OnClickListener getNavClickListener(final int index, final Runnable callback) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == curNavButton) {
                    return;
                }

                viewPager.setCurrentItem(index, true);

                if (callback != null) {
                    callback.run();
                }
            }
        };
    }

    private void selectNavBar(View v) {
        v.setAlpha(1f);
        if (curNavButton != navBarAdd) {
            curNavButton.setAlpha(selectedAlpha);
        }
        curNavButton = v;
    }

    public void showNavBar(boolean visible) {
        if (navBar == null)
            return;
        if (visible) {
            AnimationHelper.getInstance(activity).animateViewBottomIn(navBar);
            AnimationHelper.getInstance(activity).animateViewBottomIn(navBarAdd);
            Log.v("debugdebug", "boolean "+ visible + " state " + navBar.getVisibility());
        } else {
            AnimationHelper.getInstance(activity).animateViewBottomOut(navBar);
            AnimationHelper.getInstance(activity).animateViewBottomOut(navBarAdd);
            Log.v("debugdebug", "boolean "+ visible + " state " + navBar.getVisibility());

        }
    }
}
