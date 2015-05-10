package io.boolio.android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.MainActivity;
import io.boolio.android.R;
import io.boolio.android.adapters.BoolioQuestionAdapter;
import io.boolio.android.animation.AnimationHelper;
import io.boolio.android.callbacks.QuestionsCallback;
import io.boolio.android.custom.EnhancedListView;
import io.boolio.android.custom.PullToRefreshView;
import io.boolio.android.custom.ScrollingListView;
import io.boolio.android.gcm.GCMService;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.models.Question;
import io.boolio.android.network.ServerFeed;
import io.boolio.android.network.ServerUser;

/**
 * Created by Chris on 4/16/15.
 */
public class FeedFragment extends BoolioFragment {
    final public static int ORDER = 0;
    final private static int REFRESH_DELAY = 500;
    static FeedFragment instance;

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
                    emptyBear.setVisibility(questionAdapter.getCount() == 0 ? View.VISIBLE : View.GONE);
                    GCMService.clearFeedUpdate(activity);
                }
            }, REFRESH_DELAY);
        }
    };

    PullToRefreshView pullToRefreshLayout;
    BoolioQuestionAdapter questionAdapter;
    List<String> prevSeenQuestions;
    View gifLoading, emptyBear;
    View headerBar;
    ScrollingListView.ScrollChangeListener scrollListener;

    public static FeedFragment getInstance(ScrollingListView.ScrollChangeListener scrollListener) {
        instance = new FeedFragment();
        instance.scrollListener = scrollListener;
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        prevSeenQuestions = new ArrayList<>();
        this.activity = (MainActivity) activity;
    }

    @Override
    public void refreshPage() {
        pullQuestions();
        if (questionAdapter.getCount() == 0) {
            gifLoading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        gifLoading = rootView.findViewById(R.id.gif_loading);
        emptyBear = rootView.findViewById(R.id.empty_list_message);
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
        questionAdapter = new BoolioQuestionAdapter(activity);
        setupListView(listView);

        BoolioUserHandler.getInstance(activity).setUserCallback(new Runnable() {
            @Override
            public void run() {
                pullQuestions();
            }
        });

        instance.pullQuestions();
        return rootView;
    }

    private void pullQuestions() {
        emptyBear.setVisibility(View.GONE);
        ServerFeed.getInstance(activity).getQuestionFeed(prevSeenQuestions, callback);
    }

    private void setupListView(final ScrollingListView scrollingListView) {
        /** Scrolling List View With Dismiss and Undo **/
        questionAdapter.setOnEmpty(new Runnable() {
            @Override
            public void run() {
                AnimationHelper.getInstance(activity).animateViewFadeIn(emptyBear);
            }
        });
        scrollingListView.setAdapter(questionAdapter);
        scrollingListView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView enhancedListView, final int i) {
                final Question question = questionAdapter.remove(i);
                questionAdapter.notifyDataSetChanged();
                ServerUser.getInstance(activity).skipQuestion(question);
                return new EnhancedListView.Undoable() {
                    @Override
                    public void undo() {
                        questionAdapter.insert(question, i);
                        questionAdapter.notifyDataSetChanged();
                        ServerUser.getInstance(activity).unskipQuestion(question);
                    }
                };
            }
        });
        scrollingListView.setUndoHideDelay(500);
        scrollingListView.enableSwipeToDismiss();
        scrollingListView.setScrollChangeListener(new ScrollingListView.ScrollChangeListener() {
            @Override
            public void onScroll(boolean isScrollingUp) {
                ((MainFragment) getParentFragment()).showNavBar(isScrollingUp);
            }
        });
    }
}
