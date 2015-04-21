package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.callbacks.ScrollViewCallback;

/**
 * Created by james on 4/18/15.
 */
public class ProfileViewPager extends BoolioFragment {
    static ProfileViewPager instance;
    Context context;
    List<BoolioFragment> fragmentList;
    ViewPager pager;
    TextView askedView, answerView;
    ScrollViewCallback scrollViewCallback;

    public void setCallback(ScrollViewCallback scrollViewCallback) {
        this.scrollViewCallback = scrollViewCallback;
    }

    public static ProfileViewPager getInstance() {
//        if (instance == null) {
        Log.i("DebugDebug", "here2");
            instance = new ProfileViewPager();
            instance.setProfilePager();
//        }
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_pager, container, false);


        pager = (ViewPager) rootView.findViewById(R.id.profile_view_pager);
        askedView = (TextView) rootView.findViewById(R.id.profile_asked_view);
        answerView = (TextView) rootView.findViewById(R.id.profile_answered_view);

        askedView.setAlpha(1f);
        answerView.setAlpha(0.25f);

        setUpViewPager();

        return rootView;
    }

    private void setProfilePager() {
        fragmentList = new ArrayList<>();

        // set the callbacks for each fragment
        ProfileAskedFragment fragment1 = ProfileAskedFragment.getInstance();
        fragment1.setCallback(scrollViewCallback);
        ProfileAnsweredFragment fragment2 = ProfileAnsweredFragment.getInstance();
        fragment2.setCallback(scrollViewCallback);
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        Log.i("DebugDebug", "Here");
    }

    private void setUpViewPager() {
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
            boolean forward
                    ,
                    dragging;
            int curr;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                curr = position;

                if (!dragging) {
                    return;
                }
//                forward = positionOffset > 0;
//                Log.v("debugdebug", "stuff "+forward+ " " + positionOffset);
//                if(forward && position == 0){
//                    Log.v("debugdebug", "forward from 0"+Integer.toString(curr));
//                    askedView.setAlpha(positionOffset-1);
//                    answerView.setAlpha(1-positionOffset);
//                } else if (!forward && position == 1){
//                    Log.v("debugdebug", "backward from 1"+Integer.toString(curr));
//
//                    askedView.setAlpha(1-positionOffset);
//                    answerView.setAlpha(positionOffset -1);
//                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    askedView.setAlpha(1f);
                    answerView.setAlpha(.25f);
                } else {
                    askedView.setAlpha(.25f);
                    answerView.setAlpha(1f);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                dragging = state == ViewPager.SCROLL_STATE_DRAGGING;
//                if (state == ViewPager.SCROLL_STATE_SETTLING) {
//                    if(curr == 0 && forward){
//                        Animation animation = new AlphaAnimation(askedView.getAlpha(), 1f);
//                        animation.setDuration(300);
//                        askedView.startAnimation(animation);
//                        askedView.setAlpha(1f);
//                        animation = new AlphaAnimation(askedView.getAlpha(), 1f);
//                        animation.setDuration(300);
//                        answerView.startAnimation(animation);
//                        answerView.setAlpha(.1f);
//
//                    } else if (curr == 1 && !forward){
//                        Animation animation = new AlphaAnimation(answerView.getAlpha(), 1f);
//                        animation.setDuration(300);
//                        answerView.startAnimation(animation);
//                        answerView.setAlpha(1f);
//
//                        animation = new AlphaAnimation(answerView.getAlpha(), 1f);
//                        animation.setDuration(300);
//                        askedView.startAnimation(animation);
//                        askedView.setAlpha(.1f);
//                    }
//                }
            }
        });

    }
}
