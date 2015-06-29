package io.boolio.android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Comparator;
import java.util.List;

import io.boolio.android.MainActivity;
import io.boolio.android.R;
import io.boolio.android.adapters.BoolioQuestionAdapter;
import io.boolio.android.animation.AnimationHelper;
import io.boolio.android.custom.EnhancedListView;
import io.boolio.android.custom.PullToRefreshView;
import io.boolio.android.custom.ScrollingListView;
import io.boolio.android.gcm.GCMService;
import io.boolio.android.helpers.tracking.EventTracker;
import io.boolio.android.helpers.tracking.TrackEvent;
import io.boolio.android.models.Question;
import io.boolio.android.network.clients.BoolioQuestionClient;
import io.boolio.android.network.clients.BoolioUserClient;
import io.boolio.android.network.helpers.BoolioCallback;
import io.boolio.android.network.helpers.DefaultBoolioCallback;
import io.boolio.android.network.BoolioData;

/**
 * Created by Chris on 4/16/15.
 */
public class FeedFragment extends BoolioFragment {
    static FeedFragment instance;
    final public static int ORDER = 0;

    final private static int REFRESH_DELAY = 500;
    final private static int QUESTION_LIMIT = 10;


    ScrollingListView.PullQuestionListener pullQuestionListener = new ScrollingListView.PullQuestionListener() {
        @Override
        public void pullQuestion() {
            if (questionAdapter != null && questionAdapter.isEmpty())
                showBear(false);
            BoolioQuestionClient.api().getQuestionFeed(
                BoolioData.keys("count", "prevSeenQuestions")
                    .values(QUESTION_LIMIT, questionAdapter.getQuestionIds())
                , new BoolioCallback<List<Question>>() {
                    @Override
                    public void handle(final List<Question> resObj) {
                        pullToRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                questionAdapter.addAll(resObj);
                                questionAdapter.sort(new Comparator<Question>() {
                                    @Override
                                    public int compare(Question lhs, Question rhs) {
                                        return rhs.dateCreated.compareTo(lhs.dateCreated);
                                    }

                                    @Override
                                    public boolean equals(Object object) {
                                        return false;
                                    }
                                });
                                questionAdapter.onDataSetChanged();
                                pullToRefreshLayout.setRefreshing(false);
                                gifLoading.setVisibility(View.GONE);
                                GCMService.clearFeedUpdate(activity);
                            }
                        }, REFRESH_DELAY);
                    }
                });
        }
    };

    PullToRefreshView pullToRefreshLayout;
    BoolioQuestionAdapter questionAdapter;
    View gifLoading, emptyBear, headerBar;

    public static FeedFragment getInstance() {
        instance = new FeedFragment();
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public void refreshPage() {
        pullQuestionListener.pullQuestion();
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
        pullToRefreshLayout.setRefreshDrawables(R.drawable.sky, R.drawable.pulldownbear, R.drawable.sun);
        pullToRefreshLayout.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EventTracker.getInstance(activity).track(TrackEvent.PULL_DOWN);
                pullQuestionListener.pullQuestion();
            }
        });

        ScrollingListView listView = (ScrollingListView) rootView.findViewById(R.id.question_feed);
        questionAdapter = new BoolioQuestionAdapter(activity);
        setupListView(listView);

        pullQuestionListener.pullQuestion();
        return rootView;
    }


    private void setupListView(final ScrollingListView scrollingListView) {
        /** Scrolling List View With Dismiss and Undo **/
        questionAdapter.setOnDataSetChanged(new Runnable() {
            @Override
            public void run() {
                if (questionAdapter != null)
                    showBear(questionAdapter.isEmpty());
            }
        });
        scrollingListView.setAdapter(questionAdapter);
        scrollingListView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView enhancedListView, final int i) {
                final Question question = questionAdapter.remove(i);
                questionAdapter.onDataSetChanged();
                EventTracker.getInstance(activity).trackQuestion(TrackEvent.ANSWER_QUESTION, question, "skipped");
                BoolioUserClient.api().skipQuestion(BoolioData.create().add("questionId", question._id), new DefaultBoolioCallback());
                return new EnhancedListView.Undoable() {
                    @Override
                    public void undo() {
                        EventTracker.getInstance(activity).trackQuestion(TrackEvent.ANSWER_QUESTION, question, "undo");
                        questionAdapter.insert(question, i);
                        questionAdapter.onDataSetChanged();
                        BoolioUserClient.api().unskipQuestion(question, new DefaultBoolioCallback());
                    }
                };
            }
        });
        scrollingListView.setUndoHideDelay(500);
        scrollingListView.enableSwipeToDismiss();
        scrollingListView.setPullQuestionListener(pullQuestionListener);
        scrollingListView.setScrollChangeListener(new ScrollingListView.ScrollChangeListener() {
            @Override
            public void onScroll(boolean isScrollingUp) {
                ((MainFragment) getParentFragment()).showNavBar(isScrollingUp);
            }
        });
    }

    private void showBear(boolean empty) {
        if (empty) {
            AnimationHelper.getInstance(activity).animateViewFadeIn(emptyBear);
            ((MainFragment) getParentFragment()).showNavBar(true);
        } else
            emptyBear.setVisibility(View.GONE);
    }
}
