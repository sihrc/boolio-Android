package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.adapters.ContactsAdapter;

/**
 * Created by filippos on 4/23/15.
 */
public class FriendsFragment extends BoolioFragment {
    static FriendsFragment instance;
    Context context;

    ViewPager friendsViewPager;
    TextView contactsTab, facebookTab;

    List<BoolioListFragment> fragmentList;
    ContactsAdapter contactsAdapter;

    public static FriendsFragment getInstance() {
        if (instance == null) {
            instance = new FriendsFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsViewPager = (ViewPager) rootView.findViewById(R.id.friends_view_pager);
        contactsTab = (TextView) rootView.findViewById(R.id.friend_contacts);
        facebookTab = (TextView) rootView.findViewById(R.id.friend_facebook);

        setupPager();

        return rootView;
    }

    private void setupPager() {
        contactsTab.setAlpha(1f);
        facebookTab.setAlpha(0.25f);

        contactsAdapter = new ContactsAdapter(activity, R.layout.item_contacts) {
            @Override
            public void handleContactClick(String number) {
                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setData(Uri.parse("sms:" + number));
                smsIntent.putExtra("sms_body", "Hey! I'm on boolio. You should join!\n https://boolio.io");
                FriendsFragment.this.startActivityForResult(smsIntent, 9);
            }
        };

        fragmentList = new ArrayList<BoolioListFragment>() {
            {
                add(BoolioListFragment.newInstance(contactsAdapter, null));
                add(ComingSoonFragment.newInstance("Facebook Friends coming soon!"));
            }
        };

        friendsViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    contactsTab.setAlpha(1f);
                    facebookTab.setAlpha(.25f);
                } else {
                    contactsTab.setAlpha(.25f);
                    facebookTab.setAlpha(1f);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        friendsViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
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
}