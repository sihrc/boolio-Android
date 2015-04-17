package io.boolio.android.fragments.tutorials;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.fragments.BoolioFragment;
import io.boolio.android.fragments.FeedFragment;
import io.boolio.android.fragments.LoginFragment;

/**
 * Created by Chris on 4/15/15.
 */
public class TutorialPagerFragment extends BoolioFragment {
    Context context;

    ViewPager pager;
    LinearLayout inactiveIndicators, activeIndicators;
    List<BoolioFragment> fragmentList;
    Drawable inactiveIndicator, activeIndicator;

    public static TutorialPagerFragment newInstance() {
        TutorialPagerFragment tutorialPagerFragment = new TutorialPagerFragment();
        tutorialPagerFragment.setTutorials(
                // Tutorial I
                R.drawable.logo, R.string.tutorial_1,

                // Tutorial II
                R.drawable.logo, R.string.tutorial_1,

                // Tutorial III
                R.drawable.logo, R.string.tutorial_1,

                // Tutorial IV
                R.drawable.logo, R.string.tutorial_1
        );
        return tutorialPagerFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        inactiveIndicator = getResources().getDrawable(R.drawable.inactive_indicator);
        activeIndicator = getResources().getDrawable(R.drawable.active_indicator);
    }

    // Set the Tutorial Fragments
    private void setTutorials(Integer... args) {
        fragmentList = new ArrayList<>();
        int total = args.length, index= 0;
        while (index < total)
            fragmentList.add(TutorialFragment.newInstance(args[index++], args[index++]));
        fragmentList.add(LoginFragment.newInstance());
        fragmentList.add(FeedFragment.getInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial_pager, container, false);

        activeIndicators = (LinearLayout) view.findViewById(R.id.pager_active_indicator);
        inactiveIndicators = (LinearLayout) view.findViewById(R.id.pager_inactive_indicator);
        setupIndicators();


        pager = (ViewPager) view.findViewById(R.id.fragment_tutorial_view_pager);
        setupViewPager();

        return view;
    }

    private void setupIndicators() {
        // Add Page Indicators
        View singleIndicator;
        for (int i = 0; i < fragmentList.size(); i++) {
            // Add Active Indicators
            singleIndicator = new ImageView(context);
            singleIndicator.setBackground(activeIndicator);
            singleIndicator.setAlpha(0);
            activeIndicators.addView(singleIndicator);

            // Add inactive Indicators to Linear Layout
            singleIndicator = new ImageView(context);
            singleIndicator.setBackground(inactiveIndicator);
            inactiveIndicators.addView(singleIndicator);
        }
        activeIndicators.getChildAt(0).setAlpha(1f);
    }

    private void setupViewPager() {
        pager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int prev, curr, next;
            boolean dragging;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                curr = position;

                if (!dragging) {
                    return;
                }
                boolean forward = positionOffset > 0; positionOffset = Math.abs(positionOffset)/.5f;
                next = forward ? position + 1 : position - 1;
                activeIndicators.getChildAt(position).setAlpha(1 - positionOffset);
                View view = activeIndicators.getChildAt(next);
                if (view != null)
                    view.setAlpha(positionOffset/2f);

            }
            @Override
            public void onPageSelected(int position) {
                // Switch Page Indicators
                activeIndicators.getChildAt(position).setAlpha(1f);
                activeIndicators.getChildAt(prev).setAlpha(0f);
                prev = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                dragging = state == ViewPager.SCROLL_STATE_DRAGGING;
                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    View current = activeIndicators.getChildAt(curr);
                    Animation animation = new AlphaAnimation(current.getAlpha(), 1f);
                    animation.setDuration(300);
                    current.startAnimation(animation);
                    current.setAlpha(1f);

                    View previous = activeIndicators.getChildAt(next);
                    animation = new AlphaAnimation(current.getAlpha(), 1f);
                    animation.setDuration(300);
                    previous.startAnimation(animation);
                    previous.setAlpha(0f);
                }
            }
        });
    }
}
