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
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }
}
