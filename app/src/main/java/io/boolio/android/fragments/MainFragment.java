package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.soundcloud.android.crop.Crop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.boolio.android.R;
import io.boolio.android.animation.AnimationHelper;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.PictureHelper;
import io.boolio.android.helpers.Utils;
import io.boolio.android.helpers.tracking.EventTracker;
import io.boolio.android.helpers.tracking.TrackEvent;

/**
 * Created by Chris on 4/28/15.
 */
public class MainFragment extends BoolioFragment {
    final static float selectedAlpha = .75f;

    // Nav-bar views
    @Bind(R.id.nav_bar) LinearLayout navBar;
    @Bind(R.id.nav_bar_add) View navBarAdd;
    @Bind(R.id.nav_bar_add_send) View navBarAddSend;
    @Bind(R.id.main_view_pager) ViewPager viewPager;

    List<BoolioFragment> fragmentList;
    View curNavButton;

    public static MainFragment newInstance() {
        return newInstance(FeedFragment.ORDER);
    }

    public static MainFragment newInstance(int startFrag) {
        MainFragment fragment = new MainFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("start", startFrag);

        fragment.setArguments(bundle);
        return fragment;
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
        ButterKnife.bind(this, rootView);

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(viewPager.getWindowToken(), 0);

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
        navBar.findViewById(R.id.nav_bar_friends).setOnClickListener(getNavClickListener(4, null));

    }

    private void setupViewPager() {
        // viewPager by default only loads the fragments that are right next to each other.
        // this makes sure that all of our fragments are loaded
        viewPager.setOffscreenPageLimit(fragmentList.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (navBar != null) {
                    selectNavBar(navBar.getChildAt(position * 2));
                    showNavBar(true);
                    fragmentList.get(position).refreshPage();
                    if (viewPager.getCurrentItem() == 2) {
                        showNavBar(true);
                        navBarAddSend.setVisibility(View.VISIBLE);
                        navBarAddSend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((CreateQuestionFragment) fragmentList.get(2)).submitOnClickSetup();
                            }
                        });

                    } else {
                        navBarAddSend.setVisibility(View.GONE);
                    }
                }
                Utils.hideKeyboard(activity);
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

        viewPager.setCurrentItem(getArguments().getInt("start"));
    }

    private View.OnClickListener getNavClickListener(final int index, final Runnable callback) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == curNavButton) {
                    return;
                }
                EventTracker.getInstance(activity).track(TrackEvent.BUTTON_NAVIGATE, new HashMap<String, Object>() {{
                    if (index < fragmentList.size())
                        put("fragment", fragmentList.get(index).getClass().getSimpleName().replace("Fragment", "").toLowerCase());
                }});
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
        if (navBar == null && activity != null)
            return;
        if (visible) {
            AnimationHelper.getInstance(activity).animateViewBottomIn(navBar);
            AnimationHelper.getInstance(activity).animateViewBottomIn(navBarAdd);
        } else {
            AnimationHelper.getInstance(activity).animateViewBottomOut(navBar);
            AnimationHelper.getInstance(activity).animateViewBottomOut(navBarAdd);

        }
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        fragmentList = new ArrayList<BoolioFragment>() {{
            add(FeedFragment.getInstance());
            // breaking here because userId is null
            add(ProfileFragment.newInstance(BoolioUserHandler.getInstance().getUserId()));
            add(CreateQuestionFragment.newInstance(new Runnable() {
                @Override
                public void run() {
                    navBar.getChildAt(0).callOnClick();
                }
            }));
            add(SearchFragment.getInstance());
            add(FriendsFragment.getInstance());
        }};
    }
}
