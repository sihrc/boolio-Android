package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.adapters.BoolioQuestionAdapter;
import io.boolio.android.callbacks.QuestionsCallback;
import io.boolio.android.custom.ScrollingListView;
import io.boolio.android.models.Question;
import io.boolio.android.network.ServerFeed;

/**
 * Created by james on 4/21/15.
 */
public class SearchFragment extends BoolioFragment {
    static SearchFragment instance;
    Context context;

    EditText searchBar;
    ViewPager viewPager;
    TextView questionsTab, friendsTab, categoriesTab;
    boolean isEmpty;

    List<BoolioListFragment> fragmentList;
    ScrollingListView.ScrollChangeListener scrollChangeListener;

    BoolioQuestionAdapter questionsTabAdapter, friendsTabAdapter, categoriesTabAdapter;
    QuestionsCallback questionsCallback = new QuestionsCallback() {
        @Override
        public void handleQuestions(List<Question> questionList) {
            questionsTabAdapter.clear();
            if (!isEmpty)
                questionsTabAdapter.addAll(questionList);
            questionsTabAdapter.notifyDataSetChanged();
        }
    };

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

        searchBar = (EditText) rootView.findViewById(R.id.search_bar);
        viewPager = (ViewPager) rootView.findViewById(R.id.search_view_pager);
        questionsTab = (TextView) rootView.findViewById(R.id.search_questions_tab);
        friendsTab = (TextView) rootView.findViewById(R.id.search_friends_tab);
        categoriesTab = (TextView) rootView.findViewById(R.id.search_categories_tab);

        searchBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, 0, 0);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (viewPager.getCurrentItem() != 0 || s.length() == 0) {
                    questionsTabAdapter.clear();
                    questionsTabAdapter.notifyDataSetChanged();
                    isEmpty = true;
                    return;
                }
                isEmpty = false;
                searchServer(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        setupPager();
        setupTabOnClick();

        return rootView;
    }

    private void searchServer(String query) {
        ServerFeed.getInstance(context).searchQuestion(
                query, questionsCallback);
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
                add(BoolioListFragment.newInstance(questionsTabAdapter, null));
                add(ComingSoonFragment.newInstance("Search for friends coming soon!"));
                add(ComingSoonFragment.newInstance("Search for tags coming soon!"));
            }
        };


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
                    searchBar.setVisibility(View.GONE);
                    questionsTab.setAlpha(.25f);
                    friendsTab.setAlpha(1f);
                    categoriesTab.setAlpha(.25f);
                } else {
                    searchBar.setVisibility(View.GONE);
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
