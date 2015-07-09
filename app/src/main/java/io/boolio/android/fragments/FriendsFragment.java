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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.boolio.android.R;
import io.boolio.android.adapters.ContactsAdapter;

/**
 * Created by filippos on 4/23/15.
 */
public class FriendsFragment extends BoolioFragment {
    static FriendsFragment instance;
    Context context;

    @Bind(R.id.friends_view_pager) ViewPager friendsViewPager;
    @Bind(R.id.friend_contacts) TextView contactsTab;
    @Bind(R.id.friend_facebook) TextView facebookTab;

    List<BoolioFragment> fragmentList;
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
    public void refreshPage() {
        if (contactsTab != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(contactsTab.getApplicationWindowToken(), 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.bind(this, rootView);

        setupPager();

        return rootView;
    }

    @OnClick(R.id.friend_contacts)
    void onClickContacts() {
        friendsViewPager.setCurrentItem(0);
    }

    @OnClick(R.id.friend_facebook)
    void onClickFacebook() {
        friendsViewPager.setCurrentItem(1);
    }

    private void setupPager() {
        contactsTab.setAlpha(1f);
        facebookTab.setAlpha(0.25f);

        contactsAdapter = new ContactsAdapter(activity, R.layout.item_contacts) {
            @Override
            public void handleContactClick(String number) {
                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setData(Uri.parse("sms:" + number));
                smsIntent.putExtra("sms_body", "Hey! I'm on boolio. You should join!\n https://boolio.io/android");
                FriendsFragment.this.startActivityForResult(smsIntent, 9);
            }
        };

        fragmentList = new ArrayList<BoolioFragment>() {
            {
                add(BoolioListFragment.newInstance(contactsAdapter, null));
                add(ComingSoonFragment.newInstance("Facebook friends coming soon!"));
            }
        };

        friendsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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