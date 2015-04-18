package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.adapters.QuestionAdapter;
import io.boolio.android.callbacks.QuestionsCallback;
import io.boolio.android.custom.PullToRefreshView;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.models.Question;
import io.boolio.android.network.BoolioServer;

/**
 * Created by Chris on 4/16/15.
 */
public class FeedFragment extends BoolioFragment {
    static FeedFragment instance;
    final public static int REFRESH_DELAY = 2000;

    Context context;
    PullToRefreshView pullToRefreshLayout;
    QuestionAdapter questionAdapter;
    List<String> prevSeenQuestions;
    Runnable afterOpen;

    public static FeedFragment getInstance() {
        if (instance == null)
            instance = new FeedFragment();
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        prevSeenQuestions = new ArrayList<>();
        context = activity;
    }

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
                }
            }, REFRESH_DELAY);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        pullToRefreshLayout = (PullToRefreshView) rootView.findViewById(R.id.ptr_layout);
        pullToRefreshLayout.setRefreshDrawables(R.drawable.sky, R.drawable.bear, R.drawable.sun);
        pullToRefreshLayout.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullQuestions();
            }
        });


        ListView listView = (ListView) rootView.findViewById(R.id.question_feed);
        questionAdapter = new QuestionAdapter(context, R.layout.item_question);
        BoolioUserHandler.getInstance(context).setUserCallback(new Runnable() {
            @Override
            public void run() {
                pullQuestions();
            }
        });
        listView.setAdapter(questionAdapter);
        
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (afterOpen != null)
            afterOpen.run();
    }

    @Override
    public void onResume() {
        super.onResume();
        pullQuestions();
        if (afterOpen != null)
            afterOpen.run();
    }

    private void pullQuestions() {
        BoolioServer.getInstance(context).getQuestionFeed(prevSeenQuestions, callback);
    }
}
