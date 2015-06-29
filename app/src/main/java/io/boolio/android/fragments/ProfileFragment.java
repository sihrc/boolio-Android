package io.boolio.android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.adapters.BoolioAnswerAdapter;
import io.boolio.android.animation.AnimationHelper;
import io.boolio.android.custom.BoolioProfileImage;
import io.boolio.android.custom.ScrollingListView;
import io.boolio.android.gcm.GCMService;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.Glider;
import io.boolio.android.helpers.PrefsHelper;
import io.boolio.android.models.Question;
import io.boolio.android.models.User;
import io.boolio.android.network.clients.BoolioQuestionClient;
import io.boolio.android.network.clients.BoolioUserClient;
import io.boolio.android.network.helpers.BoolioCallback;
import io.boolio.android.network.BoolioData;

/**
 * Created by Chris on 4/17/15.
 * ProfileFragment contains user profile information and question feeds
 */
public class ProfileFragment extends BoolioFragment {
    public final static int ORDER = 1;

    String userId;
    User user;

    // Profile Page
    BoolioProfileImage profileUserImage;
    TextView askedCount, profileUsername, answeredCount, karmaCount, profileDisplayName, answeredCountIn, karmaCountIn, askedCountIn;

    LinearLayout karmaBar;
    RelativeLayout karmaShow;
    View profileSetting, gifLoading;

    // List Fragment Pager
    ViewPager viewPager;
    TextView answerView, askedView;
    BoolioAnswerAdapter askedAdapter, answeredAdapter;
    List<BoolioListFragment> fragmentList;

    int white_color, dark_gray, theme_blue;

