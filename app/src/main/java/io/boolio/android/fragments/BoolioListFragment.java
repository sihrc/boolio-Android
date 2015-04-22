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
import io.boolio.android.helpers.YPositionListener;

/**
 * Created by Chris on 4/21/15.
 */
public class BoolioListFragment extends BoolioFragment {
    Context context;
    QuestionAdapter questionAdapter;
    ListView listView;

    // Callbacks
    QuestionsPullInterface pullInterface;
    View.OnTouchListener disabled;
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    };

    public static BoolioListFragment newInstance(QuestionAdapter questionAdapter, View.OnTouchListener listener,
                                                 QuestionsPullInterface pullQuestions) {
        BoolioListFragment fragment = new BoolioListFragment();
        fragment.questionAdapter = questionAdapter;
        fragment.pullInterface = pullQuestions;
        fragment.disabled = listener;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_boolio_list, container, false);

        listView = (ListView) rootView.findViewById(R.id.profile_asked_feed);
        listView.setOnTouchListener(disabled);
        listView.setAdapter(questionAdapter);

        return rootView;
    }

    public void enableListView(boolean enabled) {
        listView.setOnTouchListener(enabled ? touchListener : disabled);
    }

    @Override
    public void onResume() {
        super.onResume();
        pullInterface.pullQuestions();
    }
}
