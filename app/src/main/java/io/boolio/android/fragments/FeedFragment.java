package io.boolio.android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.MainActivity;
import io.boolio.android.R;
import io.boolio.android.adapters.BoolioQuestionAdapter;
import io.boolio.android.callbacks.QuestionsCallback;
import io.boolio.android.custom.PullToRefreshView;
import io.boolio.android.custom.ScrollingListView;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.models.Question;
import io.boolio.android.network.BoolioServer;

/**
 * Created by Chris on 4/16/15.
 */
public class FeedFragment extends BoolioFragment {
    final public static int REFRESH_DELAY = 2000;
    QuestionsCallback callback = new QuestionsCallback() {
        @Override
        public void handleQuestions(final List<Question> questionList) {
            pullToRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    questionAdapter.clear();
                    questionAdapter.addAll(questionList);
                    questionAdapter.notifyDataSetChanged();
                    pullToRefreshLayout.setRefreshing(false);
                    gifLoading.setVisibility(View.GONE);
                    if (questionAdapter.getCount() == 0) {
                        loadingMessage.setVisibility(View.VISIBLE);
                        context.showNavBar(true);
                    }
                }
            }, REFRESH_DELAY);
        }
    };
    static FeedFragment instance;
    MainActivity context;
    PullToRefreshView pullToRefreshLayout;
    BoolioQuestionAdapter questionAdapter;
    List<String> prevSeenQuestions;
    View gifLoading, loadingMessage;
    View headerBar;

    public static FeedFragment getInstance() {
        if (instance == null)
            instance = new FeedFragment();
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        prevSeenQuestions = new ArrayList<>();
        context = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        gifLoading = rootView.findViewById(R.id.gif_loading);
        loadingMessage = rootView.findViewById(R.id.empty_list_message);
        headerBar = rootView.findViewById(R.id.header_bar);

        pullToRefreshLayout = (PullToRefreshView) rootView.findViewById(R.id.ptr_layout);
        pullToRefreshLayout.setRefreshDrawables(R.drawable.sky, R.drawable.bear, R.drawable.sun);
        pullToRefreshLayout.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullQuestions();
            }
        });

        ScrollingListView listView = (ScrollingListView) rootView.findViewById(R.id.question_feed);
        questionAdapter = new BoolioQuestionAdapter(context);
        listView.setAdapter(questionAdapter);
        listView.setScrollChangeListener(new ScrollingListView.ScrollChangeListener() {
            @Override
            public void onScroll(boolean isScrollingUp) {
                context.showNavBar(isScrollingUp);
            }
        });

        BoolioUserHandler.getInstance(context).setUserCallback(new Runnable() {
            @Override
            public void run() {
                pullQuestions();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (questionAdapter.getCount() == 0) {
            gifLoading.setVisibility(View.VISIBLE);
        }
        pullQuestions();
    }

    private void pullQuestions() {
        if (BoolioUserHandler.getInstance(context).getUser().userId == null) {
            Log.e("Pulling Questions", "UserId is null");
            return;
        }
        loadingMessage.setVisibility(View.GONE);
        BoolioServer.getInstance(context).getQuestionFeed(prevSeenQuestions, callback);
    }
}
