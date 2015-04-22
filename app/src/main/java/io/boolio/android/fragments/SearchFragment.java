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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.adapters.QuestionAdapter;
import io.boolio.android.callbacks.QuestionsCallback;
import io.boolio.android.callbacks.QuestionsPullInterface;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.models.Question;
import io.boolio.android.network.BoolioServer;

/**
 * Created by james on 4/21/15.
 */
public class SearchFragment extends BoolioFragment {
    static SearchFragment instance;
    Context context;

    EditText searchBar;
    ViewPager viewPager;
    TextView questionsTab, friendsTab, catergoriesTab;

    List<BoolioListFragment> fragmentList;

    QuestionAdapter questionsTabAdapter, friendsTabAdapter, catergoriesTabAdapter;
    RelativeLayout relativeLayout;

    public static SearchFragment getInstance(){
        if (instance == null){
            instance = new SearchFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        searchBar = (EditText) rootView.findViewById(R.id.search_bar);
        viewPager = (ViewPager) rootView.findViewById(R.id.search_view_pager);
        questionsTab = (TextView) rootView.findViewById(R.id.search_questions_tab);
        friendsTab = (TextView) rootView.findViewById(R.id.search_friends_tab);
        catergoriesTab = (TextView) rootView.findViewById(R.id.search_categories_tab);

        relativeLayout = (RelativeLayout)rootView.findViewById(R.id.search_rellayout);

        hideKeyBoard();
        setupPager();

        return rootView;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    private void hideKeyBoard(){
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(v==relativeLayout){
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }
    private void setupPager() {
        questionsTab.setAlpha(1f);
        friendsTab.setAlpha(0.25f);
        catergoriesTab.setAlpha(0.25f);
        questionsTabAdapter = new QuestionAdapter(context);
        friendsTabAdapter = new QuestionAdapter(context);
        catergoriesTabAdapter = new QuestionAdapter(context);
        fragmentList = new ArrayList<BoolioListFragment>() {{
            add(BoolioListFragment.newInstance(questionsTabAdapter, new QuestionsPullInterface() {
                @Override
                public void pullQuestions() {
//                    BoolioServer.getInstance(context).getUserAsked(
//                            BoolioUserHandler.getInstance(context).getUser().userId,
//                            new QuestionsCallback() {
//                                @Override
//                                public void handleQuestions(List<Question> questionList) {
//                                    askedAdapter.clear();
//                                    askedAdapter.addAll(questionList);
//
//                                }
//                            }
//                    );
                }
            }, runnable));


            add(BoolioListFragment.newInstance(friendsTabAdapter, new QuestionsPullInterface() {
                @Override
                public void pullQuestions() {
//                    BoolioServer.getInstance(context).getUserAnswered(
//                            BoolioUserHandler.getInstance(context).getUser().userId,
//                            new QuestionsCallback() {
//                                @Override
//                                public void handleQuestions(List<Question> questionList) {
//                                    answeredAdapter.clear();
//                                    answeredAdapter.addAll(questionList);
//                                }
//                            }
//                    );
                }
            }, runnable));
            add(BoolioListFragment.newInstance(catergoriesTabAdapter, new QuestionsPullInterface() {
                @Override
                public void pullQuestions() {
//                    BoolioServer.getInstance(context).getUserAsked(
//                            BoolioUserHandler.getInstance(context).getUser().userId,
//                            new QuestionsCallback() {
//                                @Override
//                                public void handleQuestions(List<Question> questionList) {
//                                    askedAdapter.clear();
//                                    askedAdapter.addAll(questionList);
//
//                                }
//                            }
//                    );
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
                    questionsTab.setAlpha(1f);
                    friendsTab.setAlpha(.25f);
                    catergoriesTab.setAlpha(.25f);
                } else if(position == 1) {
                    questionsTab.setAlpha(.25f);
                    friendsTab.setAlpha(1f);
                    catergoriesTab.setAlpha(.25f);
                } else {
                    questionsTab.setAlpha(.25f);
                    friendsTab.setAlpha(.25f);
                    catergoriesTab.setAlpha(1f);
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
}
