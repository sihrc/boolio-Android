package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.adapters.QuestionAdapter;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.models.Question;
import io.boolio.android.network.BoolioServer;

/**
 * Created by Chris on 4/16/15.
 */
public class FeedFragment extends BoolioFragment {
    static FeedFragment instance;

    Context context;
    List<String> prevSeenQuestions;


    public static FeedFragment getInstance() {
        if (instance == null) {
            instance = new FeedFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        prevSeenQuestions = new ArrayList<>();
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.question_feed);
        final QuestionAdapter questionAdapter = new QuestionAdapter(context, R.layout.question_item);
        BoolioUserHandler.getInstance(context).setUserCallback(new Runnable() {
            @Override
            public void run() {
                BoolioServer.getInstance(context).getQuestionFeed(questionAdapter, prevSeenQuestions);
            }
        });
        listView.setAdapter(questionAdapter);

        return rootView;
    }
}
