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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.adapters.BoolioAnswerAdapter;
import io.boolio.android.animation.AnimationHelper;
import io.boolio.android.callbacks.QuestionsCallback;
import io.boolio.android.callbacks.QuestionsPullInterface;
import io.boolio.android.custom.BoolioProfileImage;
import io.boolio.android.custom.ScrollingListView;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.models.Question;
import io.boolio.android.models.User;
import io.boolio.android.network.BoolioServer;
import io.boolio.android.network.NetworkCallback;

/**
 * Created by Chris on 4/17/15.
 */
public class ProfileFragment extends BoolioFragment {
    Context context;
    String userId;
    User user;

    // Profile Page
    BoolioProfileImage profileUserImage;
    TextView askedCount, profileUsername, answeredCount, karmaCount, profileDisplayName, answeredCountIn, karmaCountIn, askedCountIn;

    LinearLayout karmaBar;
    RelativeLayout karmaShow;
    ImageView profileSetting;

    // List Fragment Pager
    ViewPager viewPager;
    View answerView, askedView;
    BoolioAnswerAdapter askedAdapter, answeredAdapter;
    List<BoolioListFragment> fragmentList;
    ScrollingListView.ScrollChangeListener scrollChangeListener;

    public static ProfileFragment newInstance(String userId, ScrollingListView.ScrollChangeListener scrollChangeListener) {
        ProfileFragment fragment = new ProfileFragment();

        fragment.userId = userId;
        fragment.scrollChangeListener = scrollChangeListener;

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        user = BoolioUserHandler.getInstance(activity).getUser();
        BoolioServer.getInstance(context).getUserProfile(
                userId == null ? BoolioUserHandler.getInstance(context).getUser().userId : userId,
                new NetworkCallback<User>() {
                    @Override
                    public void handle(User user) {
                        ProfileFragment.this.user = user;
                        updateViews();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileSetting = (ImageView) rootView.findViewById(R.id.profile_setting);
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
        askedView = rootView.findViewById(R.id.profile_asked_view);
        answerView = rootView.findViewById(R.id.profile_answered_view);

        setupPager();
        setupTabOnClick();
        setupKarmaView();

        return rootView;
    }

    private void setupPager() {
        askedView.setAlpha(1f);
        answerView.setAlpha(0.25f);
        // profile
        askedAdapter = new BoolioAnswerAdapter(context);
        answeredAdapter = new BoolioAnswerAdapter(context);
        fragmentList = new ArrayList<BoolioListFragment>() {{
            add(BoolioListFragment.newInstance(askedAdapter, new QuestionsPullInterface() {
                @Override
                public void pullQuestions() {
                    BoolioServer.getInstance(context).getUserAsked(
                            userId,
                            new QuestionsCallback() {
                                @Override
                                public void handleQuestions(List<Question> questionList) {
                                    askedAdapter.clear();
                                    askedAdapter.addAll(questionList);
                                }
                            }
                    );
                }
            }, scrollChangeListener));


            add(BoolioListFragment.newInstance(answeredAdapter, new QuestionsPullInterface() {
                @Override
                public void pullQuestions() {
                    BoolioServer.getInstance(context).getUserAnswered(
                            userId,
                            new QuestionsCallback() {
                                @Override
                                public void handleQuestions(List<Question> questionList) {
                                    answeredAdapter.clear();
                                    answeredAdapter.addAll(questionList);
                                }
                            }
                    );
                }
            }, scrollChangeListener));
        }};


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
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
                if (karmaShow.getVisibility() == View.VISIBLE) {
                    karmaShow.setVisibility(View.INVISIBLE);
                } else {
                    karmaShow.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Update Views with User Information once populated *
     */
    private void updateViews() {
        if (user == null)
            return;

        if (userId.equals(BoolioUserHandler.getInstance(context).getUser().userId)) {
            profileDisplayName.setText(R.string.my_profile_page);
        } else {
            profileDisplayName.setText(R.string.profile_page);
        }

        profileUserImage.setImageUrl(user.profilePic, BoolioServer.getInstance(context).getImageLoader());
        askedCount.setText(String.valueOf(user.questionsAsked.size()));
        answeredCount.setText(String.valueOf(user.questionsAnswered.size()));
        karmaCount.setText(String.valueOf(user.questionsAnswered.size() + user.questionsAsked.size())); //FIXME IMPLEMENT ON BACKEND
        askedCountIn.setText(String.valueOf(user.questionsAsked.size()));
        answeredCountIn.setText(String.valueOf(user.questionsAnswered.size()));
        karmaCountIn.setText(String.valueOf(user.questionsAnswered.size() + user.questionsAsked.size()));
        profileUsername.setText(user.name); // FIXME ADD USERNAME
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("debugdebug", "when is this called");

    }
}
