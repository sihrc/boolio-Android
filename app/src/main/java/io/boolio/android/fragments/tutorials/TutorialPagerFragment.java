package io.boolio.android.fragments.tutorials;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import io.boolio.android.R;
import io.boolio.android.fragments.BoolioFragment;
import io.boolio.android.fragments.LoginFragment;

/**
 * Created by Chris on 4/15/15.
 */
public class TutorialPagerFragment extends BoolioFragment {
    Context context;
    @Bind(R.id.pager_active_indicator) LinearLayout activeIndicators;
    @Bind(R.id.pager_inactive_indicator) LinearLayout inactiveIndicators;
    @Bind(R.id.fragment_tutorial_view_pager) ViewPager pager;

    List<BoolioFragment> fragmentList;
    Drawable inactiveIndicator, activeIndicator;
    int[] args;

    public static TutorialPagerFragment newInstance() {
        TutorialPagerFragment tutorialPagerFragment = new TutorialPagerFragment();
        tutorialPagerFragment.setTutorials(new int[]{
            // Tutorial I
            R.drawable.heartbear, R.string.tutorial_1,

            // Tutorial II
            R.drawable.map, R.string.tutorial_2,

            // Tutorial III
            R.drawable.burgerphone, R.string.tutorial_3
        });
        return tutorialPagerFragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("tutorials", args);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null)
            return;
        args = savedInstanceState.getIntArray("tutorials");
        setTutorials(args);
    }

    // Set the Tutorial Fragments
    private void setTutorials(int[] args) {
        this.args = args;
        fragmentList = new ArrayList<>();
        int total = args.length, index = 0;
        while (index < total)
            fragmentList.add(TutorialFragment.newInstance(args[index++], args[index++]));
        fragmentList.add(LoginFragment.newInstance());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        inactiveIndicator = ResourcesCompat.getDrawable(getResources(), R.drawable.inactive_indicator, activity.getTheme());
        activeIndicator = ResourcesCompat.getDrawable(getResources(), R.drawable.active_indicator, activity.getTheme());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutorial_pager, container, false);
        ButterKnife.bind(this, rootView);

        setupIndicators();
        setupViewPager();

        return rootView;
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
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int prev,
                curr,
                next;
            boolean dragging;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                curr = position;

                if (!dragging) {
                    return;
                }
                boolean forward = positionOffset > 0;
                positionOffset = Math.abs(positionOffset) / .5f;
                next = forward ? position + 1 : position - 1;
                activeIndicators.getChildAt(position).setAlpha(1 - positionOffset);
                View view = activeIndicators.getChildAt(next);
                if (view != null)
                    view.setAlpha(positionOffset / 2f);

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
