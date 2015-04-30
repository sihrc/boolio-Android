package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;

import io.boolio.android.MainActivity;
import io.boolio.android.R;
import io.boolio.android.adapters.BoolioAdapter;
import io.boolio.android.callbacks.QuestionsPullInterface;
import io.boolio.android.custom.ScrollingListView;

/**
 * Created by Chris on 4/21/15.
 */
public class BoolioListFragment extends BoolioFragment {
    MainActivity context;
    BoolioAdapter questionAdapter;
    ScrollingListView listView;
    ScrollingListView.ScrollChangeListener scrollChangeListener;
    Runnable callback;

    public static BoolioListFragment newInstance(BoolioAdapter questionAdapter,
                                                 ScrollingListView.ScrollChangeListener scrollChangeListener) {
        BoolioListFragment fragment = new BoolioListFragment();
        fragment.questionAdapter = questionAdapter;
        fragment.scrollChangeListener = scrollChangeListener;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = (MainActivity) activity;
    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_boolio_list, container, false);

        listView = (ScrollingListView) rootView.findViewById(R.id.profile_asked_feed);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int prev = 1;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (callback != null && scrollState == SCROLL_STATE_IDLE && prev == 0) {
                    callback.run();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                prev = firstVisibleItem;
            }
        });
        listView.setAdapter(questionAdapter);
        listView.setScrollChangeListener(scrollChangeListener);
        hideKeyBoard(listView);

        return rootView;
    }
}
