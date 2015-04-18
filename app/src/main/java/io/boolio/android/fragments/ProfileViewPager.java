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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;

/**
 * Created by james on 4/18/15.
 */
public class ProfileViewPager extends BoolioFragment{
    static ProfileViewPager instance;
    Context context;
    List<BoolioFragment> fragmentList;
    ViewPager pager;
    TextView askedView, answerView;

    public static ProfileViewPager getInstance(){
        if(instance == null){
            instance = new ProfileViewPager();
            instance.setProfilePager();
        }
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
        answerView.setAlpha(0.2f);

        setUpViewPager();

        return rootView;
    }
    private void setProfilePager(){
        fragmentList = new ArrayList<>();
        fragmentList.add(ProfileAskedFragment.getInstance());
        fragmentList.add(ProfileAnsweredFragment.getInstance());
    }

    private void setUpViewPager(){
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
            boolean forward, dragging ;
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
                if(position == 0) {
                    askedView.setAlpha(1f);
                    answerView.setAlpha(.1f);
                } else{
                    askedView.setAlpha(.1f);
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
