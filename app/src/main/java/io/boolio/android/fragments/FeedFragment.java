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

    public static FeedFragment getInstance() {
        if (instance == null) {
            instance = new FeedFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        Question q = new Question();
        q.creator = "Ryan";
        q.question = "What is up?";
        q.left = "Yes";
        q.right = "No";
        Question q1 = new Question();
        q1.creator = "Ryan";
        q1.question = "What is up?";
        q1.left = "Yes";
        q1.right = "No";


        ListView listView = (ListView) rootView.findViewById(R.id.question_feed);
        QuestionAdapter questionAdapter = new QuestionAdapter(context, R.layout.question_item);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("prevSeenQuestions", "");
            jsonObject.put("id", BoolioUserHandler.getInstance(context).getUser().userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BoolioServer.getInstance(context).getQuestionFeed(questionAdapter, jsonObject);

        questionAdapter.addQuestion(q);
        questionAdapter.addQuestion(q1);
        listView.setAdapter(questionAdapter);

        return rootView;
    }
}
