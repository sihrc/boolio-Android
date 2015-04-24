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
    Runnable callback;

    // Callbacks
    QuestionsPullInterface pullInterface;

    public static BoolioListFragment newInstance(BoolioAdapter questionAdapter,
                                                 QuestionsPullInterface pullQuestions, Runnable callback) {
        BoolioListFragment fragment = new BoolioListFragment();
        fragment.questionAdapter = questionAdapter;
        fragment.pullInterface = pullQuestions;
        fragment.callback = callback;
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
        listView.setScrollChangeListener(new ScrollingListView.ScrollChangeListener() {
            @Override
            public void onScroll(boolean isScrollingUp) {
                Log.i("DebugDebug", isScrollingUp + " boolean");
                context.showNavBar(isScrollingUp);
            }
        });
        hideKeyBoard(listView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        pullInterface.pullQuestions();
    }

    private void hideKeyBoard(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v == view) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                    return true;
                }
                return false;
            }
        });
    }
}
