package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.adapters.BoolioQuestionAdapter;
import io.boolio.android.callbacks.QuestionsCallback;
import io.boolio.android.callbacks.QuestionsPullInterface;
import io.boolio.android.custom.ScrollingListView;
import io.boolio.android.models.Question;
import io.boolio.android.network.BoolioServer;

/**
 * Created by james on 4/21/15.
 */
public class SearchFragment extends BoolioFragment {
    static SearchFragment instance;
    Context context;

    SearchView searchBar;
    ViewPager viewPager;
    TextView questionsTab, friendsTab, categoriesTab;

    List<BoolioListFragment> fragmentList;
    ScrollingListView.ScrollChangeListener scrollChangeListener;

    BoolioQuestionAdapter questionsTabAdapter, friendsTabAdapter, categoriesTabAdapter;

    public static SearchFragment getInstance(ScrollingListView.ScrollChangeListener scrollChangeListener) {
        if (instance == null) {
            instance = new SearchFragment();
            instance.scrollChangeListener = scrollChangeListener;
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

        searchBar = (SearchView) rootView.findViewById(R.id.search_bar);
        viewPager = (ViewPager) rootView.findViewById(R.id.search_view_pager);
        questionsTab = (TextView) rootView.findViewById(R.id.search_questions_tab);
        friendsTab = (TextView) rootView.findViewById(R.id.search_friends_tab);
        categoriesTab = (TextView) rootView.findViewById(R.id.search_categories_tab);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (viewPager.getCurrentItem() != 0)
                    return false;
                if (query.length() > 0) {
                    fragmentList.get(viewPager.getCurrentItem()).pullInterface.pullQuestions();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (viewPager.getCurrentItem() != 0)
                    return false;
                if (newText.length() > 0) {
                    fragmentList.get(viewPager.getCurrentItem()).pullInterface.pullQuestions();
                }
                return false;
            }
        });

        setupPager();
        setupTabOnClick();

        return rootView;
    }

    private void setupPager() {
        questionsTab.setAlpha(1f);
        friendsTab.setAlpha(0.25f);
        categoriesTab.setAlpha(0.25f);
        questionsTabAdapter = new BoolioQuestionAdapter(context);
        friendsTabAdapter = new BoolioQuestionAdapter(context);
        categoriesTabAdapter = new BoolioQuestionAdapter(context);
        fragmentList = new ArrayList<BoolioListFragment>() {
            {
                add(BoolioListFragment.newInstance(questionsTabAdapter, new QuestionsPullInterface() {
                    @Override
                    public void pullQuestions() {
                        if (searchBar.getQuery().toString().length() == 0)
                            return;
                        BoolioServer.getInstance(context).searchQuestion(
                                searchBar.getQuery().toString(),
                                new QuestionsCallback() {
                                    @Override
                                    public void handleQuestions(List<Question> questionList) {
                                        questionsTabAdapter.clear();
                                        questionsTabAdapter.addAll(questionList);
                                    }
                                }
                        );
                    }
                }, null));

                add(ComingSoonFragment.newInstance("Search for friends coming soon!"));
                add(ComingSoonFragment.newInstance("Search for tags coming soon!"));
//            TODO
//              add(BoolioListFragment.getInstance(friendsTabAdapter, new QuestionsPullInterface() {
//                @Override
//                public void pullQuestions() {
//                    // TODO
//                    BoolioServer.getInstance(context).getUserAnswered(
//                            null,
//                            new QuestionsCallback() {
//                                @Override
//                                public void handleQuestions(List<Question> questionList) {
//
//                                }
//                            }
//                    );
//                }
//            }, runnable));
//            add(BoolioListFragment.newInstance(catergoriesTabAdapter, new QuestionsPullInterface() {
//                @Override
//                public void pullQuestions() {
//                    // TODO
//                    BoolioServer.getInstance(context).getUserAsked(
//                            null,
//                            new QuestionsCallback() {
//                                @Override
//                                public void handleQuestions(List<Question> questionList) {
//                                }
//                            }
//                    );
//                }
//            }, runnable));
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
                    categoriesTab.setAlpha(.25f);
                } else if (position == 1) {
                    questionsTab.setAlpha(.25f);
                    friendsTab.setAlpha(1f);
                    categoriesTab.setAlpha(.25f);
                } else {
                    questionsTab.setAlpha(.25f);
                    friendsTab.setAlpha(.25f);
                    categoriesTab.setAlpha(1f);
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
        questionsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });

        friendsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });

        categoriesTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });
    }
}