    // Question Request Callbacks
    BoolioCallback<List<Question>> askedCallback = new BoolioCallback<List<Question>>() {
        @Override
        public void handle(List<Question> questionList) {
            askedAdapter.clear();
            askedAdapter.addAll(questionList);
            askedAdapter.notifyDataSetChanged();
            gifLoading.setVisibility(View.GONE);
        }
    };
    BoolioCallback<List<Question>> answeredCallback = new BoolioCallback<List<Question>>() {
        @Override
        public void handle(List<Question> questionList) {
            answeredAdapter.clear();
            answeredAdapter.addAll(questionList);
            answeredAdapter.notifyDataSetChanged();
            gifLoading.setVisibility(View.GONE);
        }
    };

    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.userId = userId;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        user = BoolioUserHandler.getInstance().getUser();
        white_color = getResources().getColor(R.color.white);
        dark_gray = getResources().getColor(R.color.tab_light_gray);
        theme_blue = getResources().getColor(R.color.darker_blue);
        BoolioUserClient.api().getUserProfile(
                userId == null ? PrefsHelper.getInstance().getString("_id") : userId,
                new BoolioCallback<User>() {
                    @Override
                    public void handle(User user) {
                        ProfileFragment.this.user = user;
                        updateViews();
                    }
                });
    }

    @Override
    public void refreshPage() {
        BoolioUserClient.api().getUserProfile(
                userId == null ? BoolioUserHandler.getInstance().getUserId() : userId,
                new BoolioCallback<User>() {
                    @Override
                    public void handle(User user) {
                        ProfileFragment.this.user = user;
                        updateViews();
                        gifLoading.setVisibility(View.VISIBLE);
                        BoolioQuestionClient.api().getQuestions(BoolioData.keys("ids").values(user.questionsAnswered), answeredCallback);
                        BoolioQuestionClient.api().getQuestions(BoolioData.keys("ids").values(user.questionsAsked), askedCallback);
                    }
                });
    }

    /**
     * Update Views with User Information once populated *
     */
    public void updateViews() {
        if (user == null)
            return;
        Glider.profile(profileUserImage, user.profilePic);
        askedCount.setText(String.valueOf(user.questionsAsked.size()));
        answeredCount.setText(String.valueOf(user.questionsAnswered.size()));
        karmaCount.setText(String.valueOf(user.questionsAnswered.size() + user.questionsAsked.size())); //FIXME IMPLEMENT ON BACKEND
        askedCountIn.setText(String.valueOf(user.questionsAsked.size()));
        answeredCountIn.setText(String.valueOf(user.questionsAnswered.size()));
        karmaCountIn.setText(String.valueOf(user.questionsAnswered.size() + user.questionsAsked.size()));
        profileUsername.setText(user.name); // FIXME ADD USERNAME
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileSetting = rootView.findViewById(R.id.profile_setting);
        profileUserImage = (BoolioProfileImage) rootView.findViewById(R.id.profile_user_image);

        karmaShow = (RelativeLayout) rootView.findViewById(R.id.karma_show);

        askedCount = (TextView) rootView.findViewById(R.id.asked_count);
        askedCountIn = (TextView) rootView.findViewById(R.id.asked_count_in); // duplicate
        profileUsername = (TextView) rootView.findViewById(R.id.profile_username);
        answeredCount = (TextView) rootView.findViewById(R.id.answered_count);
        answeredCountIn = (TextView) rootView.findViewById(R.id.answered_count_in); // duplicate
        profileDisplayName = (TextView) rootView.findViewById(R.id.profile_user_name);
        karmaBar = (LinearLayout) rootView.findViewById(R.id.karma_bar);


        karmaCount = (TextView) rootView.findViewById(R.id.karma_count);
        karmaCountIn = (TextView) rootView.findViewById(R.id.karma_count_in);  // duplicate

        viewPager = (ViewPager) rootView.findViewById(R.id.asked_answered_view_pager);
        askedView = (TextView) rootView.findViewById(R.id.profile_asked_view);
        answerView = (TextView) rootView.findViewById(R.id.profile_answered_view);

        gifLoading = rootView.findViewById(R.id.list_frag_gif_loading);

        setupPager();
        setupTabOnClick();
        setupKarmaView();
        setupLogout();

        return rootView;
    }

    private void setupPager() {
        // profile
        askedAdapter = new BoolioAnswerAdapter(activity);
        answeredAdapter = new BoolioAnswerAdapter(activity);
        fragmentList = new ArrayList<BoolioListFragment>() {{
            add(BoolioListFragment.newInstance(askedAdapter, new ScrollingListView.ScrollChangeListener() {
                @Override
                public void onScroll(boolean isScrollingUp) {
                    ((MainFragment) getParentFragment()).showNavBar(isScrollingUp);
                }
            }));
            add(BoolioListFragment.newInstance(answeredAdapter, new ScrollingListView.ScrollChangeListener() {
                @Override
                public void onScroll(boolean isScrollingUp) {
                    ((MainFragment) getParentFragment()).showNavBar(isScrollingUp);
                }
            }));
        }};


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    answerView.setBackgroundColor(dark_gray);
                    askedView.setBackgroundColor(white_color);
                    answerView.setTextColor(white_color);
                    askedView.setTextColor(theme_blue);
                } else {
                    askedView.setBackgroundColor(dark_gray);
                    answerView.setBackgroundColor(white_color);
                    askedView.setTextColor(white_color);
                    answerView.setTextColor(theme_blue);
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

    private void setupTabOnClick() {
        askedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        answerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
    }

    private void setupKarmaView() {
        karmaBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (karmaShow.getVisibility() == View.GONE)
                    AnimationHelper.getInstance(activity).animateViewLeftIn(karmaShow);
                else
                    AnimationHelper.getInstance(activity).animateViewRightOut(karmaShow);
            }
        });
    }

    private void setupLogout() {
        // Settings Button
        profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.settings)
                        .setItems(new CharSequence[]{"Logout", "Cancel"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Toast.makeText(activity, "Logged Out", Toast.LENGTH_SHORT).show();
                                        LoginManager.getInstance().logOut();
                                        dialog.dismiss();
                                        break;
                                    case 1:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        GCMService.clearAll(activity);
    }
}
