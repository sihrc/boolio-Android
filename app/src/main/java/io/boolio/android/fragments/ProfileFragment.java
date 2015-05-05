package io.boolio.android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import io.boolio.android.callbacks.QuestionsCallback;
import io.boolio.android.custom.BoolioProfileImage;
import io.boolio.android.custom.ScrollingListView;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.models.Question;
import io.boolio.android.models.User;
import io.boolio.android.network.ServerQuestion;
import io.boolio.android.network.ServerUser;
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
    View profileSetting;

    // List Fragment Pager
    ViewPager viewPager;
    TextView answerView, askedView;
    BoolioAnswerAdapter askedAdapter, answeredAdapter;
    List<BoolioListFragment> fragmentList;
    ScrollingListView.ScrollChangeListener scrollChangeListener;

    int white_color, dark_gray, theme_blue, orange;

    // Question Request Callbacks
    QuestionsCallback askedCallback = new QuestionsCallback() {
        @Override
        public void handleQuestions(List<Question> questionList) {
            askedAdapter.clear();
            askedAdapter.addAll(questionList);
            askedAdapter.notifyDataSetChanged();
        }
    };
    QuestionsCallback answeredCallback = new QuestionsCallback() {
        @Override
        public void handleQuestions(List<Question> questionList) {
            answeredAdapter.clear();
            answeredAdapter.addAll(questionList);
            answeredAdapter.notifyDataSetChanged();
        }
    };

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
        white_color = getResources().getColor(R.color.white);
        dark_gray = getResources().getColor(R.color.tab_light_gray);
        theme_blue = getResources().getColor(R.color.theme_blue);
        orange = getResources().getColor(R.color.feed_question_right_color);
        ServerUser.getInstance(context).getUserProfile(
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
    public void refreshPage() {
        ServerUser.getInstance(context).getUserProfile(
                userId == null ? BoolioUserHandler.getInstance(context).getUser().userId : userId,
                new NetworkCallback<User>() {
                    @Override
                    public void handle(User user) {
                        ProfileFragment.this.user = user;
                        updateViews();
                        ServerQuestion.getInstance(context).getQuestions(user.questionsAnswered, answeredCallback);
                        ServerQuestion.getInstance(context).getQuestions(user.questionsAsked, askedCallback);
                    }
                });
        hideKeyBoard(profileUsername);
    }

    /**
     * Update Views with User Information once populated *
     */
    public void updateViews() {
        if (user == null)
            return;
        profileUserImage.setImageUrl(user.profilePic, ServerUser.getInstance(context).getImageLoader());
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

        setupPager();
        setupTabOnClick();
        setupKarmaView();
        setupLogout();

        return rootView;
    }

    private void setupPager() {
        // profile
        askedAdapter = new BoolioAnswerAdapter(context);
        answeredAdapter = new BoolioAnswerAdapter(context);
        fragmentList = new ArrayList<BoolioListFragment>() {{
            add(BoolioListFragment.newInstance(askedAdapter, scrollChangeListener));
            add(BoolioListFragment.newInstance(answeredAdapter, scrollChangeListener));
        }};


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    answerView.setBackgroundColor(dark_gray);
                    askedView.setBackgroundColor(white_color);
                    answerView.setTextColor(white_color);
                    askedView.setTextColor(orange);
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
                    AnimationHelper.getInstance(context).animateViewLeftIn(karmaShow);
                else
                    AnimationHelper.getInstance(context).animateViewRightOut(karmaShow);
            }
        });
    }

    private void setupLogout() {
        // Settings Button
        profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.settings)
                        .setItems(new CharSequence[]{"Logout", "Cancel"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show();
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
}
