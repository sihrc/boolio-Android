package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import io.boolio.android.R;
import io.boolio.android.adapters.QuestionAdapter;
import io.boolio.android.callbacks.QuestionsPullInterface;
import io.boolio.android.callbacks.ScrollViewCallback;
import io.boolio.android.helpers.DragListener;

/**
 * Created by Chris on 4/21/15.
 */
public class BoolioListFragment extends BoolioFragment {
    Context context;
    QuestionAdapter questionAdapter;
    ListView listView;

    // Callbacks
    ScrollViewCallback scrollViewCallback;
    QuestionsPullInterface pullInterface;

    public static BoolioListFragment newInstance(QuestionAdapter questionAdapter,
                                                 ScrollViewCallback scrollViewCallback, QuestionsPullInterface pullQuestions) {
        BoolioListFragment fragment = new BoolioListFragment();
        fragment.questionAdapter = questionAdapter;
        fragment.scrollViewCallback = scrollViewCallback;
        fragment.pullInterface = pullQuestions;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_boolio_list, container, false);

        listView = (ListView) rootView.findViewById(R.id.profile_asked_feed);
        listView.setAdapter(questionAdapter);

        final DragListener dragListener = new DragListener(context);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dragListener.onTouch(v, event);
                scrollViewCallback.scroll(dragListener.getDy());

                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        pullInterface.pullQuestions();
    }
}
