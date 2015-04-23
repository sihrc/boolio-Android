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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.MainActivity;
import io.boolio.android.R;
import io.boolio.android.adapters.QuestionAdapter;
import io.boolio.android.callbacks.QuestionsCallback;
import io.boolio.android.callbacks.QuestionsPullInterface;
import io.boolio.android.callbacks.ScrollViewListener;
import io.boolio.android.callbacks.UserCallback;
import io.boolio.android.custom.ObservableScrollView;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.models.Question;
import io.boolio.android.models.User;
import io.boolio.android.network.BoolioServer;
import io.boolio.android.views.BoolioProfileImage;

/**
 * Created by Chris on 4/17/15.
 */
public class ProfileFragment extends BoolioFragment {
    Context context;
    String userId;
    User user;

    // Layouts Sections
    ObservableScrollView scrollView;
    RelativeLayout headerBar;
    RelativeLayout movingFeed;

    // Profile Page
    ImageView profileSetting;
    BoolioProfileImage profileUserImage;
    TextView profileDisplayName, askedCount, profileUsername, answeredCount, karmaCount;

    // List Fragment Pager
    ViewPager viewPager;
    View answerView, askedView;
    QuestionAdapter askedAdapter, answeredAdapter;
    List<BoolioListFragment> fragmentList;
    int headerHeight;
    boolean movingFeedIsTop;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            scrollView.setEnabled(true);
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
        context = activity;
        BoolioServer.getInstance(context).getUserProfile(
                userId == null ? BoolioUserHandler.getInstance(context).getUser().userId : userId,
                new UserCallback() {
                    @Override
                    public void handleUser(User user) {
                        ProfileFragment.this.user = user;
                        updateViews();
                    }
                });
        headerHeight = (int) getResources().getDimension(R.dimen.header_height);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        scrollView = (ObservableScrollView) rootView.findViewById(R.id.profile_scroll_view);
        headerBar = (RelativeLayout) rootView.findViewById(R.id.header_bar);
        movingFeed = (RelativeLayout) rootView.findViewById(R.id.moving_feed);
        movingFeed.setLayoutParams(new LinearLayout.LayoutParams(MainActivity.SCREEN_WIDTH, MainActivity.SCREEN_HEIGHT));

        profileSetting = (ImageView) rootView.findViewById(R.id.profile_setting);
        profileUserImage = (BoolioProfileImage) rootView.findViewById(R.id.profile_user_image);

        askedCount = (TextView) rootView.findViewById(R.id.asked_count);
        profileUsername = (TextView) rootView.findViewById(R.id.profile_username);
        answeredCount = (TextView) rootView.findViewById(R.id.answered_count);
        profileDisplayName = (TextView) rootView.findViewById(R.id.profile_user_name);
        karmaCount = (TextView) rootView.findViewById(R.id.karma_count);

        viewPager = (ViewPager) rootView.findViewById(R.id.asked_answered_view_pager);
        askedView = rootView.findViewById(R.id.profile_asked_view);
        answerView = rootView.findViewById(R.id.profile_answered_view);

        setupScrolling();
        setupPager();
        setupViews();
        setupTabOnClick();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollView.requestFocus();
    }

    private void setupTabOnClick(){
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
    private void setupScrolling() {
        scrollView.setSmoothScrollingEnabled(true);
        scrollView.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldX, int oldY) {
                if (y - oldY >= 0 && y >= movingFeed.getY()) {
                    movingFeedIsTop = true;
                    scrollView.setScrollY((int) (movingFeed.getY()));
                    scrollView.setEnabled(false);
                }
            }
        });
    }

    private void setupPager() {
        askedView.setAlpha(1f);
        answerView.setAlpha(0.25f);
        askedAdapter = new QuestionAdapter(context);
        answeredAdapter = new QuestionAdapter(context);
        fragmentList = new ArrayList<BoolioListFragment>() {{
            add(BoolioListFragment.newInstance(askedAdapter, new QuestionsPullInterface() {
                @Override
                public void pullQuestions() {
                    BoolioServer.getInstance(context).getUserAsked(
                            BoolioUserHandler.getInstance(context).getUser().userId,
                            new QuestionsCallback() {
                                @Override
                                public void handleQuestions(List<Question> questionList) {
                                    askedAdapter.clear();
                                    askedAdapter.addAll(questionList);

                                }
                            }
                    );
                }
            }, runnable));


            add(BoolioListFragment.newInstance(answeredAdapter, new QuestionsPullInterface() {
                @Override
                public void pullQuestions() {
                    BoolioServer.getInstance(context).getUserAnswered(
                            BoolioUserHandler.getInstance(context).getUser().userId,
                            new QuestionsCallback() {
                                @Override
                                public void handleQuestions(List<Question> questionList) {
                                    answeredAdapter.clear();
                                    answeredAdapter.addAll(questionList);
                                }
                            }
                    );
                }
            }, runnable));
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

    private void setupViews() {
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
                                        BoolioUserHandler.getInstance(context).logout();
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

    /**
     * Update Views with User Information once populated *
     */
    private void updateViews() {
        if (user == null)
            return;
        if (user.userId.equals(BoolioUserHandler.getInstance(context).getUser().userId)) {
            profileSetting.setVisibility(View.VISIBLE);
            profileDisplayName.setText(R.string.my_profile_page);
        } else {
            profileSetting.setVisibility(View.GONE);
            profileDisplayName.setText(R.string.profile_page);
        }

        profileUserImage.setImageUrl(user.profilePic, BoolioServer.getInstance(context).getImageLoader());
        askedCount.setText(String.valueOf(user.questionsAsked.size()));
        answeredCount.setText(String.valueOf(user.questionsAnswered.size()));
        karmaCount.setText(String.valueOf(user.questionsAnswered.size() + user.questionsAsked.size())); //FIXME IMPLEMENT ON BACKEND
        profileUsername.setText(user.name); // FIXME ADD USERNAME
    }
}
